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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.surfnet.oaaas.auth.principal.BasicAuthCredentials;
import org.surfnet.oaaas.model.AccessTokenRequest;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.ClientRepository;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse.*;

/**
 * Implementation of {@link OAuth2Validator}
 * 
 */
@Named
public class OAuth2ValidatorImpl implements OAuth2Validator {

  private static final Set<String> RESPONSE_TYPES = new HashSet<String>();

  private static final Set<String> GRANT_TYPES = new HashSet<String>();

  static {
    RESPONSE_TYPES.add(IMPLICIT_GRANT_RESPONSE_TYPE);
    RESPONSE_TYPES.add(AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE);
    
    GRANT_TYPES.add(GRANT_TYPE_AUTHORIZATION_CODE);
    GRANT_TYPES.add(GRANT_TYPE_REFRESH_TOKEN);
    GRANT_TYPES.add(GRANT_TYPE_CLIENT_CREDENTIALS);
    GRANT_TYPES.add(GRANT_TYPE_PASSWORD);
  }

  @Inject
  private ClientRepository clientRepository;

  @Override
  public ValidationResponse validate(AuthorizationRequest authorizationRequest) {
    try {
      validateAuthorizationRequest(authorizationRequest);

      String responseType = validateResponseType(authorizationRequest);

      Client client = validateClient(authorizationRequest);
      authorizationRequest.setClient(client);

      String redirectUri = determineRedirectUri(authorizationRequest, responseType, client);
      authorizationRequest.setRedirectUri(redirectUri);

      List<String> scopes = determineScopes(authorizationRequest, client);
      authorizationRequest.setRequestedScopes(scopes);

    } catch (ValidationResponseException e) {
      return e.v;
    }
    return VALID;
  }

  protected List<String> determineScopes(AuthorizationRequest authorizationRequest, Client client) {
    if (CollectionUtils.isEmpty(authorizationRequest.getRequestedScopes())) {
      // TODO add default scopes.
      return null;
    } else {
      List<String> scopes = authorizationRequest.getRequestedScopes();
      List<String> clientScopes = client.getScopes();
      for (String scope : scopes) {
        if (!clientScopes.contains(scope)) {
          throw new ValidationResponseException(SCOPE_NOT_VALID);
        }
      }
      return authorizationRequest.getRequestedScopes();
    }
  }

  protected String determineRedirectUri(AuthorizationRequest authorizationRequest, String responseType, Client client) {
    List<String> uris = client.getRedirectUris();
    String redirectUri = authorizationRequest.getRedirectUri();
    if (StringUtils.isBlank(redirectUri)) {
      if (responseType.equals(IMPLICIT_GRANT_RESPONSE_TYPE)) {
        throw new ValidationResponseException(IMPLICIT_GRANT_REDIRECT_URI);
      } else if (CollectionUtils.isEmpty(uris)) {
        throw new ValidationResponseException(REDIRECT_URI_REQUIRED);
      } else {
        return uris.get(0);
      }
    } else if (!AuthenticationFilter.isValidUri(redirectUri)) {
      throw new ValidationResponseException(REDIRECT_URI_NOT_URI);
    } else if (redirectUri.contains("#")) {
      throw new ValidationResponseException(REDIRECT_URI_FRAGMENT_COMPONENT);
    } else if (CollectionUtils.isNotEmpty(uris)) {
      boolean match = false;
      for (String uri : uris) {
        if (redirectUri.startsWith(uri)) {
          match = true;
          break;
        }
      }
      if (!match) {
        // Reset the redirect uri to first of the registered ones. Otherwise the result error response would be undesired: a (possibly on purpose) redirect to URI that is not acked.
        authorizationRequest.setRedirectUri(uris.get(0));
        throw new ValidationResponseException(REDIRECT_URI_NOT_VALID);
      }
    }
    return redirectUri;
  }

  protected Client validateClient(AuthorizationRequest authorizationRequest) {
    String clientId = authorizationRequest.getClientId();
    Client client = StringUtils.isBlank(clientId) ? null : clientRepository.findByClientId(clientId);
    if (client == null) {
      throw new ValidationResponseException(UNKNOWN_CLIENT_ID);
    }
    if (!client.isAllowedImplicitGrant()
        && authorizationRequest.getResponseType().equals(IMPLICIT_GRANT_RESPONSE_TYPE)) {
      throw new ValidationResponseException(IMPLICIT_GRANT_NOT_PERMITTED);
    }
    return client;
  }

  protected String validateResponseType(AuthorizationRequest authorizationRequest) {
    String responseType = authorizationRequest.getResponseType();
    if (StringUtils.isBlank(responseType) || !RESPONSE_TYPES.contains(responseType)) {
      throw new ValidationResponseException(UNSUPPORTED_RESPONSE_TYPE);
    }
    return responseType;
  }

  protected void validateAuthorizationRequest(AuthorizationRequest authorizationRequest) {
  }


  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.OAuth2Validator#validate(org.surfnet.oaaas.model.AccessTokenRequest)
   */
  @Override
  public ValidationResponse validate(AccessTokenRequest request, 
      BasicAuthCredentials clientCredentials) {
    try {
      validateGrantType(request);
      
      validateAttributes(request);
      
      validateClient(request, clientCredentials);
      
      validateAccessTokenRequest(request);
      
    } catch (ValidationResponseException e) {
      return e.v;
    }
    return VALID;
  }
  
  protected void validateGrantType(AccessTokenRequest request) {
    String grantType = request.getGrantType();
    if (StringUtils.isBlank(grantType) || !GRANT_TYPES.contains(grantType)) {
      throw new ValidationResponseException(UNSUPPORTED_GRANT_TYPE);
    }
  }

  protected void validateAttributes(AccessTokenRequest request) {
    String grantType = request.getGrantType();
    if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
      if (StringUtils.isBlank(request.getCode())) {
        throw new ValidationResponseException(INVALID_GRANT_AUTHORIZATION_CODE);
      }
    } else if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
      if (StringUtils.isBlank(request.getRefreshToken())) {
        throw new ValidationResponseException(INVALID_GRANT_REFRESH_TOKEN);
      }
    } else if (GRANT_TYPE_PASSWORD.equals(grantType)) {
      if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
        throw new ValidationResponseException(INVALID_GRANT_PASSWORD);
      }
    }
  }
  
  protected void validateClient(AccessTokenRequest accessTokenRequest, 
      BasicAuthCredentials clientCredentials) {
    Client client = null;
    
    // Were we given client credentials via basic auth?
    if (!clientCredentials.isNull()) {
      // Confirm that the credentials are valid and use them to get the client
      if (!clientCredentials.isValid()) {
        throw new ValidationResponseException(UNAUTHORIZED_CLIENT);
      }
      client = getClient(clientCredentials.getUsername(), clientCredentials.getPassword(), 
          UNAUTHORIZED_CLIENT);
    } else if (!StringUtils.isBlank(accessTokenRequest.getClientId())) {
      // Use the request parameters to obtain the client
      client = getClient(accessTokenRequest.getClientId(), accessTokenRequest.getClientSecret(), 
          UNKNOWN_CLIENT_ID);
    }

    // Record the associated client
    accessTokenRequest.setClient(client);
  }
  
  private Client getClient(String clientId, String clientSecret, ValidationResponse error) {
    // Find the indicated client
    Client client = clientRepository.findByClientId(clientId);
    if (client == null) {
      throw new ValidationResponseException(error);
    }
    
    // Confirm that the credentials match those for the client
    if (!client.verifySecret(clientSecret)) {
      throw new ValidationResponseException(error);
    }
    return client;
  }
  
  protected void validateAccessTokenRequest(AccessTokenRequest accessTokenRequest) {
    if (accessTokenRequest.getGrantType().equals(GRANT_TYPE_CLIENT_CREDENTIALS)) {
      // We must have a client
      Client client = accessTokenRequest.getClient();
      if (client == null) {
        throw new ValidationResponseException(INVALID_GRANT_CLIENT_CREDENTIALS);
      }
      
      // And the client must be allowed to perform this grant type
      if (!client.isAllowedClientCredentials()) {
        accessTokenRequest.setClient(null);
        throw new ValidationResponseException(CLIENT_CREDENTIALS_NOT_PERMITTED);
      }
    }
  }

}
