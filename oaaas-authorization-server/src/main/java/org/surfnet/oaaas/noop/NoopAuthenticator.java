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

package org.surfnet.oaaas.noop;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.SimplePrincipal;

/**
 * A mimimalistic implementation of AbstractAuthenticator that contains no authentication but only fulfills the
 * contract of Authenticators.
 * Useful for testing and demonstration purposes only, of course not safe for production.
 */
public class NoopAuthenticator extends AbstractAuthenticator {

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.AbstractAuthenticator#authenticate(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain, java.lang.String, java.lang.String)
   */
  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException {
    super.setAuthStateValue(request, authStateValue);
    super.setPrincipal(request, new SimplePrincipal("noop"));
    chain.doFilter(request, response);
  }

  
  
}
