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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.ClientRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * {@link Test} for {@link OAuth2ValidatorImpl}
 * 
 */
public class OAuth2ValidatorImplTest {

  @Mock
  private ClientRepository clientRepository;

  @InjectMocks
  private OAuth2ValidatorImpl validator = new OAuth2ValidatorImpl();
  
  private AuthorizationRequest request;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    Client client = createClient("client-app");
    when(clientRepository.findByClientId(client.getClientId())).thenReturn(client);
    this.request = getAuthorizationRequest(client);
  }

  

  /**
   * Test method for
   * {@link org.surfnet.oaaas.auth.OAuth2ValidatorImpl#validate(org.surfnet.oaaas.model.AuthorizationRequest)}
   * .
   */
  @Test
  public void testValidateValidRedirectUri() {
    request.setRedirectUri("http://not-registrated.nl");
    validate(ValidationResponse.REDIRCT_URI_NOT_VALID);
  }

  @Test
  public void testValidateClientId() {
    request.setClientId("unknown_client");
    validate(ValidationResponse.UNKNOWN_CLIENT_ID);
  }
  
  @Test
  public void testValidateImplicitGrant() {
    request.setResponseType(OAuth2ValidatorImpl.IMPLICIT_GRANT_RESPONSE_TYPE);
    request.setRedirectUri(" ");
    validate(ValidationResponse.IMPLICIT_GRANT_REDIRECT_URI);
  }
  
  @Test
  public void testValidateResponseType() {
    request.setResponseType("not-existing-response-type");
    validate(ValidationResponse.UNSUPPORTED_RESPONSE_TYPE);
  }

  @Test
  public void testValidateScope() {
    request.setScope("no-existing-scope");
    validate(ValidationResponse.SCOPE_NOT_VALID);
  }

  @Test
  public void testValidateRedirectUri() {
    request.setRedirectUri("qwert://no-valid-url");
    validate(ValidationResponse.REDIRCT_URI_NOT_URI);
  }
  private Client createClient(String clientId) {
    Client client = new Client();
    client.setName("Client App");
    client.setClientId(clientId);
    client.setRedirectUris("http://gothere.nl,http://gohere.nl");
    client.setScopes("read,update");
    return client;
  }

  private void validate(ValidationResponse expected) {
    ValidationResponse response = validator.validate(request);
    assertEquals(expected, response);
  }

  private AuthorizationRequest getAuthorizationRequest(Client client) {
    AuthorizationRequest request = new AuthorizationRequest();
    request.setClientId(client.getClientId());
    request.setRedirectUri("http://gothere.nl");
    request.setScope("read,update");
    request.setResponseType(OAuth2ValidatorImpl.AUTHORIZATION_CODE_GRANT_RESPONSE_TYPE);
    return request;
  }

}
