package org.surfnet.oaaas.conext;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class SAMLAuthenticatedPrincipal extends AuthenticatedPrincipal implements UserDetails{

  @JsonIgnore
  private final static String IDENTITY_PROVIDER = "IDENTITY_PROVIDER";

  @JsonIgnore
  private final static String DISPLAY_NAME = "DISPLAY_NAME";

  public SAMLAuthenticatedPrincipal() {
  }

  public SAMLAuthenticatedPrincipal(String username, Collection<String> roles, Map<String, String> attributes, Collection<String> groups, String identityProvider, String displayName, boolean adminPrincipal) {
    super(username, roles, attributes, groups);
    addAttribute(IDENTITY_PROVIDER, identityProvider);
    addAttribute(DISPLAY_NAME, displayName);
    setAdminPrincipal(adminPrincipal);
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    if (!CollectionUtils.isEmpty(getRoles())) {
      for (final String role : getRoles()) {
        authorities.add(new GrantedAuthority(){
          public String getAuthority() {
            return role;
          }
        });
      }
    }
    return authorities;
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    throw new RuntimeException("SAML based authentication does not support passwords on the receiving end");
  }

  @JsonIgnore
  @Override
  public String getUsername() {
    return getName();
  }

  @Override
  public String getDisplayName() {
    return getAttributes().get(DISPLAY_NAME);
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return true;
  }

  @JsonIgnore
  public String getIdentityProvider() {
    return getAttributes().get(IDENTITY_PROVIDER);
  }

}
