package org.surfnet.oaaas.auth;

import javax.inject.Inject;

import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.ResourceOwner;
import org.surfnet.oaaas.repository.ResourceOwnerRepository;

/**
 * Implementation of {@link ResourceOwnerAuthenticator} which uses the local auth server model
 * to authenticate.
 * 
 * @author sfitts
 *
 */
public class LocalResourceOwnerAuthenticator implements ResourceOwnerAuthenticator {
  
  @Inject
  private ResourceOwnerRepository resourceOwnerRepository;

  @Override
  public AuthenticatedPrincipal authenticate(String username, String password) {
    ResourceOwner user = resourceOwnerRepository.findByUsername(username);
    if (user == null) {
      return null;
    }
    
    // Validate password
    if (!user.checkPassword(password)) {
      return null;
    }
    return new AuthenticatedPrincipal(username);
  }

}
