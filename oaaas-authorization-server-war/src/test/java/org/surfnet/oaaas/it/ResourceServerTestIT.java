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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for ResourceServer REST resource.
 * This presumes some state on the server side:
 * <ul>
 *   <li>An existing </li>
 * </ul>
 */
public class ResourceServerTestIT extends AbstractAuthorizationServerTest {

  private static final String ACCESS_TOKEN = "dad30fb8-ad90-4f24-af99-798bb71d27c8";
  private Client client;

  @Before
  public void client() {
    ClientConfig config = new DefaultClientConfig();
    this.client = Client.create(config);
  }

  @Test
  public void put() {
    /*
    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setContactName("myContactName");
    resourceServer.setDescription("The description");
    resourceServer.setName("the name");
    resourceServer.setThumbNailUrl("http://example.com/thumbnail");
    final ClientResponse response = client
        .resource(baseUrlWith("/admin/resourceServer"))
        .type(MediaType.APPLICATION_JSON)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(ClientResponse.class, resourceServer);

    assertEquals(205, response.getStatus());
    ResourceServer returnedResourceServer = response.getEntity(ResourceServer.class);
    assertEquals(resourceServer,returnedResourceServer);
    */
  }

}
