package org.surfnet.oaaas.auth.api;

import java.util.List;
import java.util.Map;

/**
 *
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
