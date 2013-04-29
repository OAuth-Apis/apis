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

package org.surfnet.oaaas.it;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Before;
import org.junit.Test;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.StatisticsResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for ResourceServer REST resource. This presumes on the
 * server side:
 * <ul>
 * <li>An existing access token</li>
 * </ul>
 */
public class ResourceServerTestIT extends AbstractAuthorizationServerTest {

  private WebResource webResource;

  @Before
  public void client() {
    ClientConfig config = new DefaultClientConfig();
    // Default jaxb provider cannot properly deserialize lists.
    config.getClasses().add(JacksonJsonProvider.class);

    webResource = Client.create(config).resource(baseUrl()).path("admin").path("resourceServer");
  }

  @Test
  public void put() {
    ResourceServer resourceServer = buildResourceServer();

    final ClientResponse response = webResource.header("Authorization", authorizationBearer(ACCESS_TOKEN)).put(ClientResponse.class,
        resourceServer);

    assertEquals(201, response.getStatus());
    ResourceServer returnedResourceServer = response.getEntity(ResourceServer.class);
    assertEquals(resourceServer.getName(), returnedResourceServer.getName());
    assertNotNull("the server should generate an ID", returnedResourceServer.getId());
    assertNotNull("the server should generate a secret", returnedResourceServer.getSecret());
  }

  @Test
  public void putInvalid() {
    ResourceServer resourceServer = buildResourceServer();

    final ClientResponse response = webResource.header("Authorization", authorizationBearer(ACCESS_TOKEN)).put(ClientResponse.class,
        resourceServer);

    assertEquals(201, response.getStatus());

    final ClientResponse response2 = webResource.header("Authorization", authorizationBearer(ACCESS_TOKEN)).put(ClientResponse.class,
        resourceServer);
    assertEquals("putting the same server twice should not work because id+name combination has unique constraint", 500,
        response2.getStatus());
  }

  @Test
  public void get() {
    // First get a non existing resource server
    ClientResponse response = webResource.path("-1").header("Authorization", authorizationBearer(ACCESS_TOKEN)).get(ClientResponse.class);
    assertEquals("Random id should return nothing", 404, response.getStatus());

    // Insert some random one.
    ResourceServer existingResourceServer = putSomeResourceServer();

    // Get it again.
    final ResourceServer returnedFromGet = webResource.path(String.valueOf(existingResourceServer.getId()))
        .header("Authorization", authorizationBearer(ACCESS_TOKEN)).get(ResourceServer.class);
    assertEquals(existingResourceServer, returnedFromGet);

    // Get all
    final List<ResourceServer> returnFromGetAll = webResource.header("Authorization", authorizationBearer(ACCESS_TOKEN)).get(
        new GenericType<List<ResourceServer>>() {
        });
    assertTrue(returnFromGetAll.size() > 0);
  }

  @Test
  public void post() {

    ResourceServer existingResourceServer = putSomeResourceServer();

    final String newThumbnailUrl = "http://example.com/anotherThumbNailUrl";
    existingResourceServer.setThumbNailUrl(newThumbnailUrl);

    ResourceServer returnedFromPost = webResource.path(String.valueOf(existingResourceServer.getId()))
        .header("Authorization", authorizationBearer(ACCESS_TOKEN)).post(ResourceServer.class, existingResourceServer);

    assertEquals(newThumbnailUrl, returnedFromPost.getThumbNailUrl());
  }

  @Test
  public void delete() {

    // create a random resourceServer
    ResourceServer existingResourceServer = putSomeResourceServer();

    // Delete it again.
    String id = String.valueOf(existingResourceServer.getId());
    ClientResponse response = webResource.path(id).header("Authorization", authorizationBearer(ACCESS_TOKEN)).delete(ClientResponse.class);

    // Make sure that the response is a 'no content' one
    assertEquals(204, response.getStatus());

    // And make sure it is not found anymore afterwards.
    ClientResponse responseFromGet = webResource.path(id).header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .delete(ClientResponse.class);
    assertEquals(404, responseFromGet.getStatus());
  }

  @Test
  public void stats() {
    final ClientResponse response = webResource.path("stats").header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(ClientResponse.class);
    assertEquals(200, response.getStatus());

    StatisticsResponse entity = response.getEntity(StatisticsResponse.class);
    assertTrue(entity.getResourceServers().size() > 0);
    assertNotNull(entity.getResourceServers().get(0).getName());
  }

  @Test
  public void principal() {
    final ClientResponse response = webResource.path("principal").header("Authorization", authorizationBearer(ACCESS_TOKEN))
            .get(ClientResponse.class);
    assertEquals(200, response.getStatus());

    AuthenticatedPrincipal principal = response.getEntity(AuthenticatedPrincipal.class);
    assertEquals("admin-enduser",principal.getName());
  }

  /**
   * Convenience method to put some random resourceServer.
   * 
   * @return a persisted resourceServer
   */
  private ResourceServer putSomeResourceServer() {
    ResourceServer resourceServer = buildResourceServer();

    return webResource.header("Authorization", authorizationBearer(ACCESS_TOKEN)).put(ResourceServer.class, resourceServer);
  }

  /**
   * Create a resourceServer that's ready to be persisted.
   * 
   * @return a ResourceServer
   */
  private ResourceServer buildResourceServer() {
    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setContactName("myContactName");
    resourceServer.setDescription("The description");
    resourceServer.setName("the name" + System.currentTimeMillis());
    resourceServer.setKey("the-key-" + System.currentTimeMillis());
    resourceServer.setThumbNailUrl("http://example.com/thumbnail");
    return resourceServer;
  }

}
