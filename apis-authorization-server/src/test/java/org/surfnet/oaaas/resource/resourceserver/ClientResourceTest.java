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

package org.surfnet.oaaas.resource.resourceserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ErrorResponse;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientResourceTest {
  private static final Logger LOG = LoggerFactory.getLogger(ClientResourceTest.class);

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private ResourceServerRepository resourceServerRepository;

  @Mock
  private Validator validator;

  @InjectMocks
  private ClientResource clientResource;

  MockHttpServletRequest request = new MockHttpServletRequest();

  @Before
  public void setup() {
    clientResource = new ClientResource();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sanitize() {
    String sanitized = clientResource.sanitizeClientName("ab()();'$&*  ---  %(&^*c123");
    assertEquals("ab-------c123", sanitized);

    sanitized = clientResource.sanitizeClientName("some nice client name");
    assertEquals("some-nice-client-name", sanitized);

    sanitized = clientResource.sanitizeClientName("Some Nice Client-Name *%^$#''§`~");
    assertEquals("some-nice-client-name-", sanitized);
  }

  @Test
  public void uniqueClientId() {
    final Client existingClient = new Client();
    when(clientRepository.findByClientId(anyString())).thenReturn(
        existingClient,
        existingClient,
        existingClient,
        existingClient,
        existingClient,
        null);
    Client newClient = new Client();
    newClient.setName("myname");
    String clientId = clientResource.generateClientId(newClient);
    LOG.debug("client id generated: " + clientId);
    // 5 existing clients, this one should be number 6.
    assertEquals("myname6", clientId);
  }

  @Test
  public void scopesShouldBeSubsetOfResourceServerScopes() {

    Client client = new Client();
    request.setAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE, new VerifyTokenResponse("",
        new ArrayList<String>(), new AuthenticatedPrincipal("user"), 0L));
    client.setScopes(Arrays.asList("Some", "arbitrary", "set"));
    client.setName("clientname");
    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setScopes(Arrays.asList("read", "update", "delete"));
    when(resourceServerRepository.findByIdAndOwner(1L, "user")).thenReturn(resourceServer);

    final ConstraintViolation<Client> violation = (ConstraintViolation<Client>) mock(ConstraintViolation.class);
    Set<ConstraintViolation<Client>> violations = Collections.singleton(violation);
    when(validator.validate(client)).thenReturn(violations);
    final Response response = clientResource.put(request, 1L, client);
    assertEquals(400, response.getStatus());
    assertEquals("invalid_scope", ((ErrorResponse) response.getEntity()).getError());
  }
}
