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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import com.yammer.dropwizard.views.View;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

@Named("formAuthenticator")
public class FormLoginAuthenticator extends AbstractAuthenticator {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.surfnet.oaaas.auth.AbstractAuthenticator#authenticate(javax.servlet
   * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * javax.servlet.FilterChain, java.lang.String, java.lang.String)
   */
  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException {
    if (request.getMethod().equals("POST")) {
      processForm(request);
      chain.doFilter(request, response);
    } else {
      processInitial(request, response, returnUri, authStateValue);
    }
  }

  private void processInitial(HttpServletRequest request, ServletResponse response, String returnUri,
      String authStateValue) throws IOException {

    ViewMessageBodyWriter w = new ViewMessageBodyWriter(new MockHttpHeaders());
    View view = new LoginView(super.getReturnUri(request), authStateValue);

    w.writeTo(view, LoginView.class, null, null, MediaType.TEXT_HTML_TYPE, MockHttpHeaders.headersAsMap(request),
        response.getOutputStream());

  }

  private void processForm(final HttpServletRequest request) {
    // TODO: process POST parameters, actually perform authentication at user
    // repository

    setAuthStateValue(request, request.getParameter("authState"));
    setPrincipal(request, new SimplePrincipal(request.getParameter("username")));
  }

}
