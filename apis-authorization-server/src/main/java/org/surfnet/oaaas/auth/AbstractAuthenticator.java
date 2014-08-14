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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * To be implemented by various authentication methods.
 */
public abstract class AbstractAuthenticator extends AuthorizationSupport {

  /**
   * The constant that contains the principal, set by concrete authenticators
   * and consumed by the authorization endpoint.
   */
  public static final String PRINCIPAL = "PRINCIPAL";

  /**
   * Constant to get the return uri when the control should be returned to the
   * implementor
   */
  public static final String RETURN_URI = "RETURN_URI";

  /**
   * The constant used to keep 'session' state when we give flow control to the
   * authenticator filter. Part of the contract with the authenticator Filter is
   * that we expect to get the value back when authentication is done.
   */
  public static final String AUTH_STATE = "AUTH_STATE";

  /**
   * Implement this method to state whether the given request is a continuation that can be handled.
   * This method will be called for every consecutive request after the initial one.<br />
   * Returning true means that the request is part of an ongoing authentication.<br />
   * Returning false indicates to the framework that the request is not known.<br />
   * Typically this can be determined by the http method or one or more request parameters/attributes being present.
   *
   * @param request the HttpServletRequest
   */
  public abstract boolean canCommence(HttpServletRequest request);


  /**
   * Implement this method to perform the actual authentication. Use
   * {@link FormLoginAuthenticator} as an example.
   * 
   * <ul>
   * <li>set the authState attribute, by calling
   * {@link #setAuthStateValue(javax.servlet.ServletRequest, String)}</li>
   * <li>set the principal attribute, by calling
   * {@link #setPrincipal(ServletRequest, AuthenticatedPrincipal)}</li>
   * <li>call chain.doFilter(request, response) to let the flow continue..
   * </ul>
   * 
   * @param request
   *          the ServletRequest
   * @param response
   *          the ServletResponse
   * @param chain
   *          the original http servlet filter chain
   * @param authStateValue
   *          the authState nonce to set back on the {@link ServletRequest} when
   *          done
   * @param returnUri
   *          the startpoint of the chain if you want to return from a form or
   *          other (external) component
   */
  public abstract void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException;

  /**
   * Set the given principal on the request.
   * 
   * @param request
   *          the original ServletRequest
   * @param principal
   *          the Principal to set.
   */
  protected final void setPrincipal(ServletRequest request, AuthenticatedPrincipal principal) {
    request.setAttribute(PRINCIPAL, principal);
  }

}
