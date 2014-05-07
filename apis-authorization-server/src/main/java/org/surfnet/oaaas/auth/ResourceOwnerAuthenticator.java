package org.surfnet.oaaas.auth;

import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * Defines the service contract for authentication of resource owners.
 * 
 * @author sfitts
 *
 */
public interface ResourceOwnerAuthenticator {

  /**
   * Authenticate the given resource owner credentials.
   * 
   * @param username
   *          the user name of the resource owner
   * @param password
   *          the password of the resource owner
   * @return the {@link AuthenticatedPrincipal} associated with the given credentials.  Will
   *  return {@code null} if the credentials could not be authenticated.
   */
  AuthenticatedPrincipal authenticate(String username, String password);

}
