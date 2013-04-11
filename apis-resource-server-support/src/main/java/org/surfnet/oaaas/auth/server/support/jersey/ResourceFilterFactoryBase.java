package org.surfnet.oaaas.auth.server.support.jersey;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;

/**
 *
 * 
 * @author Todd Fast
 */
public class ResourceFilterFactoryBase implements ResourceFilterFactory {

	/**
	 *
	 *
	 * @param method
	 * @return
	 */
	@Override
	public List<ResourceFilter> create(AbstractMethod method) {
		return Collections.<ResourceFilter>singletonList(new Filter(method));
	}


	/**
	 *
	 *
	 * @param method
	 * @return
	 */
	protected String getMethodName(AbstractMethod method) {
		StringBuilder result=new StringBuilder();

		if (method.getResource()!=null && method.getResource().getPath()!=null)
			result.append(method.getResource().getPath().getValue());

		result.append(":")
			.append(method.getMethod().getName())
			.append("()");

		return result.toString();
	}


	/**
	 *
	 *
	 */
	protected ContainerRequest filterRequest(AbstractMethod method,
			ContainerRequest request) {
		return request;
	}


	/**
	 *
	 *
	 */
	protected ContainerResponse filterResponse(AbstractMethod method,
			ContainerRequest request, ContainerResponse response) {
		return response;
	}




	////////////////////////////////////////////////////////////////////////////
	// Inner type
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	private class Filter
		implements ResourceFilter, ContainerRequestFilter,
			ContainerResponseFilter
	{
		public Filter(AbstractMethod method) {
			super();
			this.method=method;
		}

		@Override
		public ContainerRequestFilter getRequestFilter() {
			return this;
		}

		@Override
		public ContainerResponseFilter getResponseFilter() {
			return this;
		}

		@Override
		public ContainerRequest filter(ContainerRequest request) {
			return filterRequest(method,request);
		}

		@Override
		public ContainerResponse filter(ContainerRequest request,
				ContainerResponse response) {
			return filterResponse(method,request,response);
		}

		private AbstractMethod method;
	}
}