package org.surfnet.oaaas.auth.server.support.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import org.surfnet.oaaas.auth.api.ApisAuthorization;

/**
 *
 * 
 * @author Todd Fast
 */
public class ApisAuthorizationSecurityContext implements SecurityContext {

	/**
	 *
	 *
	 * @param request
	 */
	public ApisAuthorizationSecurityContext(ContainerRequest request,
			ApisAuthorization authorization) {
		super();

		if (request==null) {
			throw new IllegalArgumentException(
				"Parameter \"request\" cannot be null");
		}

		this.request=request;
		this.authorization=authorization;
	}


	@Override
	public String getAuthenticationScheme() {
		return "oauth2"; // ???
	}


	@Override
	public Principal getUserPrincipal() {
		return authorization!=null ? authorization.getPrincipal() : null;
	}


	@Override
	public boolean isSecure() {
		// This is incorrect; this should instead be an indicator if the
		// request was over SSL. For now, assume that we always use OAuth2
		// over SSL, so if we authenticated, it must've been SSL (lame, I know).
		return getUserPrincipal()!=null;
	}


	@Override
	public boolean isUserInRole(String role) {
		return authorization!=null 
			? authorization.getPrincipal().getRoles().contains(role)
			: false;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private final ContainerRequest request;
	private ApisAuthorization authorization;
}