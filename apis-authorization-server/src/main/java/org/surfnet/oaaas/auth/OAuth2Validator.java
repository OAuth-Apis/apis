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

import org.surfnet.oaaas.model.AccessTokenRequest;
import org.surfnet.oaaas.model.AuthorizationRequest;

/**
 * Responsible for validating the OAuth2 incoming requests
 * 
 */
public interface OAuth2Validator {

  String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

  String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

  String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

  String IMPLICIT_GRANT_RESPONSE_TYPE = "token";

  String AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE = "code";

  String CLIENT_CREDENTIALS_GRANT_RESPONSE_TYPE = GRANT_TYPE_CLIENT_CREDENTIALS;

  String BEARER = "bearer";

  /**
   * Validate the {@link AuthorizationRequest}
   * 
   * @param request
   *          the Authorization Request with the data send from the client
   * @return A {@link ValidationResponse} specifying what is wrong (if any)
   */
  ValidationResponse validate(AuthorizationRequest request);

  /**
   * Validate the {@link AccessTokenRequest}
   * 
   * @param request
   *          the AccessTokenRequest with the data send from the client
   * @return A {@link ValidationResponse} specifying what is wrong (if any)
   */
  ValidationResponse validate(AccessTokenRequest request);

  /**
   * 
   * See <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2#section-5.2"> the spec</a>
   *
   */
  enum ValidationResponse {

    VALID("valid", "valid"),

    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type", String.format(
        "The supported response_type values are '%s' and '%s'", IMPLICIT_GRANT_RESPONSE_TYPE,
        AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE)),

    UNKNOWN_CLIENT_ID("unauthorized_client", "The client_id is unknown"),

    IMPLICIT_GRANT_REDIRECT_URI("invalid_request", "For Implicit Grant the redirect_uri parameter is required"),

    REDIRECT_URI_REQUIRED("invalid_request",
        "Client has no registered redirect_uri, must provide run-time redirect_uri"),

    REDIRCT_URI_NOT_VALID("invalid_request",
        "The redirect_uri does not equal any of the registered redirect_uri values"),

    REDIRCT_URI_NOT_URI("invalid_request", "The redirect_uri is not a valid URL"),

    REDIRECT_URI_DIFFERENT("invaid_request","The redirect_uri does not match the initial authorization request"),
    
    SCOPE_NOT_VALID("invalid_scope", "The requested scope is invalid, unknown, malformed, " +
        "or exceeds the scope granted by the resource owner."),

    IMPLICIT_GRANT_NOT_PERMITTED("unsupported_response_type", "The client has no permisssion for implicit grant"),

    CLIENT_CREDENTIALS_NOT_PERMITTED("unauthorized_client", "The client has no permisssion for client credentials"),

    REDIRECT_URI_FRAGMENT_COMPONENT("invalid_request",
        "The redirect_uri endpoint must not include a fragment component"),

    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type", String.format("The supported grant_type values are '%s' and '%s'",
        GRANT_TYPE_AUTHORIZATION_CODE, GRANT_TYPE_REFRESH_TOKEN)),

    INVALID_GRANT_AUTHORIZATION_CODE("invalid_grant", "The authorization code is invalid"),

    INVALID_GRANT_REFRESH_TOKEN("invalid_grant", "The refresh token is invalid");

    private String value;
    private String description;

    private ValidationResponse(String value, String description) {
      this.value = value;
      this.description = description;
    }

    public boolean valid() {
      return this.equals(VALID);
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }
  }

}
