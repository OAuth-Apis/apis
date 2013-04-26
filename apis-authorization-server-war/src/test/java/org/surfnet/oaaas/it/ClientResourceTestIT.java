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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Before;
import org.junit.Test;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.ValidationErrorResponse;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ClientResourceTestIT extends AbstractAuthorizationServerTest {

  private WebResource webResource;
  private ResourceServer resourceServer;

  private List<String> resourceServerScopes = Arrays.asList("read", "write");

  @Before
  public void prepareRestClientAndCreateResourceServer() {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    webResource = com.sun.jersey.api.client.Client.create(config)
        .resource(baseUrl())
        .path("admin")
        .path("resourceServer");

    ResourceServer newResourceServer = new ResourceServer();
    newResourceServer.setContactName("myContactName");
    newResourceServer.setDescription("The description");
    newResourceServer.setName("the name" + System.currentTimeMillis());
    newResourceServer.setKey("the-key-" + System.currentTimeMillis());
    newResourceServer.setThumbNailUrl("http://example.com/thumbnail");
    newResourceServer.setScopes(resourceServerScopes);

    resourceServer = webResource
        .type(MediaType.APPLICATION_JSON)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(ResourceServer.class, newResourceServer);

    // setup a new webResource as entry point for Client-REST-requests.
    webResource = com.sun.jersey.api.client.Client.create(config)
        .resource(baseUrl())
        .path("admin")
        .path("resourceServer")
        .path(String.valueOf(resourceServer.getId()))
        .path("client");
  }

  @Test
  public void getNonExisting() {
    ClientResponse response = webResource
        .path("-1")
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(ClientResponse.class);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void getAll() {
    putSomeClient();
    putSomeClient();

    ClientResponse response = webResource
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(ClientResponse.class);
    assertEquals(200, response.getStatus());
    assertTrue(response.getEntity(Client[].class).length > 1);
  }


  @Test
  public void get() {
    Client c = putSomeClient();

    final Client returnedFromGet = webResource
        .path(String.valueOf(c.getId()))
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(Client.class);

    assertEquals(c.getId(), returnedFromGet.getId());
    assertEquals(c.getAttributes(), returnedFromGet.getAttributes());
  }

  @Test
  public void put() {
    Client c = buildClient();
    Client putResult = webResource
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(Client.class, c);
    assertThat("Server should override provided secret with a generated one",
        putResult.getSecret(),not(equalTo(c.getSecret())));

    assertNotNull(putResult.getId());

    assertEquals(c.getAttributes(), putResult.getAttributes());
  }

  @Test
  public void putInvalidScopes() {
    Client c = buildClient();
    c.setScopes(Arrays.asList("invalidScope", "read", "write"));
    ClientResponse clientResponse = webResource
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(ClientResponse.class, c);
    assertThat("Server should not accept a client with scopes that are not a subset of the resourceServers scope",
        clientResponse.getStatus(), equalTo(400));
    final ValidationErrorResponse validationErrorResponse = clientResponse.getEntity(ValidationErrorResponse.class);
    assertThat(validationErrorResponse.getViolations().size(), equalTo(1));
    assertThat(validationErrorResponse.getViolations().get(0), containsString("Client should only contain scopes that its resource server defines"));
  }

  @Test
  public void post() {
    Client originalClient = putSomeClient();


    final String newDescription = "new description";
    originalClient.setDescription(newDescription);
    Client postResult = webResource
        .path(String.valueOf(originalClient.getId()))
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .post(Client.class, originalClient);
    assertEquals(newDescription, postResult.getDescription());
  }

  @Test
  public void delete() {
    Client c = putSomeClient();
    String id = String.valueOf(c.getId());
    ClientResponse deleteResponse = webResource
        .path(id)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .delete(ClientResponse.class);
    assertEquals(204, deleteResponse.getStatus());

    ClientResponse getResponse = webResource
        .path(id)
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .get(ClientResponse.class);
    assertEquals(404, getResponse.getStatus());
  }

  private Client buildClient() {
    Client c = new Client();
    String r = UUID.randomUUID().toString();
    c.setClientId(r);
    c.setContactEmail("contact@example.com");
    c.setContactName("contact name");
    c.setName(r);
    c.setScopes(Arrays.asList("read"));
    c.setSecret(r);
    c.setDescription("Some description");
    final HashMap<String, String> attributes = new HashMap<String, String>();
    attributes.put("myKey", "myValue");
    attributes.put("myKey2", "myValue2");
    attributes.put("myKey3", "myValue3");
    c.setAttributes(attributes);
    return c;
  }

  private Client putSomeClient() {
    Client c = buildClient();
    return webResource
        .header("Authorization", authorizationBearer(ACCESS_TOKEN))
        .put(Client.class, c);
  }

}
