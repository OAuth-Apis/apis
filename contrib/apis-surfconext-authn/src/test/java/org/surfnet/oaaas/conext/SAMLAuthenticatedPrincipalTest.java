package org.surfnet.oaaas.conext;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SAMLAuthenticatedPrincipalTest {

  @Test
  public void testSerialization() throws IOException {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key", "value");
    String identityProvider = "http://universiteit-hardewijk";
    String displayName = "gebruiker.pi";
    AuthenticatedPrincipal principal = new SAMLAuthenticatedPrincipal("ud.id.name.pi", Arrays.asList(new String[]{"USER", "ADMIN"}), attributes, Arrays.asList(new String[]{"id.group.1", "id.group.2", "id.group.3"}), identityProvider, displayName, true);
    String json = principal.serialize();
    SAMLAuthenticatedPrincipal samlPrincipal = (SAMLAuthenticatedPrincipal) AuthenticatedPrincipal.deserialize(json);
    assertTrue(samlPrincipal.isGroupAware());
    assertEquals(identityProvider, samlPrincipal.getIdentityProvider());
    assertEquals(displayName, samlPrincipal.getDisplayName());
    assertTrue(samlPrincipal.isAdminPrincipal());
  }

}
