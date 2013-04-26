/*
 * Copyright 2012 SURFnet bv, The Netherlands
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

package org.surfnet.oaaas.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.surfnet.oaaas.auth.principal.UserPassCredentials;

/**
 * Represents a Client as defined by the OAuth 2 specification:
 *
 * <pre>
 *         An application making protected resource requests on behalf of the resource owner and with its
 *         authorization.  The term client does not imply any particular implementation characteristics (e.g. whether
 *         the application executes on a server, a desktop, or other devices).
 * </pre>
 */

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(name = "client")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Client extends AbstractEntity {

  @Column(name = "clientName")
  @NotNull
  private String name;

  @Column(unique = true)
  private String clientId;

  @Column
  private String secret;

  @Column
  private String description;

  @Column
  private String contactName;

  @Column
  private String contactEmail;

  @ElementCollection(fetch= FetchType.EAGER)
  private List<String> scopes;

  @ManyToOne(optional = false)
  @JsonIgnore
  @JoinColumn(name = "resourceserver_id", nullable = false, updatable = false)
  private ResourceServer resourceServer;

  @ElementCollection(fetch= FetchType.EAGER)
  @MapKeyColumn(name = "attribute_name")
  @Column(name = "attribute_value")
  @CollectionTable(name = "client_attributes", joinColumns = @JoinColumn(name = "client_id"))
  private Map<String, String> attributes = new HashMap<String, String>();

  @Column
  private String thumbNailUrl;

  @ElementCollection(fetch= FetchType.EAGER)
  private List<String> redirectUris = new ArrayList<String>();

  @Column
  private boolean skipConsent;

  @Column
  private boolean includePrincipal;
  /*
   * Seconds for expire of the access token that is granted for users of this
   * client
   */
  @Column
  private long expireDuration;

  @Column
  private boolean useRefreshTokens;

  @Column
  private boolean allowedImplicitGrant;

  @Column
  private boolean allowedClientCredentials;

  // Listed here so Cascade will work.
  @OneToMany(mappedBy ="client", cascade = CascadeType.ALL)
  private List<AccessToken> accessTokens;

  // Listed here so Cascade will work.
  @OneToMany(mappedBy ="client", cascade = CascadeType.ALL)
  private List<AuthorizationRequest> authorizationRequests;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  /**
   * @return the scopes
   */
  public List<String> getScopes() {
    return scopes;
  }

  /**
   * @param scopes
   *          the scopes to set
   */
  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  /**
   * @return the resourceServer
   */
  public ResourceServer getResourceServer() {
    return resourceServer;
  }

  /**
   * @param resourceServer
   *          the resourceServer to set
   */
  public void setResourceServer(ResourceServer resourceServer) {
    this.resourceServer = resourceServer;
  }

  /**
   * @return the thumbNailUrl
   */
  public String getThumbNailUrl() {
    return thumbNailUrl;
  }

  /**
   * @param thumbNailUrl
   *          the thumbNailUrl to set
   */
  public void setThumbNailUrl(String thumbNailUrl) {
    this.thumbNailUrl = thumbNailUrl;
  }

  /**
   * @return the skipConsent
   */
  public boolean isSkipConsent() {
    return skipConsent;
  }

  /**
   * @param skipConsent
   *          the skipConsent to set
   */
  public void setSkipConsent(boolean skipConsent) {
    this.skipConsent = skipConsent;
  }

  /**
   * @return the clientId
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * @param clientId
   *          the clientId to set
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * @return the secret
   */
  public String getSecret() {
    return secret;
  }

  /**
   * @param secret
   *          the secret to set
   */
  public void setSecret(String secret) {
    this.secret = secret;
  }

  /**
   * Get the redirectUris
   * @return List of String
   */
  public List<String> getRedirectUris() {
    return redirectUris;
  }

  /**
   * Set the redirectUris
   * @param redirectUris   the redirectUris to use.
   */
  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  /**
   * @return the useRefreshTokens
   */
  public boolean isUseRefreshTokens() {
    return useRefreshTokens;
  }

  /**
   * @param useRefreshTokens
   *          the useRefreshTokens to set
   */
  public void setUseRefreshTokens(boolean useRefreshTokens) {
    this.useRefreshTokens = useRefreshTokens;
  }

  /**
   * @return the expireDuration
   */
  public long getExpireDuration() {
    return expireDuration;
  }

  /**
   * @param expireDuration
   *          the expireDuration to set
   */
  public void setExpireDuration(long expireDuration) {
    this.expireDuration = expireDuration;
  }

  public boolean isExactMatch(UserPassCredentials credentials) {
    return credentials != null && credentials.isValid() && credentials.getUsername().equals(clientId)
        && credentials.getPassword().equals(secret);

  }

  /**
   * @return the attributes
   */
  public Map<String, String> getAttributes() {
    return attributes;
  }

  /**
   * @param attributes
   *          the attributes to set
   */
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  /**
   * @return the AllowedImplicitGrant
   */
  public boolean isAllowedImplicitGrant() {
    return allowedImplicitGrant;
  }

  /**
   * @param AllowedImplicitGrant
   *          the AllowedImplicitGrant to set
   */
  public void setAllowedImplicitGrant(boolean allowedImplicitGrant) {
    this.allowedImplicitGrant = allowedImplicitGrant;
  }

  public boolean isIncludePrincipal() {
    return includePrincipal;
  }

  public void setIncludePrincipal(boolean includePrincipal) {
    this.includePrincipal = includePrincipal;
  }

  public boolean isAllowedClientCredentials() {
    return allowedClientCredentials;
  }

  public void setAllowedClientCredentials(boolean allowedClientCredentials) {
    this.allowedClientCredentials = allowedClientCredentials;
  }


  /*
   * (non-Javadoc)
   *
   * @see org.surfnet.oaaas.model.AbstractEntity#validate()
   */
  @Override
  public boolean validate(ConstraintValidatorContext context) {
    boolean isValid = true;

    if (isUseRefreshTokens() && getExpireDuration() == 0L) {
      violation(context, "If refresh tokens are to be used then the expiry duration must be greater then 0");
      isValid = false;
    }

    if (isAllowedClientCredentials() && isAllowedImplicitGrant()) {
      violation(context, "A Client can not be issued the client credentials grant AND the implicit grant as client credentials requires a secret.");
      isValid = false;
    }

    if (scopes != null && !resourceServer.getScopes().containsAll(scopes)) {
      String message = "Client should only contain scopes that its resource server defines. " +
          "Client scopes: " + scopes + ". Resource server scopes: " + resourceServer.getScopes();
      violation(context, message);
      isValid = false;
    }

    for (String redirectUri : redirectUris) {
      try {
        new URL(redirectUri);
      } catch (MalformedURLException e) {
        violation(context, "redirectUri '" + redirectUri + "' is not a valid URI");
        isValid = false;
      }
    }
    return isValid;
  }

}
