package org.surfnet.oaaas.auth.server.support.jersey;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.surfnet.oaaas.auth.server.support.annotation.AnonymousAccessAllowed;
import org.surfnet.oaaas.auth.server.support.filter.ApisAuthorizationFilter;
import org.surfnet.oaaas.auth.server.support.model.ApisAuthorization;
import org.surfnet.oaaas.auth.server.support.model.AuthenticatedPrincipal;

/**
 * A Jersey filter factory that provides a {@link SecurityContext} containing
 * apis authorization information into the request.
 * <p/>
 * To use, you must add the following to your Jersey servlet or filter 
 * configuration in web.xml (note that multiple resource filters should be
 * comma-separated):
 * <p/>
 * <pre>
 * {@code
 * 	<init-param>
 *		<param-name>com.sun.jersey.spi.container.ResourceFilters</param-name>
 *		<param-value>org.surfnet.oaaas.auth.server.support.jersey.ApisAuthorizationResourceFilterFactory</param-value>
 *	</init-param>
 * }
 * </pre>
 * 
 * @author Todd Fast
 */
public class ApisAuthorizationResourceFilterFactory 
		extends ResourceFilterFactoryBase {

	/**
	 *
	 *
	 * @param httpRequest
	 */
	public ApisAuthorizationResourceFilterFactory(
			@Context HttpServletRequest httpRequest) {
		super();

		// FYI, the lifecycle of a filter factory is as a singleton, but this
		// request object is a threadsafe proxy that will be provisioned per
		// request so that we can use it safely below. Neato, eh?
		this.httpRequest=httpRequest;
	}


	/**
	 *
	 *
	 */
	@Override
	protected ContainerRequest filterRequest(AbstractMethod method,
			ContainerRequest request) {

		ApisAuthorization authorization=(ApisAuthorization)
			httpRequest.getAttribute(
				ApisAuthorizationFilter.ATTR_AUTHORIZATION);

		// Set the security context
		ApisAuthorizationSecurityContext securityContext=
			securityContext=new ApisAuthorizationSecurityContext(request,
				authorization);
		request.setSecurityContext(securityContext);

		// Verify that the resource method can be called without a user
		AuthenticatedPrincipal user=(AuthenticatedPrincipal)
			securityContext.getUserPrincipal();

		if (user!=null) {
			return request;
		}
		else
		// Ignore the presence of user, which we may want to get if it's
		// available in the case of anonymous-accessible methods
		if (/*user==null &&*/ method.isAnnotationPresent(
			AnonymousAccessAllowed.class)) {
			return request;
		}
		else
		if (method.getMethod().getName().equals("getWadl")) {
			// Allow the generated WADL to bypass the filter
			return request;
		}
		else {
			// A user was required but not found, and/or no annotation
			throw new WebApplicationException(
				Response
					.status(Status.UNAUTHORIZED)
					.entity("Authentication required")
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build());
		}
	}

	private HttpServletRequest httpRequest;
}
