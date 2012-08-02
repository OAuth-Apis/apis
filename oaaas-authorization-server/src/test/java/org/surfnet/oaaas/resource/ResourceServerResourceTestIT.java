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

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.model.ResourceServer;

import static org.junit.Assert.assertEquals;

@Ignore
public class ResourceServerResourceTestIT {

  private static final String BASE_URL = "http://localhost:8080/resourceServer";
  private Client client;
  private static final Logger LOG = LoggerFactory.getLogger(ResourceServerResourceTestIT.class);

  @Before
  public void setup() {
    client = Client.create();
  }

  @Test
  public void get() {
    WebResource webResource = client.resource(BASE_URL + "/1.json");

    ClientResponse response = webResource.accept("application/json")
        .get(ClientResponse.class);

    assertEquals(200, response.getStatus());

    String output = response.getEntity(String.class);

    System.out.println("Output from Server .... \n");
    System.out.println(output);

  }

  @Test
  public void put() {
    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setName("thename");
    resourceServer.setSecret("thesecret");
    resourceServer.setContactName("myname");
    LOG.debug(client.getMessageBodyWorkers().toString());
    final ResourceServer responseObj = client
        
        .resource(BASE_URL)
        .type(MediaType.APPLICATION_JSON)
        .put(ResourceServer.class, resourceServer);

    assertEquals(resourceServer, responseObj);

  }
}
