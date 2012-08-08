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

package org.surfnet.oaaas.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.surfnet.oaaas.model.VerifyTokenResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class VerifyResourceTestIT extends AbstractAuthorizationServerTest {

  @Test
  public void withNoParams() {
    final ClientResponse response = getClient()
        .resource(baseUrlWith("/v1/tokeninfo"))
        .get(ClientResponse.class);
    assertEquals(401, response.getStatus());
  }
 
  @Test
  public void withNoAuthorizationHeader() {
    final ClientResponse response = getClient()
        .resource(baseUrlWith("/v1/tokeninfo"))
        .queryParam("access_token", "boobaa")
        .get(ClientResponse.class);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void withInvalidAuthorizationHeader() {
    final ClientResponse response = getClient()
        .resource(baseUrlWith("/v1/tokeninfo"))
        .queryParam("access_token", "boobaa")
        .header("Authorization", "NotBasicButGarbage abb ccc dd")
        .get(ClientResponse.class);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void withValidAuthorizationHeaderButNoAccessToken() {
    final ClientResponse response = getClient()
        .resource(baseUrlWith("/v1/tokeninfo"))
        .header("Authorization", authorizationBasic("user", "pass"))
        .get(ClientResponse.class);
    assertEquals(401, response.getStatus());
  }

  @Test
  public void happy() {
    final ClientResponse response = getClient()
        .resource(baseUrlWith("/v1/tokeninfo"))
        .queryParam("access_token", "00-11-22-33")
        .header("Authorization", authorizationBasic("it-test-resource-server", "somesecret"))
        .get(ClientResponse.class);
    assertEquals(200, response.getStatus());
    final VerifyTokenResponse verifyTokenResponse = response.getEntity(VerifyTokenResponse.class);
    assertEquals("it-test-enduser", verifyTokenResponse.getPrincipal().getName());
  }
}
