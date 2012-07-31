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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.yammer.dropwizard.testing.ResourceTest;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientResourceTest extends ResourceTest {


  @Mock
  private ResourceServerRepository resourceServerRepository;

  @Mock
  private ClientRepository repository;

  @InjectMocks
  private ClientResource clientResource;

  @Override
  protected void setUpResources() throws Exception {
    clientResource = new ClientResource();
    MockitoAnnotations.initMocks(this);
    addResource(clientResource);

  }

  @Test
  public void getNonExisting() {
    final ResourceServer resourceServer = new ResourceServer();
    when(resourceServerRepository.findByIdAndOwner(1L, "me")).thenReturn(resourceServer);

    ClientResponse response = client().resource("/admin/resourceServer/1/client/1.json").get(ClientResponse.class);
    assertEquals(404, response.getStatus());
    verify(repository).findOne(1L);
  }

  @Test
  public void getAllWhenNoneFound() {
    ClientResponse response = client().resource("/admin/client").get(ClientResponse.class);
    assertEquals(404, response.getStatus());

  }

  @Test
  public void getAll() {
    when(repository.findAll()).thenReturn(Arrays.asList(new Client(), new Client(), new Client()));
    ClientResponse response = client().resource("/admin/resourceSerclient").get(ClientResponse.class);
    assertEquals(200, response.getStatus());
    List<Client> clients = response.getEntity(new GenericType<ArrayList<Client>>(ArrayList.class));
    assertEquals(3, clients.size());
  }

  @Test
  public void get() {
    Client s = new Client();
    s.setId(1L);
    when(repository.findOne(1L)).thenReturn(s);

    ClientResponse response = client().resource("/admin/client/1.json").get(ClientResponse.class);

    assertEquals(200, response.getStatus());
    verify(repository).findOne(1L);
  }
}
