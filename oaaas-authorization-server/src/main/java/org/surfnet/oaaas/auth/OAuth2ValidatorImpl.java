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

  static {
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
  public ValidationResponse validate(AuthorizationRequest authorizationRequest) {
    try {
      validateAuthorizationRequest(authorizationRequest);

      String responseType = validateResponseType(authorizationRequest);

      Client client = validateClient(authorizationRequest);
      authorizationRequest.setClient(client);

      String redirectUri = determineRedirectUri(authorizationRequest, responseType, client);
      authorizationRequest.setRedirectUri(redirectUri);

      String scopes = determineScopes(authorizationRequest, client);
      authorizationRequest.setScopes(scopes);

    } catch (ValidationResponseException e) {
      return e.v;
    }
    return ValidationResponse.VALID;
  }

  protected String determineScopes(AuthorizationRequest authorizationRequest, Client client) {
    if (StringUtils.isBlank(authorizationRequest.getScopes())) {
      return client.getScopes();
    } else {
      String[] scopes = authorizationRequest.getScopes().split(",");
      List<String> clientScopes = Arrays.asList(client.getScopes().split(","));
      for (String scope : scopes) {
        if (!clientScopes.contains(scope)) {
          throw new ValidationResponseException(ValidationResponse.SCOPE_NOT_VALID);
        }
      }
      return authorizationRequest.getScopes();
    }
  }

  protected String determineRedirectUri(AuthorizationRequest authorizationRequest, String responseType, Client client) {
    String uris = client.getRedirectUris();
    String redirectUri = authorizationRequest.getRedirectUri();
    if (StringUtils.isBlank(redirectUri)) {
      if (responseType.equals(IMPLICIT_GRANT_RESPONSE_TYPE)) {
        throw new ValidationResponseException(ValidationResponse.IMPLICIT_GRANT_REDIRECT_URI);
      } else if (StringUtils.isBlank(uris)) {
        throw new ValidationResponseException(ValidationResponse.REDIRECT_URI_REQUIRED);
      } else {
        String[] split = uris.split(",");
        return split[0].trim();
      }
    } else if (!AuthenticationFilter.isValidUrl(redirectUri)) {
      throw new ValidationResponseException(ValidationResponse.REDIRCT_URI_NOT_URI);
    } else if (!StringUtils.isBlank(uris) && !Arrays.asList(uris.split(",")).contains(redirectUri)) {
      throw new ValidationResponseException(ValidationResponse.REDIRCT_URI_NOT_VALID);
    }
    return redirectUri;
  }

  protected Client validateClient(AuthorizationRequest authorizationRequest) {
    String clientId = authorizationRequest.getClientId();
    Client client = StringUtils.isBlank(clientId) ? null : clientRepository.findByClientId(clientId);
    if (client == null) {
      throw new ValidationResponseException(ValidationResponse.UNKNOWN_CLIENT_ID);
    }
    return client;
  }

  protected String validateResponseType(AuthorizationRequest authorizationRequest) {
    String responseType = authorizationRequest.getResponseType();
    if (StringUtils.isBlank(responseType) || !RESPONSE_TYPES.contains(responseType)) {
      throw new ValidationResponseException(ValidationResponse.UNSUPPORTED_RESPONSE_TYPE);
    }
    return responseType;
  }

  protected void validateAuthorizationRequest(AuthorizationRequest authorizationRequest) {
  }

  @SuppressWarnings("serial")
  class ValidationResponseException extends RuntimeException {
    private ValidationResponse v;

    public ValidationResponseException(ValidationResponse v) {
      this.v = v;
    }
  }

}
