package org.surfnet.oaaas.auth.server.support.filter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import org.surfnet.oaaas.auth.server.support.model.ApisAuthorization;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

//import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;

//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.Base64;
import java.io.InputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * {@link Filter} which can be used to protect all relevant resources by
 * validating the oauth access token with the Authorization server. This is an
 * example configuration:
 * <p/>
 * <pre>
 * {@code
 * <filter>
 *   <filter-name>authorization-server</filter-name>
 *   <filter-class>org.surfnet.oaaas.auth.server.support.filter.ApisAuthorizationFilter</filter-class>
 *   <init-param>
 *     <param-name>resource-server-key</param-name>
 *     <param-value>university-foo</param-value>
 *   </init-param>
 *   <init-param>
 *     <param-name>resource-server-secret</param-name>
 *     <param-value>58b749f7-acb3-44b7-a38c-53d5ad740cf6</param-value>
 *   </init-param>
 *   <init-param>
 *     <param-name>authorization-server-url</param-name>
 *     <param-value>http://[host-name]/v1/tokeninfo</param-value>
 *   </init-param>
 * </filter>
 * <filter-mapping>
 *   <filter-name>authorization-server</filter-name>
 *  <url-pattern>/*</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 * <p/>
 * The response of the Authorization Server is put on the
 * {@link HttpServletRequest} with the name
 * {@link ApisAuthorizationFilter#apisAuthorization}.
 * <p/>
 * Of course it might be better to use a properties file depending on the
 * environment (e.g. OTAP) to get the name, secret and url. This can be achieved
 * simple to override the {@link AuthorizationServerFilter#init(FilterConfig)}
 * <p/>
 * Also note that by default the responses from the Authorization Server are
 * cached. This can easily be changed if you override
 * {@link ApisAuthorizationFilter#cacheAccessTokens()} and to configure the
 * cache differently override {@link ApisAuthorizationFilter#buildCache()}
 */
public class ApisAuthorizationFilter implements Filter {

	private static final Logger LOG=
		LoggerFactory.getLogger(ApisAuthorizationFilter.class);

	/*
	 * Endpoint of the authorization server (e.g. something like
	 * http://<host-name>/v1/tokeninfo)
	 */
	private String authorizationServerUrl;

	/*
	 * Base64-encoded concatenation of the name of the resource server and the
	 * secret separated with a colon
	 */
	private String authorizationValue;

	/*
	 * Client to make GET calls to the authorization server
	 */
	private Client client;

	/*
	 * Constant for the access token (oauth2 spec)
	 */
	private static final String BEARER="bearer";

	/*
	 * Constant name of the request attribute where the response is stored
	 */
	public static final String ATTR_AUTHORIZATION="apisAuthorization";

	/*
	 * If not overridden by a subclass we cache the answers from the authorization
	 * server
	 */
//	private Cache<String,VerifyTokenResponse> cache;

	/*
	 * Key and secret obtained out-of-band to authenticate against the
	 * authorization server
	 */
	private String resourceServerKey;
	private String resourceServerSecret;
	private ObjectMapper objectMapper;


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		/*
		 * First check on the presence of a apis.application.properties file, then
		 * try to use the filter config if parameters are present. If this also
		 * fails trust on the setters (e.g. probably in test modus), but apply
		 * fail-fast strategy
		 */
		InputStream inputStream=filterConfig.getServletContext()
			.getResourceAsStream("api.application.properties");

		if (inputStream!=null) {
			Properties prop=new Properties();
			try {
				prop.load(inputStream);
			}
			catch (IOException e) {
				throw new RuntimeException(
					"Error in reading the apis.application.properties file",e);
			}
			resourceServerKey=prop.getProperty("adminService.resourceServerKey");
			resourceServerSecret=prop.getProperty(
				"adminService.resourceServerSecret");
			authorizationServerUrl=prop.getProperty(
				"adminService.tokenVerificationUrl");
		}
		else {
			if (filterConfig.getInitParameter("resource-server-key")!=null) {
				resourceServerKey=filterConfig.getInitParameter(
					"resource-server-key");
				resourceServerSecret=filterConfig.getInitParameter(
					"resource-server-secret");
				authorizationServerUrl=filterConfig.getInitParameter(
					"authorization-server-url");
			}
		}

		if (resourceServerKey==null || resourceServerKey.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Must provide a resource server key");
		}

		if (resourceServerSecret==null ||
				resourceServerSecret.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Must provide a resource server secret");
		}

		if (authorizationServerUrl==null ||
				authorizationServerUrl.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Must provide a authorization server url");
		}

		this.authorizationValue=new String(
			Base64.encode(resourceServerKey+":"+resourceServerSecret));

// TAF: Removed Guava dependency
//		if (cacheAccessTokens()) {
//			this.cache=buildCache(false);
//		}

		client=createClient();

		objectMapper=createObjectMapper();
	}


	protected ObjectMapper createObjectMapper() {
		return new ObjectMapperProvider().getContext(ObjectMapper.class);
	}


	/**
	 * @return Client
	 */
	protected Client createClient() {
		ClientConfig cc=new DefaultClientConfig();
		cc.getClasses().add(ObjectMapperProvider.class);
		return Client.create(cc);
	}


// TAF: Removed Guava dependency
//	@SuppressWarnings({"rawtypes","unchecked"})
//	protected Cache<String,VerifyTokenResponse> buildCache(boolean recordStats) {
//		CacheBuilder cacheBuilder=CacheBuilder.newBuilder().maximumSize(100000).
//			expireAfterAccess(10,TimeUnit.MINUTES);
//		return recordStats ? cacheBuilder.recordStats().build() : cacheBuilder.
//			build();
//	}


	@Override
	public void doFilter(ServletRequest servletRequest,
		ServletResponse servletResponse,FilterChain chain)
		throws IOException,ServletException {
		HttpServletRequest request=(HttpServletRequest)servletRequest;
		HttpServletResponse response=(HttpServletResponse)servletResponse;
		/*
		 * The Access Token from the Client app as documented in
		 * http://tools.ietf.org/html/draft-ietf-oauth-v2#section-7
		 */
		final String accessToken=getAccessToken(request);
		if (accessToken==null) {
			LOG.warn(
				"No accesstoken on request. Will respond with error response");
			sendError(response,HttpServletResponse.SC_FORBIDDEN,
				"OAuth2 endpoint requires valid access token");
			return;
		}
		else {
			ApisAuthorization tokenResponse=null;
			/*
			 * Get the 'Validate Access Token' response from the Authorization Server
			 * either live or from the cache
			 */
			try {
				Callable<ApisAuthorization> verifyCall=getCallable(accessToken,
					response);
// TAF: Removed Guava dependency
//				tokenResponse=cacheAccessTokens() ? cache.get(accessToken,
//					verifyCall) : verifyCall.call();
				tokenResponse=verifyCall.call();
			}
			catch (Exception e) {
				LOG.error("While validating access token",e);
				sendError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Cannot verify access token");
				return;
			}

			/*
			 * The presence of the principal is the check to ensure that the 
			 * access token is ok.
			 */
			if (tokenResponse!=null && tokenResponse.getPrincipal()!=null) {
				request.setAttribute(ATTR_AUTHORIZATION,tokenResponse);
				chain.doFilter(request,response);
				return;
			}
		}

		sendError(response,HttpServletResponse.SC_FORBIDDEN,"OAuth2 endpoint");
	}


	private Callable<ApisAuthorization> getCallable(final String accessToken,
		final HttpServletResponse response) {
		return new Callable<ApisAuthorization>() {
			@Override
			public ApisAuthorization call() throws Exception {
				return getVerifyTokenResponse(accessToken,response);
			}
		};
	}


	protected ApisAuthorization getVerifyTokenResponse(String accessToken,
		final HttpServletResponse response) {
		ClientResponse res=client.resource(String.format("%s?access_token=%s",
			authorizationServerUrl,accessToken))
			.header(HttpHeaders.AUTHORIZATION,"Basic "+authorizationValue)
			.accept("application/json")
			.get(ClientResponse.class);

		/*
		 * Can't use directly jersey, as we need the mr bean module
		 */
		try {
			String responseString=res.getEntity(String.class);
			LOG.debug("Got verify token response (status: {}): '{}'",res.
				getClientResponseStatus().getStatusCode(),responseString);
			return objectMapper.readValue(responseString,
				ApisAuthorization.class);
		}
		catch (Exception e) {
			LOG.warn("Could not parse the Verify Token Response",e);
			sendError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Cannot parse result");
			return new ApisAuthorization(e.getMessage());
		}
	}


	protected void sendError(HttpServletResponse response,int statusCode,
		String reason) {
		try {
			response.sendError(statusCode,reason);
			response.flushBuffer();
		}
		catch (IOException e) {
			throw new RuntimeException(reason,e);
		}
	}


	protected boolean cacheAccessTokens() {
		return false;
	}


	private String getAccessToken(HttpServletRequest request) {
		String accessToken=null;
		String header=request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header!=null) {
			int space=header.indexOf(' ');
			if (space>0) {
				String method=header.substring(0,space);
				if (BEARER.equalsIgnoreCase(method)) {
					accessToken=header.substring(space+1);
				}
			}
		}
		else {
			// TAF: Fall back to looking for the access token as a query
			// parameter
			accessToken=request.getParameter("access_token");
		}

		return accessToken;
	}


	@Override
	public void destroy() {
	}


	/**
	 * @return the cache
	 */
// TAF: Removed Guava dependency
//	public Cache<String,VerifyTokenResponse> getCache() {
//		return cache;
//	}


	public void setAuthorizationServerUrl(String authorizationServerUrl) {
		this.authorizationServerUrl=authorizationServerUrl;
	}


	public void setResourceServerSecret(String resourceServerSecret) {
		this.resourceServerSecret=resourceServerSecret;
	}


	public void setResourceServerKey(String resourceServerKey) {
		this.resourceServerKey=resourceServerKey;
	}
}
