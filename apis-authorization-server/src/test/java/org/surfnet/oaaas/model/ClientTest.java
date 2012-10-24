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
package org.surfnet.oaaas.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for validation of Client
 */
public class ClientTest extends AbstractEntityTest {

  private Client client;

  private List<String> uris = Arrays.asList("http://uri1", "http://uri2");

  @Before
  public void setup() {
    client = new Client();
    client.setName("not-null");
    client.setClientId("not-null");
    client.setUseRefreshTokens(true);
    client.setExpireDuration(60 * 60);
    client.setRedirectUris(uris);

    ResourceServer resourceServer = new ResourceServer();
    resourceServer.setScopes(Arrays.asList("read", "delete"));
    client.setScopes(Arrays.asList("read", "delete"));
    client.setResourceServer(resourceServer);

  }

  @Test
  public void noErrors() {
    Set<ConstraintViolation<Client>> violations = validator.validate(client);
    assertEquals(0, violations.size());
    assertEquals(uris, client.getRedirectUris());
  }

  @Test
  public void arbitraryScopes() {

    client.setScopes(Arrays.asList("arbitrary", "scopes"));
    Set<ConstraintViolation<Client>> violations = validator.validate(client);
    assertEquals("Client should only be able to use scopes that the resource server defines", 1, violations.size());
  }

  @Test
  public void redirectUris() {
    client.setRedirectUris(Arrays.asList("invalid-uri"));
    Set<ConstraintViolation<Client>> violations = validator.validate(client);
    assertEquals("Client should have valid redirectUris", 1, violations.size());
  }
}
