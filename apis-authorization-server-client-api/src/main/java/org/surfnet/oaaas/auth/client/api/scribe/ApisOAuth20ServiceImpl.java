package org.surfnet.oaaas.auth.client.api.scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;

/**
 *
 * 
 * @author Todd Fast
 */
/*pkg*/ class ApisOAuth20ServiceImpl extends OAuth20ServiceImpl {

	/**
	 * Default constructor
	 * <p/>
	 * @param api OAuth2.0 api information
	 * @param config OAuth 2.0 configuration param object
	 */
	protected ApisOAuth20ServiceImpl(AbstractApisApi api, OAuthConfig config) {
		super(api,config);
		this.api=api;
		this.config=config;
	}


	/**
	 * {@inheritDoc}
	 * <p/>
	 */
	public Token getAccessToken(Token requestToken, Verifier verifier) {
		OAuthRequest request=new OAuthRequest(api.getAccessTokenVerb(),
			api.getAccessTokenEndpoint());

		// Apis expects all parameters to be form encoded in the body, so
		// these aren't strictly necessary, but are helpful for debugging
		// as they are logged
		request.addQuerystringParameter("grant_type","authorization_code");
		request.addQuerystringParameter(OAuthConstants.CLIENT_ID,
			config.getApiKey());
		request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET,
			config.getApiSecret());
		request.addQuerystringParameter(OAuthConstants.CODE,
			verifier.getValue());
		request.addQuerystringParameter(OAuthConstants.REDIRECT_URI,
			config.getCallback());
		if (config.hasScope()) {
			request.addQuerystringParameter(OAuthConstants.SCOPE,
				config.getScope());
		}

		// Note, apis expects the grant_type parameter to be specified in the
		// body instead of as a query parameter (as is common)
		request.addBodyParameter("grant_type","authorization_code");
		request.addBodyParameter(OAuthConstants.CLIENT_ID,
			config.getApiKey());
		request.addBodyParameter(OAuthConstants.CLIENT_SECRET,
			config.getApiSecret());
		request.addBodyParameter(OAuthConstants.CODE,
			verifier.getValue());
		request.addBodyParameter(OAuthConstants.REDIRECT_URI,
			config.getCallback());
		if (config.hasScope()) {
			request.addBodyParameter(OAuthConstants.SCOPE,
				config.getScope());
		}

		Response response=request.send();

		return api.getAccessTokenExtractor().extract(response.getBody());
	}


	private final AbstractApisApi api;
	private final OAuthConfig config;
}
