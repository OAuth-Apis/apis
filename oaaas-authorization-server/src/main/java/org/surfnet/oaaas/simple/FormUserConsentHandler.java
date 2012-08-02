/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.simple;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.surfnet.oaaas.auth.AbstractUserConsentHandler;
import org.surfnet.oaaas.auth.Client;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

import com.yammer.dropwizard.views.View;
import com.yammer.dropwizard.views.ViewMessageBodyWriter;

/**
 * {@link AbstractUserConsentHandler} that uses a simple freemarker form to ask
 * the user for consent
 * 
 */
@Named("formConsentHandler")
public class FormUserConsentHandler extends AbstractUserConsentHandler {

  private static final String USER_OAUTH_APPROVAL = "user_oauth_approval";


  /*
   * (non-Javadoc)
   * 
   * @see
   * org.surfnet.oaaas.auth.AbstractUserConsentHandler#handleUserConsent(javax
   * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * javax.servlet.FilterChain, java.lang.String, java.lang.String)
   */
  @Override
  public void handleUserConsent(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri, Client client) throws IOException, ServletException {
    if (request.getMethod().equals("POST")) {
      processForm(request);
      chain.doFilter(request, response);
    } else {
      processInitial(request, response, returnUri, authStateValue, client);
    }
    
    //      http://freemarker.sourceforge.net/docs/pgui_quickstart_all.html

  }
  
  private void processInitial(HttpServletRequest request, ServletResponse response, String returnUri,
      String authStateValue, Client client) throws IOException {

    ViewMessageBodyWriter w = new ViewMessageBodyWriter(new MockHttpHeaders());
    View view = new ConsentView(super.getReturnUri(request), authStateValue, client);

    w.writeTo(view, ConsentView.class, null, null, MediaType.TEXT_HTML_TYPE, MockHttpHeaders.headersAsMap(request),
        response.getOutputStream());

  }

  private void processForm(final HttpServletRequest request) {
    if (Boolean.valueOf(request.getParameter(USER_OAUTH_APPROVAL))) {
      setAuthStateValue(request, request.getParameter("authState"));
      String[] scopes = request.getParameterValues("granted_scopes");
      setScopes(request, scopes);
    }
  }

}
