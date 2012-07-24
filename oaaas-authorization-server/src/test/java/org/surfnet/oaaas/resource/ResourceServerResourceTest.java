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

import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResourceServerResourceTest extends ResourceTest {

  @Mock
  private ResourceServerRepository repository;

  @InjectMocks
  private ResourceServerResource resourceServerResource;

  @Override
  protected void setUpResources() throws Exception {
    resourceServerResource = new ResourceServerResource();
    MockitoAnnotations.initMocks(this);
    addResource(resourceServerResource);
  }


  @Test
  public void getServer() {
    ResourceServer s = new ResourceServer();
    s.setId(1L);
    when(repository.findOne(1L)).thenReturn(s);
    assertThat("GET requests fetch the server by ID",
        client().resource("/resourceServer/1.json").get(ResourceServer.class),
        is(s));
    verify(repository).findOne(1L);
  }

  @Test
  public void nonExisting() {
    when(repository.findOne(991L)).thenReturn(null);

    ClientResponse response = client().resource("/resourceServer/991.json").get(ClientResponse.class);

    assertEquals("GET requests fetch the server by ID but when not found, returns a 404. ", 404, response.getStatus());

    verify(repository).findOne(991L);
  }

  @Test
  public void putServer() {
    ResourceServer newOne = new ResourceServer();
    newOne.setSecret("s");
    newOne.setContactName("c");
    newOne.setName("name");
    ResourceServer savedOne = new ResourceServer();
    savedOne.setId(101L);
    savedOne.setSecret("s");
    savedOne.setName("name");
    savedOne.setContactName("c");
    when(repository.save((ResourceServer) any())).thenReturn(savedOne);

    ClientResponse response = client()
        .resource("/resourceServer").put(ClientResponse.class, newOne);

    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(Long.valueOf(101L), response.getEntity(ResourceServer.class).getId());
  }

  @Test
  public void delete() {
    when(repository.findOne(1L)).thenReturn(new ResourceServer());
    ClientResponse response = client()
        .resource("/resourceServer/1.json")
        .delete(ClientResponse.class);
    assertEquals(204, response.getStatus());
  }

  @Test
  public void deleteButNoneFound() {
    when(repository.findOne(1L)).thenReturn(null);
    ClientResponse response = client()
        .resource("/resourceServer/1.json")
        .delete(ClientResponse.class);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void update() {
    ResourceServer existingOne = new ResourceServer();
    existingOne.setName("thename");
    existingOne.setContactName("contact");
    existingOne.setSecret("s");
    existingOne.setId(1L);

    ResourceServer newOne = new ResourceServer();
    newOne.setId(1L);
    newOne.setSecret("s");
    newOne.setName("newname");
    newOne.setContactName("contact");

    when(repository.findOne(1L)).thenReturn(existingOne);

    when(repository.save((ResourceServer) any())).thenReturn(newOne);
    ClientResponse response = client()
        .resource("/resourceServer/1.json")
        .entity(newOne)
        .post(ClientResponse.class);

    assertEquals(response.getStatus(), 200);
    final ResourceServer savedOne = response.getEntity(ResourceServer.class);
    assertEquals("saved instance should equal posted one", newOne, savedOne);
    assertEquals(savedOne.getName(), "newname");
  }
}
