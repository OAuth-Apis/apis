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

package org.surfnet.oaaas.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.ClientRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ClientResourceTest {
  private static final Logger LOG = LoggerFactory.getLogger(ClientResourceTest.class);

  @Mock
  private ClientRepository clientRepository;

  @InjectMocks
  private ClientResource clientResource;

  @Before
  public void setup() {
    clientResource = new ClientResource();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sanitize() {
    final String sanitized = clientResource.sanitizeClientName("ab()();'$&*  ---  %(&^*c123");
    assertEquals("abc123", sanitized);
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
}
