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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for validation of Client
 *
 */
public class ClientTest extends AbstractEntityTest {

  @Test
  public void testValidation() {
    final List<String> uris = Arrays.asList("uri1", "uri2");

    Client client = new Client();
    client.setName("not-null");
    client.setClientId("not-null");
    client.setUseRefreshTokens(true);
    client.setExpireDuration(60 * 60);
    client.setRedirectUris(uris);
    Set<ConstraintViolation<Client>> violations = validator.validate(client);
    assertEquals(0, violations.size());
    assertEquals(uris, client.getRedirectUris());
  }

}
