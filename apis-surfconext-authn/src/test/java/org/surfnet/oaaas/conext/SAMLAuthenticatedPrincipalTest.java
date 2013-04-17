package org.surfnet.oaaas.conext;

import org.junit.Test;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SAMLAuthenticatedPrincipalTest {

  @Test
  public void testSerialization() throws IOException {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key", "value");
    AuthenticatedPrincipal principal = new SAMLAuthenticatedPrincipal("ud.id.name.pi", Arrays.asList(new String[]{"USER", "ADMIN"}), attributes, Arrays.asList(new String[]{"id.group.1", "id.group.2", "id.group.3"}));
    SAMLAuthenticatedPrincipal samlPrincipal = (SAMLAuthenticatedPrincipal) AuthenticatedPrincipal.deserialize(principal.serialize());
    assertTrue(samlPrincipal.isGroupAware());


  }

}
