package org.surfnet.oaaas.noop;

import org.surfnet.oaaas.auth.ResourceOwnerAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * Minimal implementation of {@link ResourceOwnerAuthenticator} designed to satisfy the contract.
 * Useful for testing or demonstration purposes, but clearly not fit for production.
 * 
 * @author sfitts
 *
 */
public class NoopResourceOwnerAuthenticator implements ResourceOwnerAuthenticator {
  
  public static final String BAD_USER = "xxxBAD USERxxx";

  @Override
  public AuthenticatedPrincipal authenticate(String username, String password) {
    if (username == null) {
      throw new IllegalArgumentException("Must supply a non-null user name");
    }
    
    if (password == null) {
      throw new IllegalArgumentException("Must supply a non-null password.");
    }
    
    // Is this our bad user?
    if (BAD_USER.equals(username)) {
      return null;
    }
    return new AuthenticatedPrincipal(username);
  }

}
