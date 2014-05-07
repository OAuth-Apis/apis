package org.surfnet.oaaas.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.ResourceOwner;
import org.surfnet.oaaas.repository.ResourceOwnerRepository;

public class LocalResourceOwnerAuthenticatorTest {

  private static final String PASSWORD = "password";

  @Mock
  private ResourceOwnerRepository resourceOwnerRepository;
  
  @InjectMocks
  private LocalResourceOwnerAuthenticator authenticator = new LocalResourceOwnerAuthenticator();
  
  private ResourceOwner resourceOwner;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    this.resourceOwner = createResourceOwner("username");
    when(resourceOwnerRepository.findByUsername(this.resourceOwner.getUsername())).thenReturn(resourceOwner);
  }

  @Test
  public void testAuthenticate() {
    AuthenticatedPrincipal principal = 
        this.authenticator.authenticate(this.resourceOwner.getUsername(), PASSWORD);
    assertNotNull(principal);
    assertEquals("Principal does not have expected name", this.resourceOwner.getUsername(),
        principal.getName());
  }

  @Test
  public void testAuthenticateBadUser() {
    AuthenticatedPrincipal principal = this.authenticator.authenticate("foo", PASSWORD);
    assertNull(principal);
  }

  @Test
  public void testAuthenticateBadPassword() {
    AuthenticatedPrincipal principal = 
        this.authenticator.authenticate(this.resourceOwner.getUsername(), "bad");
    assertNull(principal);
  }

  private ResourceOwner createResourceOwner(String username) {
    ResourceOwner resourceOwner = new ResourceOwner();
    resourceOwner.setUsername(username);
    resourceOwner.setPassword(PASSWORD);
    return resourceOwner;
  }
}
