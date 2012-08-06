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

import org.apache.commons.codec.binary.Base64;


public abstract class AbstractAuthorizationServerTest {

  protected int defaultServletPort = 8080;

  public String baseUrl() {
    return String.format("http://localhost:%s",
        System.getProperty("servlet.port", String.valueOf(defaultServletPort)));
  }

  public String baseUrlWith(String suffix) {
    return baseUrl().concat(suffix);
  }

  public static String authorizationBasic(String username, String password) {
    String concatted = username + ":" + password;
    return "Basic " + new String(Base64.encodeBase64(concatted.getBytes()));
  }
  public static String authorizationBearer(String token) {
    return "bearer " + token;
  }
}
