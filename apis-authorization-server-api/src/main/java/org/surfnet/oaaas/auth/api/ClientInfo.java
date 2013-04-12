package org.surfnet.oaaas.auth.api;

import java.util.List;
import java.util.Map;

/**
 * Information about the client application requesting authorization.
 * Represents a Client as defined by the OAuth 2 specification:
 *
 * <pre>
 *         An application making protected resource requests on behalf of the resource owner and with its
 *         authorization.  The term client does not imply any particular implementation characteristics (e.g. whether
 *         the application executes on a server, a desktop, or other devices).
 * </pre>
 * 
 * @see org.surfnet.oaaas.model.Client
 * @author Todd Fast
 */
public interface ClientInfo {


	/**
	 *
	 *
	 */
	public String getName();


	/**
	 *
	 *
	 */
	public String getDescription();


	/**
	 *
	 *
	 */
	public String getContactName();


	/**
	 *
	 *
	 */
	public String getContactEmail();


	/**
	 *
	 *
	 */
	public String getThumbNailUrl();


	/**
	 *
	 *
	 */
	public boolean isSkipConsent();


	/**
	 *
	 *
	 */
	public String getClientId();


	/**
	 *
	 *
	 */
	public String getSecret();


	/**
	 *
	 *
	 */
	public List<String> getRedirectUris();


	/**
	 *
	 *
	 */
	public boolean isUseRefreshTokens();


	/**
	 *
	 *
	 */
	public long getExpireDuration();


	/**
	 *
	 *
	 */
	public Map<String, String> getAttributes();


	/**
	 *
	 *
	 */
	public boolean isNotAllowedImplicitGrant();


	/**
	 *
	 *
	 */
	public List<String> getScopes();
}
