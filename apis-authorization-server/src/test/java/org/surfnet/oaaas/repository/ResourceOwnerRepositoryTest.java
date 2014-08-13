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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.surfnet.oaaas.model.ResourceOwner;

/**
 * {@link Test} for {@link ResourceServerRepository}
 * 
 */
public class ResourceOwnerRepositoryTest extends AbstractTestRepository {

  @Test
  public void test() {
    ResourceOwnerRepository repo = getRepository(ResourceOwnerRepository.class);

    ResourceOwner ro = repo.findByUsername("emma.blunt");
    assertNotNull("Did not find expected resource owner", ro);
    
    ro = repo.findByUsername("not.here");
    assertNull("Found user that shouldn't be there", ro);
  }

  @Test
  public void findAll() {
    ResourceOwnerRepository repo = getRepository(ResourceOwnerRepository.class);
    Iterable<ResourceOwner> all = repo.findAll();
    int i = 0;
    for (ResourceOwner resourceOwner : all) {
      assertNotNull(resourceOwner);
      i++;
    }
    assertEquals(1, i);
  }

}
