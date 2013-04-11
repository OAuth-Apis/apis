package org.surfnet.oaaas.auth.client.api.scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.extractors.TokenExtractor20Impl;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;

/**
 * A Scribe OAuth2 API for use with an apis authorization server
 * 
 * @author Todd Fast
 */
public class AbstractApisApi extends DefaultApi20 {

	/**
	 * Constructs the API instance with a specified base server URL. Note, a
	 * constructed API instance can be used with Scribe's {@link ServiceBuilder}
	 * instead of a class.
	 *
	 * @param	baseURL
	 *			The base URL, comprising the protocol, server, and optionally
	 *			port, with or without an ending slash. For example, when
	 *			running apis locally on port 8080, the base URL would be
	 *			"http://localhost:8080".
	 */
	public AbstractApisApi(String baseURL) {
		super();
		if (baseURL==null) {
			throw new IllegalArgumentException(
				"Parameter \"baseURI\" cannot be null");
		}

		// Q: Is it correct for baseURI to be "" such that the following URLs
		// are relative?

		this.baseURL=baseURL.endsWith("/") ? baseURL : baseURL+"/";
		authorizeURL=baseURL+
			"oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s";
		scopedAuthorizeURL=authorizeURL+"&scope=%s";
//		accessTokenEndpoint=baseURI+"oauth2/token?grant_type=authorization_code";
		accessTokenEndpoint=baseURL+"oauth2/token";
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}


	@Override
	public OAuthService createService(OAuthConfig config) {
		return new ApisOAuth20ServiceImpl(this,config);
	}


	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}


	@Override
	public String getAccessTokenEndpoint() {
		return accessTokenEndpoint;
	}


	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		// Append scope if present
		if (config.hasScope()) {
			return String.format(scopedAuthorizeURL,config.getApiKey(),
				OAuthEncoder.encode(config.getCallback()),
				OAuthEncoder.encode(config.getScope()));
		}
		else {
			return String.format(authorizeURL,config.getApiKey(),
				OAuthEncoder.encode(config.getCallback()));
		}
	}


	private final String baseURL;
	private final String authorizeURL;
	private final String scopedAuthorizeURL;
	private final String accessTokenEndpoint;
}
