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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.openjpa.persistence.jdbc.Unique;

/**
 * A representation of an <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2#section-4.1.1"
 * >AuthorizationRequest</a>.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="authorizationrequest")
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
public class AuthorizationRequest extends AbstractEntity {

  @Column
  @NotNull
  private String responseType;

  @Column
  @NotNull
  private String clientId;

  @Column
  @NotNull
  private String redirectUri;

  @Column
  private String scope;

  @Column
  private String state;

  @Column
  @NotNull
  @Unique
  private String csrfValue;
  
  public AuthorizationRequest() {
    super();
  }

  public AuthorizationRequest(String responseType, String clientId, String redirectUri, String scope, String state, String csrfValue) {
    super();
    this.responseType = responseType;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.scope = scope;
    this.state = state;
    this.csrfValue = csrfValue;
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
  public String getScope() {
    return scope;
  }

  /**
   * @param scope
   *          the scope to set
   */
  public void setScope(String scope) {
    this.scope = scope;
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
   * @return the csrfValue
   */
  public String getCsrfValue() {
    return csrfValue;
  }

  /**
   * @param csrfValue the csrfValue to set
   */
  public void setCsrfValue(String csrfValue) {
    this.csrfValue = csrfValue;
  }

}
