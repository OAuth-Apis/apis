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
package org.surfnet.oaaas.auth;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.model.Client;

/**
 * Responsible for handling user consent.
 *
 */
public abstract class AbstractUserConsentHandler extends AbstractFilter {

  /**
   * The constant that contains the scopes, set by concrete userConsentHandlers
   * and consumed by the authorization endpoint.
   */
  public static final String GRANTED_SCOPES = "GRANTED_SCOPES";

  /**
   * Constant to get the Client when the control should be returned to the
   * implementor
   */
  public static final String CLIENT = "CLIENT";

  /**
   *
   * Get the Client from the request context to use in handling user consent
   *
   * @param request
   *          the {@link ServletRequest}
   * @return the Client which is asking for consent
   */
  public final Client getClient(ServletRequest request) {
    return (Client) request.getAttribute(CLIENT);
  }

  @Override
  public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException {
    handleUserConsent((HttpServletRequest) request, (HttpServletResponse) response, chain, getAuthStateValue(request),
        getReturnUri(request), getClient(request));
  }

  /**
   * Implement this method to perform the actual authentication. Use
   * {@link org.surfnet.oaaas.consent.FormUserConsentHandler
   * FormUserConsentHandler} as an example.
   *
   * In general, the contract is:
   * <p>
   * assert that the user has granted consent. You can use the request and
   * response for this. When not yet granted consent:
   * </p>
   * <ul>
   * <li>use {@link #getAuthStateValue(javax.servlet.ServletRequest)} to
   * pass-around for user agent communication</li>
   * <li>use {@link #getReturnUri(javax.servlet.ServletRequest)} if you need to
   * step out and return to the current location</li>
   * <li>use {@link #getClient(javax.servlet.ServletRequest)} for accessing the
   * {@link Client} data</li>
   * </ul>
   * <p>
   * When consent granted:
   * </p>
   * <ul>
   * <li>set the authState attribute, by calling
   * {@link #setAuthStateValue(javax.servlet.ServletRequest, String)}</li>
   * <li>set the scopes (optional) the user has given consent for, by calling
   * {@link #setScopes}</li>
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
   * @param client
   *          the Client wished to obtain an access token
   */
  public abstract void handleUserConsent(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri, Client client) throws IOException, ServletException;

  /**
   * Set the granted scopes of the consent on the request. Note: this optional.
   *
   * @param request
   *          the original ServletRequest
   * @param scopes
   *          the {@link String[]} scopes.
   */
  protected final void setGrantedScopes(ServletRequest request, String[] scopes) {
    request.setAttribute(GRANTED_SCOPES, scopes);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

}
