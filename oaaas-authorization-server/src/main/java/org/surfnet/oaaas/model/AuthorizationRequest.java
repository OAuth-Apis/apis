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
import org.apache.openjpa.persistence.jdbc.Unique;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * A representation of an <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2#section-4.1.1"
 * >AuthorizationRequest</a>.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "authorizationrequest")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AuthorizationRequest extends AbstractEntity {

  @Column
  @NotNull
  private String responseType;

  @Transient
  private String clientId;

  @Transient
  private AuthenticatedPrincipal principal;

  @Column(length = 512)
  private String encodedPrincipal;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id", nullable = false, updatable = false)
  private Client client;

  @Column
  @NotNull
  private String redirectUri;

  @Column
  private String scopes;

  @Column
  private String state;

  @Column
  @NotNull
  @Unique
  private String authState;

  @Column
  @Unique
  private String authorizationCode;

  public AuthorizationRequest() {
    super();
  }

  public AuthorizationRequest(String responseType, String clientId, String redirectUri, String scopes, String state,
      String authState) {
    super();
    this.responseType = responseType;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.scopes = scopes;
    this.state = state;
    this.authState = authState;
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
   * @return the responseType
   */
  public String getResponseType() {
    return responseType;
  }

  /**
   * @param responseType
   *          the responseType to set
   */
  public void setResponseType(String responseType) {
    this.responseType = responseType;
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
   * @return the redirectUri
   */
  public String getRedirectUri() {
    return redirectUri;
  }

  /**
   * @param redirectUri
   *          the redirectUri to set
   */
  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  /**
   * @return the scope
   */
  public String getScopes() {
    return scopes;
  }

  /**
   * @param scope
   *          the scope to set
   */
  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  /**
   * @return the state
   */
  public String getState() {
    return state;
  }

  /**
   * @param state
   *          the state to set
   */
  public void setState(String state) {
    this.state = state;
  }

  /**
   * @return the authState
   */
  public String getAuthState() {
    return authState;
  }

  /**
   * @param authState
   *          the authState to set
   */
  public void setAuthState(String authState) {
    this.authState = authState;
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
   * @return the authorizationCode
   */
  public String getAuthorizationCode() {
    return authorizationCode;
  }

  /**
   * @param authorizationCode
   *          the authorizationCode to set
   */
  public void setAuthorizationCode(String authorizationCode) {
    this.authorizationCode = authorizationCode;
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
  public void setPrincipal(AuthenticatedPrincipal principal) {
    this.principal = principal;
    this.encodePrincipal();
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

}
