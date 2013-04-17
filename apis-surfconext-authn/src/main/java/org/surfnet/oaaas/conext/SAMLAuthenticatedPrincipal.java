package org.surfnet.oaaas.conext;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.util.CollectionUtils;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class SAMLAuthenticatedPrincipal extends AuthenticatedPrincipal {

  public SAMLAuthenticatedPrincipal() {
  }

  public SAMLAuthenticatedPrincipal(String username) {
    super(username);
  }

  public SAMLAuthenticatedPrincipal(String username, Collection<String> roles) {
    super(username, roles);
  }

  public SAMLAuthenticatedPrincipal(String username, Collection<String> roles, Map<String, String> attributes) {
    super(username, roles, attributes);
  }

  public SAMLAuthenticatedPrincipal(String username, Collection<String> roles, Map<String, String> attributes, Collection<String> groups) {
    super(username, roles, attributes, groups);
  }

}
