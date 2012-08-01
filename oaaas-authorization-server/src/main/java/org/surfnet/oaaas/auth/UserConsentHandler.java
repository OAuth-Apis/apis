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

import javax.servlet.ServletRequest;
import javax.ws.rs.core.Response;

import org.surfnet.oaaas.auth.principal.RolesPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.resource.TokenResource;

/**
 * Responsible for handling user consent.
 * 
 */
public interface UserConsentHandler {

  /**
   * The constant used to keep 'session' state when we give flow control to the
   * {@link UserConsentHandler}. Part of the contract is that we expect to get
   * the value back when consent is handled.
   */
  public static final String AUTH_STATE = AbstractAuthenticator.AUTH_STATE;

  public static final String USER_OAUTH_APPROVAL = "user_oauth_approval";
  
  public static final String GRANTED_SCOPES = "granted_scopes";

  /**
   * Is consent required. Note that the {@link Client} is present on the
   * {@link AuthorizationRequest}.
   * 
   * 
   * @param authorizationRequest
   *          the request made by the Client
   * @return boolean if we need to proceed to
   *         {@link #handleConsent(AuthorizationRequest, RolesPrincipal)}
   */
  boolean isConsentRequired(AuthorizationRequest authorizationRequest);

  /**
   * Implement this method to perform the actual user consent handling. The
   * default implementation displays a consent screen using FreeMarker, but as
   * the return value is a {@link Response} anything is possible using a 302.
   * 
   * The control must be returned to the {@link TokenResource} at one time using
   * a POST to /authorize. The contract is:
   * <p>
   * get consent from the user using the {@link Client} attributes on the
   * {@link AuthorizationRequest} and POST back to /authorize with:
   * </p>
   * <ul>
   * <li>the {@link UserConsentHandler#USER_OAUTH_APPROVAL} request parameter set to
   * true</li>
   * <li>the {@link UserConsentHandler#AUTH_STATE} request parameter with the original value</li>
   * <li>the {@link UserConsentHandler#GRANTED_SCOPES} request parameters with the granted scopes</li>
   * </ul>
   * 
   * @param authorizationRequest
   *          the {@link AuthorizationRequest}
   * @param rolesPrincipal
   *          the {@link RolesPrincipal}
   * @return a {@link Response} object
   */

  Response handleConsent(AuthorizationRequest authorizationRequest, RolesPrincipal rolesPrincipal);

}
