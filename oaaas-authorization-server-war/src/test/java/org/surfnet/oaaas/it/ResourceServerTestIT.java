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

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.junit.Before;
import org.junit.Test;
import org.surfnet.oaaas.model.ResourceServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for ResourceServer REST resource.
 * This presumes some state on the server side:
 * <ul>
 *   <li>An existing </li>
 * </ul>
 */
public class ResourceServerTestIT extends AbstractAuthorizationServerTest {

  private static final String ACCESS_TOKEN = "dad30fb8-ad90-4f24-af99-798bb71d27c8";
  private WebResource webResource;

  @Before
  public void client() {
    ClientConfig config = new DefaultClientConfig();

    webResource = Client.create(config)
        .resource(baseUrl())
        .path("admin")
        .path("resourceServer");
  }

  @Test
  public void put() {
    ResourceServer resourceServer = buildResourceServer();

    final ClientResponse response = webResource
        .type(MediaType.APPLICATION_JSON)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(ClientResponse.class, resourceServer);

    assertEquals(201, response.getStatus());
    ResourceServer returnedResourceServer = response.getEntity(ResourceServer.class);
    assertEquals(resourceServer.getName(),returnedResourceServer.getName());
    assertNotNull("the server should generate an ID", returnedResourceServer.getId());
    assertNotNull("the server should generate a secret", returnedResourceServer.getSecret());
  }

  @Test
  public void get() {
    ResourceServer resourceServer = buildResourceServer();

    ResourceServer returnedFromPut = webResource
        .type(MediaType.APPLICATION_JSON)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(ResourceServer.class, resourceServer);


    final ResourceServer returnedFromGet = webResource
        .path(String.valueOf(returnedFromPut.getId()))
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(ResourceServer.class);
    assertEquals(returnedFromPut, returnedFromGet);
  }

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
