package org.surfnet.oaaas.auth.server.support.jersey;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import org.surfnet.oaaas.auth.server.support.filter.ApisAuthorizationFilter;
import org.surfnet.oaaas.auth.server.support.model.ApisAuthorization;

/**
 * Injects the current ApisAuthorization into the requesting method or type
 *
 * @author Todd Fast
 */
@Provider
public class ApisAuthorizationInjector
	extends PerRequestTypeInjectableProvider<Context, ApisAuthorization>
{
	/**
	 *
	 *
	 * @param resourceContext
	 * @param httpContext
	 */
	public ApisAuthorizationInjector(@Context ResourceContext resourceContext,
		@Context HttpContext httpContext, @Context HttpServletRequest request)
	{
		super(ApisAuthorization.class);
		this.resourceContext=resourceContext;
		this.httpContext=httpContext;
		this.request=request;
	}


	/**
	 *
	 *
	 */
	@Override
	public Injectable<ApisAuthorization> getInjectable(
		final ComponentContext componentContext, final Context context)
	{
		return new Injectable<ApisAuthorization>() {
			@Override
			public ApisAuthorization getValue() {
				return (ApisAuthorization)request.getAttribute(
					ApisAuthorizationFilter.ATTR_AUTHORIZATION);
			}
		};
	}

	private final ResourceContext resourceContext;
	private final HttpContext httpContext;
	private HttpServletRequest request;
}