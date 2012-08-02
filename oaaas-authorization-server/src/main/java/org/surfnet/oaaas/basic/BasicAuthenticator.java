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

package org.surfnet.oaaas.basic;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

/**
 * Authenticator that uses HTTP Basic Authentication.
 */
@Named("basicAuthenticator")
public class BasicAuthenticator extends AbstractAuthenticator {

  private String realm = "user: pietje, pass: puk";


  private boolean authenticate(UserPassCredentials credentials) {
    // TODO: actually perform authentication at user repository
    return "pietje".equals(credentials.getUsername()) && "puk".equals(credentials.getPassword());
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.AbstractAuthenticator#authenticate(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain, java.lang.String, java.lang.String)
   */
  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException {
    final UserPassCredentials credentials = new UserPassCredentials(request.getHeader("Authorization"));


    if (authenticate(credentials)) {
      // TODO: see below, need to implement transfer of auth state with client
      // setAuthStateValue(request, ....);
      setPrincipal(request, new SimplePrincipal(credentials.getUsername()));
      chain.doFilter(request, response);
    } else {
      // TODO: transfer auth state somehow... cookie?
      response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization required");
      response.flushBuffer();
    }
    
  }
}
