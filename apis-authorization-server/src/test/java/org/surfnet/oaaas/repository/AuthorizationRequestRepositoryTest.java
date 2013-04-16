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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;

import static org.junit.Assert.assertEquals;

/**
 * {@link Test} for {@link AuthorizationRequestRepository}
 * 
 */
public class AuthorizationRequestRepositoryTest extends AbstractTestRepository {

  @Test
  public void test() {
    AuthorizationRequestRepository repo = getRepository(AuthorizationRequestRepository.class);
    String authState = UUID.randomUUID().toString();
    AuthorizationRequest authReq = new AuthorizationRequest("code", "cool_app_id", "http://whatever",
        Arrays.asList("read","update"),
        "state", authState);
    ClientRepository clientRepo = getRepository(ClientRepository.class);
    Client client = clientRepo.findByClientId(authReq.getClientId());
    client.getAttributes();
    authReq.setClient(client);
    save(authReq, repo);
    authReq.setPrincipal(getPrincipal());
    repo.save(authReq);

    AuthorizationRequest authReqSaved = repo.findByAuthState(authState);
    AuthenticatedPrincipal principal = authReqSaved.getPrincipal();
    assertEquals("foo-university", principal.getAttributes().get("organization"));

  }

  /*
   * http://stackoverflow.com/questions/9123964/how-do-you-use-spring-data-jpa-
   * outside-of-a-spring-container
   */
  private AuthorizationRequest save(AuthorizationRequest authorizationRequest, AuthorizationRequestRepository repo) {
    getEntityManager().getTransaction().begin();
    AuthorizationRequest save = repo.save(authorizationRequest);
    getEntityManager().getTransaction().commit();
    return save;
  }

  private AuthenticatedPrincipal getPrincipal() {
    List<String> roles = Arrays.asList(new String[] { "user", "admin" });
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("organization", "foo-university");
    return new AuthenticatedPrincipal("john.doe", roles, attributes);
  }

}
