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
package org.surfnet.oaaas.authentication;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

/**
 * {@link AbstractAuthenticator} that redirects to a form. Note that other
 * implementations can go wild because they have access to the
 * {@link HttpServletRequest} and {@link HttpServletResponse}.
 * 
 */
@Named("formAuthenticator")
public class FormLoginAuthenticator extends AbstractAuthenticator {

  @Override
  public boolean canCommence(HttpServletRequest request) {
    return request.getMethod().equals("POST")
        && request.getParameter(AUTH_STATE) != null
        && request.getParameter("username") != null;
  }

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
      String authStateValue) throws IOException, ServletException {
    request.setAttribute(AUTH_STATE, authStateValue);
    request.setAttribute("actionUri", returnUri);
    request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
  }

  private void processForm(final HttpServletRequest request) {
    /*
     * Hook for actually go the database, ldap, whatever to perform
     * authentication
     */
    setAuthStateValue(request, request.getParameter(AUTH_STATE));
    setPrincipal(request, new SimplePrincipal(request.getParameter("username")));
  }

}
