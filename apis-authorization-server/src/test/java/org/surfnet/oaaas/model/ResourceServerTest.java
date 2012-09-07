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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
  public void validateMinimalistic() {
    Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
    assertEquals("minimal resource server should have no violations", 0, violations.size());
  }

  @Test
  public void validateLessThanMinimal() {
    resourceServer = new ResourceServer();
    Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
    assertEquals("Empty resource server fails on 4 NotNull-fields", 4, violations.size());
  }

  @Test
  public void validateScopes() {
    {
      resourceServer.setScopes(Arrays.asList("read", "write"));
      Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
      assertEquals("valid scopes should yield no violations", 0, violations.size());
    }
    {
      resourceServer.setScopes(Arrays.asList("exotic string123456!<>./?@#$%^&*()_+[];\""));
      Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
      assertEquals("Even an exotic scope name is allowed", 0, violations.size());
    }
    {
      resourceServer.setScopes(Arrays.asList("with,a,comma,"));
      Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
      assertEquals("comma is not allowed", 1, violations.size());
    }
  }

  @Test
  public void validateEmail() {
    resourceServer.setContactEmail("foo@example.com");
    Set<ConstraintViolation<ResourceServer>> violations = validator.validate(resourceServer);
    assertEquals("valid email should yield no violations", 0, violations.size());

    resourceServer.setContactEmail("invalid email address");
    violations = validator.validate(resourceServer);
    assertEquals("invalid email should trigger violation", 1, violations.size());

  }

  @Test
  public void hasClient() {
    Client c = new Client();
    c.setName("clientname");
    resourceServer.setClients(new HashSet<Client>( Arrays.asList(new Client(), c, new Client())));
    assertThat(resourceServer.containsClient(c), is(true));
    assertThat(resourceServer.containsClient(new Client()), is(false));
  }
}
