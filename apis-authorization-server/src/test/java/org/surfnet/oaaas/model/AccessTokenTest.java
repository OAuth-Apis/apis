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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.surfnet.oaaas.auth.api.principal.AuthenticatedPrincipal;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;

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
//System.err.println("\n\n################ emma.blunt: ["+emmaBlunt+"]\n\n");
    assertEquals(
        "rO0ABXNyADtvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLmFwaS5wcmluY2lwYWwuQXV0aGVudGljYXRlZFByaW5jaXBhbAAAAAAAAAABAgADTAAKYXR0cmlidXRlc3QAD0xqYXZhL3V0aWwvTWFwO0wABG5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAAVyb2xlc3QAFkxqYXZhL3V0aWwvQ29sbGVjdGlvbjt4cHNyAB5qYXZhLnV0aWwuQ29sbGVjdGlvbnMkRW1wdHlNYXBZNhSFWtzn0AIAAHhwdAAKZW1tYS5ibHVudHNyABpqYXZhLnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVjdDt4cHVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5+kde0cCAAB4cAAAAAJ0AAR1c2VydAAFYWRtaW4=",
        emmaBlunt);

    String adminEnduser = generateEncodedPrincipal("admin-enduser", asList);
//System.err.println("\n\n################ admin-enduser: ["+adminEnduser+"]\n\n");
    assertEquals(
        "rO0ABXNyADtvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLmFwaS5wcmluY2lwYWwuQXV0aGVudGljYXRlZFByaW5jaXBhbAAAAAAAAAABAgADTAAKYXR0cmlidXRlc3QAD0xqYXZhL3V0aWwvTWFwO0wABG5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAAVyb2xlc3QAFkxqYXZhL3V0aWwvQ29sbGVjdGlvbjt4cHNyAB5qYXZhLnV0aWwuQ29sbGVjdGlvbnMkRW1wdHlNYXBZNhSFWtzn0AIAAHhwdAANYWRtaW4tZW5kdXNlcnNyABpqYXZhLnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVjdDt4cHVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5+kde0cCAAB4cAAAAAJ0AAR1c2VydAAFYWRtaW4=",
        adminEnduser);

    String itTestEnduser = generateEncodedPrincipal("it-test-enduser", asList);
//System.err.println("\n\n################ it-test-enduser: ["+itTestEnduser+"]\n\n");
    assertEquals(
        "rO0ABXNyADtvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLmFwaS5wcmluY2lwYWwuQXV0aGVudGljYXRlZFByaW5jaXBhbAAAAAAAAAABAgADTAAKYXR0cmlidXRlc3QAD0xqYXZhL3V0aWwvTWFwO0wABG5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztMAAVyb2xlc3QAFkxqYXZhL3V0aWwvQ29sbGVjdGlvbjt4cHNyAB5qYXZhLnV0aWwuQ29sbGVjdGlvbnMkRW1wdHlNYXBZNhSFWtzn0AIAAHhwdAAPaXQtdGVzdC1lbmR1c2Vyc3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAnQABHVzZXJ0AAVhZG1pbg==",
        itTestEnduser);
  }

  @Test
  public void marshallToJsonShouldHideSomeMembers() throws JAXBException {
    AccessToken token = getToken(new AuthenticatedPrincipal("Truus"));
    token.setClient(createClient("my-client-id", "my-client-secret"));

    String json = marshallToJson(token);

    assertTrue(json.contains("my-client-id"));
    assertFalse(json.contains("my-client-secret"));
    assertFalse(json.contains("principal"));
    assertFalse(json.contains("encodedPrincipal"));
  }

  private String marshallToJson(AccessToken token) throws JAXBException {
    JSONJAXBContext context = new JSONJAXBContext(AccessToken.class);
    JSONMarshaller marshaller = context.createJSONMarshaller();

    StringWriter writer = new StringWriter();
    marshaller.marshallToJSON(token, writer);

    return writer.toString();
  }

  private Client createClient(String id, String secret) {
    Client client = new Client();
    client.setClientId(id);
    client.setSecret(secret);
    return client;
  }

  private String generateEncodedPrincipal(String name, Collection<String> roles) {
    AuthenticatedPrincipal principal = new AuthenticatedPrincipal(name, roles);
    AccessToken token = getToken(principal);
    token.encodePrincipal();
    return token.getEncodedPrincipal();
  }

  private AccessToken getToken(AuthenticatedPrincipal principal) {
    return new AccessToken(UUID.randomUUID().toString(), principal, new Client(), 0, Arrays.asList("read","update"));
  }

}
