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
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import static org.junit.Assert.assertEquals;

/**
 * Test to generate {@link Base64} encoded and decoded {@link String} for
 * creating valid test data in the in-memory database.
 * 
 */
public class AccessTokenTest {

  /**
   * Test method for
   * {@link org.surfnet.oaaas.model.AccessToken#encodePrincipal()}.
   */
  @Test
  public void testEncodePrincipal() {
    List<String> asList = Arrays.asList("user", "admin");

    String emmaBlunt = generateEncodedPrincipal("emma.blunt", asList);
    assertEquals(
        "rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AAplbW1hLmJsdW50c3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAnQABHVzZXJ0AAVhZG1pbg==",
        emmaBlunt);

    String adminEnduser = generateEncodedPrincipal("admin-enduser", asList);
    assertEquals(
        "rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AA1hZG1pbi1lbmR1c2Vyc3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAnQABHVzZXJ0AAVhZG1pbg==",
        adminEnduser);

    String itTestEnduser = generateEncodedPrincipal("it-test-enduser", asList);
    assertEquals(
        "rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AA9pdC10ZXN0LWVuZHVzZXJzcgAaamF2YS51dGlsLkFycmF5cyRBcnJheUxpc3TZpDy+zYgG0gIAAVsAAWF0ABNbTGphdmEvbGFuZy9PYmplY3Q7eHB1cgATW0xqYXZhLmxhbmcuU3RyaW5nO63SVufpHXtHAgAAeHAAAAACdAAEdXNlcnQABWFkbWlu",
        itTestEnduser);
  }

  private String generateEncodedPrincipal(String name, Collection<String> roles) {
    AuthenticatedPrincipal principal = new AuthenticatedPrincipal(name, roles);
    AccessToken token = getToken(principal);
    token.encodePrincipal();
    return token.getEncodedPrincipal();
  }

  private AccessToken getToken(AuthenticatedPrincipal principal) {
    return new AccessToken(UUID.randomUUID().toString(), principal, new Client(), 0, "read,update");
  }

}
