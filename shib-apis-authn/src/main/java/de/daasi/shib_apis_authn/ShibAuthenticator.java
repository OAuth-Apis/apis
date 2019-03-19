/*
 * Copyright 2014, Martin Haase, DAASI International, Germany 
 * 
 * based on org.surfnet.oaaas.conext.SAMLAuthenticator and heavily reduced 
 * (essentially removed homegrown spring security based SP and group api stuff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.daasi.shib_apis_authn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.surfnet.oaaas.auth.AbstractAuthenticator;

@Component
public class ShibAuthenticator extends AbstractAuthenticator {

//	private static final Logger LOG = LoggerFactory
//			.getLogger(ShibAuthenticator.class);

	private List<String> adminList;

	private final Properties properties;

	{
		try {
			// Use Remote_user for Principal; only set Admin uids here; could extend by more 
			// attribute mappings, e.g. for DISPLAYNAME or roles.
			properties = PropertiesLoaderUtils
					.loadAllProperties("saml.attributes.properties");
			String[] admins = properties.getProperty("adminPrincipals")
					.toLowerCase().split("\\s*,\\s*");
			adminList = Arrays.asList(admins);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			super.init(filterConfig);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	public boolean canCommence(HttpServletRequest request) {
		return false;
	}

	@Override
	public void authenticate(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			String authStateValue, String returnUri) throws IOException,
			ServletException {
//		LOG.debug("OAuthCallback: " + isOAuthCallback(request));

		String userId = request.getRemoteUser();
		if (StringUtils.isEmpty(userId)) {
			throw new ServletException("No REMOTE_USER from Shibboleth SP!");
		}
		boolean isAdmin = false;
		if (adminList.contains(userId.toLowerCase())) {
			isAdmin = true;
		}

		// TODO: could populate SAML attributes 
		// HashMap<String, String> attributes = new HashMap<String, String>();
		
		// populate User Agent details	
		UserAgent userAgent = new UserAgent(request);
		HashMap<String, String> attributes = userAgent.getAttributes();		
		
		SAMLAuthenticatedPrincipal principal = new SAMLAuthenticatedPrincipal(
				userId, new ArrayList<String>(), attributes,
				new ArrayList<String>(), null, userId, isAdmin);

		super.setPrincipal(request, principal);
		super.setAuthStateValue(request, authStateValue);
		chain.doFilter(request, response);
	}

}
