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

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of the AccessToken request defined in the <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2#page-27">spec</a>
 *
 */
@XmlRootElement
public class AccessTokenRequest {
  @JsonProperty("grant_type")
  private String grantType;

  private String code;

  @JsonProperty("redirect_uri")
  private String redirectUri;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("client_secret")
  private String clientSecret;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("scope")
  private String scope;

  @JsonIgnore
  private Client client;

  public static AccessTokenRequest fromMultiValuedFormParameters(MultivaluedMap<String, String> formParameters) {
    AccessTokenRequest atr = new AccessTokenRequest();
    atr.setClientId(nullSafeGetFormParameter("client_id", formParameters));
    atr.setClientSecret(nullSafeGetFormParameter("client_secret", formParameters));
    atr.setCode(nullSafeGetFormParameter("code", formParameters));
    atr.setGrantType(nullSafeGetFormParameter("grant_type", formParameters));
    atr.setRedirectUri(nullSafeGetFormParameter("redirect_uri", formParameters));
    atr.setRefreshToken(nullSafeGetFormParameter("refresh_token", formParameters));
    atr.setScope(nullSafeGetFormParameter("scope", formParameters));
    return atr;
  }

  private static String nullSafeGetFormParameter(String parameterName, MultivaluedMap<String, String> formParameters) {
    List<String> params = formParameters.get(parameterName);
    return CollectionUtils.isEmpty(params) ? null : params.get(0);
  }

  /**
   * @return the grantType
   */
  public String getGrantType() {
    return grantType;
  }

  /**
   * @param grantType
   *          the grantType to set
   */
  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code
   *          the code to set
   */
  public void setCode(String code) {
    this.code = code;
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
   * @return the clientSecret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * @param clientSecret
   *          the clientSecret to set
   */
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  /**
   * @return the refreshToken
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * @param refreshToken
   *          the refreshToken to set
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
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

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

}
