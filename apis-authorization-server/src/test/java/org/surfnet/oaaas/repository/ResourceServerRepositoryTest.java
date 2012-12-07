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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;

/**
 * {@link Test} for {@link ResourceServerRepository}
 * 
 */
public class ResourceServerRepositoryTest extends AbstractTestRepository {

  @Test
  public void test() {
    ResourceServerRepository repo = getRepository(ResourceServerRepository.class);
    ClientRepository clientRepo = getRepository(ClientRepository.class);

    ResourceServer rs = repo.findByKey("authorization-server-admin");
    Client client = null;
    assertFalse(rs.containsClient(client));

    client = clientRepo.findByClientId("authorization-server-admin-js-client");
    assertTrue(rs.containsClient(client));
    Set<Client> clients = rs.getClients();
    assertEquals(1, clients.size());
  }

  @Test
  public void findAll() {
    ResourceServerRepository repo = getRepository(ResourceServerRepository.class);
    Iterable<ResourceServer> all = repo.findAll();
    int i = 0;
    for (ResourceServer resourceServer : all) {
      i++;
      Set<Client> clients = resourceServer.getClients();
      assertTrue(!clients.isEmpty());
    }
    assertEquals(3, i);
  }

  /*
   * Due to
   * http://stackoverflow.com/questions/9123964/how-do-you-use-spring-data-jpa-
   * outside-of-a-spring-container we need to do the transaction handling
   *  ourselves.
   */
  @Test
  public void cascade() {
    ResourceServerRepository repo = getRepository(ResourceServerRepository.class);
    ClientRepository clientRepo = getRepository(ClientRepository.class);

    getEntityManager().getTransaction().begin();

    // Create and save a resourceServer
    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setKey("key");
    resourceServer.setName("name");
    resourceServer.setSecret("sec");
    resourceServer.setContactName("contact");
    resourceServer.setScopes(Arrays.asList("read"));
    resourceServer = repo.save(resourceServer);

    // Create and save a client, associated with the resourceServer
    Client c = new Client();
    c.setName("name");
    c.setClientId("clientid");
    c.setSecret(UUID.randomUUID().toString());
    c.setResourceServer(resourceServer);
    resourceServer.setClients(new HashSet(Arrays.asList(c)));
    c = clientRepo.save(c);
    getEntityManager().getTransaction().commit();

    // See that the client can be found
    assertNotNull(clientRepo.findOne(c.getId()));

    long resourceServerId = resourceServer.getId();
    // Remove the resourceServer
    getEntityManager().getTransaction().begin();
    repo.delete(resourceServer);
    getEntityManager().getTransaction().commit();

    // Expect the resource server to be deleted
    assertNull(repo.findOne(resourceServerId));

    // Expect the client to be deleted as well.
    final Client foundClient = clientRepo.findOne(c.getId());
    assertNull(foundClient);
  }

}
