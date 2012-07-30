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

import org.surfnet.oaaas.model.AuthorizationRequest;

/**
 * Responsible for validating the OAuth2 incoming requests
 * 
 */
public interface OAuth2Validator {

  String IMPLICIT_GRANT_RESPONSE_TYPE = "token";

  String AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE = "code";

  
  enum SpecResponseCode {
    VALID_REQUEST, INVALID_REQUEST, UNAUTHORIZED_CLIENT, ACCESS_DENIED, UNSUPPORTED_RESPONSE_TYPE, INVALID_SCOPE, SERVER_ERROR;
    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  enum ValidationResponse {

    VALID("valid", "valid"), UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type",
        "The supported response_type values are 'code' and 'token'"), UNKNOWN_CLIENT_ID("unauthorized_client",
        "The client_id is unknown"), IMPLICIT_GRANT_REDIRECT_URI("invalid_request",
        "For Implicit Grant the redirect_uri parameter is required"), REDIRECT_URI_REQUIRED("invalid_request",
        "Client has no registrated redirect_uri, must provide run-time redirect_uri"), REDIRCT_URI_NOT_VALID(
        "invalid_request", "The redirect_uri does not equals any of the registrated redirect_uri values"), REDIRCT_URI_NOT_URI(
        "invalid_request", "The redirect_uri is not a valid URL"), SCOPE_NOT_VALID("invalid_scope",
        "Invalid/unknown scope provided");
    private String value;
    private String description;

    private ValidationResponse(String value, String description) {
      this.value = value;
      this.description = description;
    }

    boolean isError() {
      return this.equals(VALID);
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }
  }

  ValidationResponse validate(AuthorizationRequest request);

}
