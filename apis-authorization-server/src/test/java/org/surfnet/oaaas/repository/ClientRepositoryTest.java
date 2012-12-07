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
package org.surfnet.oaaas.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;

/**
 * {@link Test} for {@link ClientRepository}
 * 
 */
public class ClientRepositoryTest extends AbstractTestRepository {

  @Test
  public void test() {
    ClientRepository repo = getRepository(ClientRepository.class);
    Client client = repo.findByClientId("cool_app_id");
    Map<String, String> attr = client.getAttributes();
    assertEquals("foo-university", attr.get("university"));
  }

  /**
   * Create a client, create some child-objects (authorization request, access token).
   * Delete the client.
   * Make sure the child objects are removed as well.
   */
  @Test
  public void cascade() {
    ClientRepository repo = getRepository(ClientRepository.class);
    ResourceServerRepository resourceServerRepository = getRepository(ResourceServerRepository.class);
    AccessTokenRepository accessTokenRepository = getRepository(AccessTokenRepository.class);
    AuthorizationRequestRepository authorizationRequestRepository = getRepository(AuthorizationRequestRepository.class);

    // Create and save a resource Server
    ResourceServer r = new ResourceServer();
    r.setKey("key");
    r.setName("name");
    r.setContactName("contactname");
    r.setSecret("secret");

    r = resourceServerRepository.save(r);

    // Create and save a client
    Client client = new Client();
    client.setName("name");
    client.setClientId("clientid");

    // Let them meet each other
    r.setClients(new HashSet(Arrays.asList(client)));
    client.setResourceServer(r);

    client = repo.save(client);


    // Create an access token
    AccessToken at = new AccessToken("mytoken", new AuthenticatedPrincipal("username"), client, 0, null);
    at = accessTokenRepository.save(at);
    assertEquals(at, accessTokenRepository.findOne(at.getId()));

    // Create an authorization request
    AuthorizationRequest ar = new AuthorizationRequest("foo", "faa", "boo", null, "boo", "boo");
    ar.setClient(client);
    ar = authorizationRequestRepository.save(ar);
    assertEquals(ar, authorizationRequestRepository.findOne(ar.getId()));

    // Make sure things are saved; the relation between clients and access tokens is unidirectional; therefore a
    // delete would not work with attached entities.
    entityManager.clear();

    final long clientId = client.getId();
    repo.delete(client);
    assertNull(repo.findOne(clientId));

    assertNull(accessTokenRepository.findOne(at.getId()));
    assertNull(authorizationRequestRepository.findOne(ar.getId()));

  }
}
