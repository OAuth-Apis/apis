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

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.fromJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ResourceServerTest {

  @Test
  public void serializesToJSON() throws Exception {
    final ResourceServer r = new ResourceServer();
    r.setId(1L);
    r.setContactName("Geert");
    r.setName("myname");
    r.setOwner("owner");
    assertThat("a resource server can be serialized to JSON",
        asJson(r),
        is(equalTo(jsonFixture("fixtures/resourceServer1.json"))));
  }

  @Test
  public void deserializesFromJSON() throws Exception {
    final ResourceServer r = new ResourceServer();
    r.setId(1L);
    assertThat("a resource server can be deserialized from JSON",
        fromJson(jsonFixture("fixtures/resourceServer1.json"), ResourceServer.class),
        is(r));
  }

  
}
