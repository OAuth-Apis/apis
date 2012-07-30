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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.ClientRepository;

/**
 * Implementation of {@link OAuth2Validator}
 * 
 */
@Named
public class OAuth2ValidatorImpl implements OAuth2Validator {

  private static final Set<String> RESPONSE_TYPES = new HashSet<String>();

  {
    RESPONSE_TYPES.add(IMPLICIT_GRANT_RESPONSE_TYPE);
    RESPONSE_TYPES.add(AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE);
  }

  @Inject
  private ClientRepository clientRepository;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.surfnet.oaaas.auth.OAuth2Validator#validate(org.surfnet.oaaas.model
   * .AuthorizationRequest)
   */
  @Override
  public ValidationResponse validate(AuthorizationRequest request) {
    String responseType = request.getResponseType();
    if (StringUtils.isBlank(responseType) || !RESPONSE_TYPES.contains(responseType)) {
      return ValidationResponse.UNSUPPORTED_RESPONSE_TYPE;
    }

    String clientId = request.getClientId();
    Client client = StringUtils.isBlank(clientId) ? null : clientRepository.findByName(clientId);
    if (client == null) {
      return ValidationResponse.UNKNOWN_CLIENT_ID;
    }
    request.setClient(client);
    String uris = client.getRedirectUri();
    String redirectUri = request.getRedirectUri();
    if (StringUtils.isBlank(redirectUri)) {
      if (responseType.equals(IMPLICIT_GRANT_RESPONSE_TYPE)) {
        return ValidationResponse.IMPLICIT_GRANT_REDIRECT_URI;
      } else if (StringUtils.isBlank(uris)) {
        return ValidationResponse.REDIRECT_URI_REQUIRED;
      } else {
        String[] split = uris.split(",");
        request.setRedirectUri(split[0].trim());
      }
    } else if (!AuthenticationFilter.isValidUrl(redirectUri)) {
      return ValidationResponse.REDIRCT_URI_NOT_URI;
    } else if (!StringUtils.isBlank(uris) && !Arrays.asList(uris.split(",")).contains(redirectUri)) {
      return ValidationResponse.REDIRCT_URI_NOT_VALID;
    }
    if (!StringUtils.isBlank(request.getScope())) {
      String[] scopes = request.getScope().split(",");
      List<String> clientScopes = Arrays.asList(client.getScopes().split(","));
      for (String scope : scopes) {
        if (!clientScopes.contains(scope)) {
          return ValidationResponse.SCOPE_NOT_VALID;
        }
      } 
    } else {
      request.setScope(client.getScopes());
    }
    return ValidationResponse.VALID;
  }

}
