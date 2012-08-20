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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;

/**
 * {@link Test} for {@link ResourceServerRepository}
 * 
 */
public class ResourceServerRepositoryTest extends AbstractRepositoryTest {

  @Test
  public void test() {
    ResourceServerRepository repo = getRepository(ResourceServerRepository.class);
    ClientRepository clientRepo = getRepository(ClientRepository.class);

    ResourceServer rs = repo.findByKey("authorization-server-admin");
    Client client = null;
    assertFalse(rs.containsClient(client));

    client = clientRepo.findByClientId("authorization-server-admin-js-client");
    assertTrue(rs.containsClient(client));
    List<Client> clients = rs.getClients();
    assertEquals(1, clients.size());
  }

}
