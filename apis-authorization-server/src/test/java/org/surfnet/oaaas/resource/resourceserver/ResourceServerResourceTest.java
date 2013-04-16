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

package org.surfnet.oaaas.resource.resourceserver;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.resource.resourceserver.ResourceServerResource;

public class ResourceServerResourceTest {
  private ResourceServerResource resourceServerResource;

  @Before
  public void setUp() throws Exception {
    resourceServerResource = new ResourceServerResource();
  }

  @Test
  public void pruneScopes() {
    Client client1 = new Client();
    client1.setScopes(Arrays.asList("scope1"));
    Client client2 = new Client();
    client2.setScopes(Arrays.asList("scope1", "scope2"));

    Set<Client> clients = new HashSet(Arrays.asList(client1, client2));

    List<String> oldScopes = Arrays.asList("scope1");
    List<String> newScopes = Arrays.asList("scope2");

    resourceServerResource.pruneClientScopes(newScopes, oldScopes, clients);

    assertEquals(0, client1.getScopes().size());
    assertEquals(1, client2.getScopes().size());
    assertEquals("scope2", client2.getScopes().get(0));
  }
}
