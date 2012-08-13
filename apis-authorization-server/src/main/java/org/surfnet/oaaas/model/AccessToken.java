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
package org.surfnet.oaaas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * Representation of an <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-30#section-1.4"
 * >AccessToken</a>
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "accesstoken")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AccessToken extends AbstractEntity {

  @Column(unique = true)
  @NotNull
  private String token;

  @Column(unique = true, nullable = true)
  private String refreshToken;
  
  @Transient
  private AuthenticatedPrincipal principal;

  @Column(length = 512)
  @NotNull
  private String encodedPrincipal;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id", nullable = false, updatable = false)
  private Client client;

  @Column
  private long expires;

  @Column
  private String scopes;

  public AccessToken() {
    super();
  }

  public AccessToken(String token, AuthenticatedPrincipal principal, Client client, long expires, String scopes) {
    this(token, principal, client, expires, scopes, null );
  }

  public AccessToken(String token, AuthenticatedPrincipal principal, Client client, long expires, String scopes, String refreshToken) {
    super();
    this.token = token;
    this.principal = principal;
    this.encodePrincipal();
    this.client = client;
    this.expires = expires;
    this.scopes = scopes;
    this.refreshToken = refreshToken;
    invariant();
  }

  private void invariant() {
    Assert.notNull(token, "Token may not be null");
    Assert.notNull(client, "Client may not be null");
    Assert.notNull(principal, "AuthenticatedPrincipal may not be null");
  }

  @PreUpdate
  @PrePersist
  public void encodePrincipal() {
    if (principal != null) {
      byte[] binaryData = SerializationUtils.serialize(principal);
      this.encodedPrincipal = new String(Base64.encodeBase64(binaryData));
    }
  }

  @PostLoad
  @PostPersist
  @PostUpdate
  public void decodePrincipal() {
    if (StringUtils.isNotBlank(encodedPrincipal)) {
      byte[] objectData = Base64.decodeBase64(encodedPrincipal);
      this.principal = (AuthenticatedPrincipal) SerializationUtils.deserialize(objectData);
    }
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token
   *          the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }

  /**
   * @param client
   *          the client to set
   */
  public void setClient(Client client) {
    this.client = client;
  }

  /**
   * @return the expires
   */
  public long getExpires() {
    return expires;
  }

  /**
   * @param expires
   *          the expires to set
   */
  public void setExpires(long expires) {
    this.expires = expires;
  }

  /**
   * @return the scopes
   */
  public String getScopes() {
    return scopes;
  }

  /**
   * @param scopes
   *          the scopes to set
   */
  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  /**
   * @return the principal
   */
  public AuthenticatedPrincipal getPrincipal() {
    return principal;
  }

  /**
   * @param principal
   *          the principal to set
   */
  private void setPrincipal(AuthenticatedPrincipal principal) {
    this.principal = principal;
  }

  /**
   * @return the encodedPrincipal
   */
  public String getEncodedPrincipal() {
    return encodedPrincipal;
  }

  /**
   * @param encodedPrincipal
   *          the encodedPrincipal to set
   */
  private void setEncodedPrincipal(String encodedPrincipal) {
    this.encodedPrincipal = encodedPrincipal;
  }

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.model.AbstractEntity#validate()
   */
  @Override
  public void validate() {
    //all is covered by not nulls
  }

  /**
   * @return the refreshToken
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * @param refreshToken the refreshToken to set
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

}
