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

package org.surfnet.oaaas.model;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourceServerTest extends AbstractEntityTest {

  private ResourceServer resourceServer;

  @Before
  public void before() {
    resourceServer = new ResourceServer();
    resourceServer.setKey("key");
    resourceServer.setName("name");
    resourceServer.setSecret("sec");
    resourceServer.setContactName("contact");
  }

  @Test
  public void minimalistic() {
    Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
    assertEquals("minimal resource server should have no violations", 0, violations.size());
  }

  // TODO: add tests for scopes, clients, any other?
}
