/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.surfnet.oaaas.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import com.yammer.dropwizard.views.View;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

@Named("theAuthenticationFilter")
public class FormLoginAuthenticator extends AbstractAuthenticator {

  private String forwardUri = "/doAuthorize";

  @Override
  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;

    if (request.getMethod().equals("POST")) {
      processForm(request);
      chain.doFilter(request, response);
    } else {
      processInitial(request, response);

    }
  }

  private void processInitial(HttpServletRequest request, ServletResponse response) throws IOException {
    final String uri = String.format("%s?forwardUri=%s&authState=%s",
        forwardUri, getReturnUri(request), getAuthStateValue(request));

    ViewMessageBodyWriter w = new ViewMessageBodyWriter(new MockHttpHeaders());
    View view = new LoginView(getReturnUri(request), getAuthStateValue(request));

    w.writeTo(view, LoginView.class, null, null, MediaType.TEXT_HTML_TYPE,
        headersAsMap(request), response.getOutputStream());

  }

  private void processForm(final HttpServletRequest request) {
    // TODO: process POST parameters, actually perform authentication at user repository

    setAuthStateValue(request, request.getParameter("authState"));
    setPrincipal(request, new SimplePrincipal(request.getParameter("username")));
  }

  private MultivaluedMap<String,Object> headersAsMap(HttpServletRequest request) {
    MultivaluedMap<String, Object> result = new StringKeyIgnoreCaseMultivaluedMap<Object>();
    final Enumeration<String> headerNames = request.getHeaderNames();
    for (String headerName : Collections.list(headerNames)) {
      List<Object> headerValues = new ArrayList<Object>();
      for (String headerValue : Collections.list(request.getHeaders(headerName))) {
        headerValues.add(headerValue);
      }
      result.put(headerName, headerValues);
    }
    return result;
  }

  public void setForwardUri(String forwardUri) {
    this.forwardUri = forwardUri;
  }

}
