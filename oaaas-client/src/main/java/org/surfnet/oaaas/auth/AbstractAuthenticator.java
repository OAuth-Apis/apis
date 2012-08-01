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

package org.surfnet.oaaas.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.surfnet.oaaas.auth.principal.RolesPrincipal;

/**
 * To be implemented by various authentication methods.
 */
public abstract class AbstractAuthenticator implements Filter {

  /**
   * Constant to set the return uri when authentication is done
   */
  public static final String RETURN_URI = "returnUri";

  /**
   * The constant used to keep 'session' state when we give flow control to the
   * authenticator filter. Part of the contract with the authenticator Filter is
   * that we expect to get the value back when authentication is done.
   */
  public static final String AUTH_STATE = "AUTH_STATE";


  /**
   * The constant that contains the principal, set by concrete authenticators and consumed by
   * the authorization endpoint.
   */
  public static final String PRINCIPAL = "PRINCIPAL";

  /**
   * Implement this method to perform the actual authentication.
   * Use {@link org.surfnet.oaaas.basic.BasicAuthenticator BasicAuthenticator}
   * or {@link org.surfnet.oaaas.simple.FormLoginAuthenticator FormLoginAuthenticator} as an example.
   *
   * In general, the contract is:
   *   <p>assert that the user is authenticated. You can use the request and response for this. When
   *   not yet authenticated:</p>
   *   <ul>
   *    <li>use {@link #getAuthStateValue(javax.servlet.ServletRequest)} to pass-around for user agent communication </li>
   *    <li>use {@link #getReturnUri(javax.servlet.ServletRequest)} if you need to step out and return to the current
   *    location
   *   </ul>
   *   <p>When authenticated:</p>
   *   <ul>
   *     <li>set the authState attribute, by calling {@link #setAuthStateValue(javax.servlet.ServletRequest, String)}</li>
   *     <li>set the principal attribute, by calling {@link #setPrincipal(ServletRequest, RolesPrincipal)}  </li>
   *    <li>call chain.doFilter(request, response) to let the flow continue..
   *   </ul>
   * @param request the ServletRequest
   * @param response the ServletResponse
   * @param chain the original http servlet filter chain
   */
  @Override
  public abstract void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  public final String getAuthStateValue(ServletRequest request) {
    return (String) request.getAttribute(AUTH_STATE);
  }

  public final String getReturnUri(ServletRequest request) {
    return (String) request.getAttribute(RETURN_URI);
  }

  protected final void setAuthStateValue(ServletRequest request, String authState) {
    request.setAttribute(AUTH_STATE, authState);
  }

  /**
   * Set the given principal on the request.
   * @param request the original ServletRequest
   * @param principal the Principal to set.
   */
  protected final void setPrincipal(ServletRequest request, RolesPrincipal principal) {
    request.setAttribute(PRINCIPAL, principal);
  }

}
