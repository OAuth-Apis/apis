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
package org.surfnet.oaaas.web;

/**
 * Form object for the test page
 * 
 */
public class ClientSettings {

  private String accessTokenEndPoint ;
  private String oauthKey;
  private String oauthSecret;
  private String authorizationURL;
  private String authorizationURLComplete;
  private String step ;
  private String requestURL;
  private boolean implicitGrant;
  private String accessToken;

  public ClientSettings() {
    super();
  }

  public ClientSettings(String accessTokenEndPoint, String oauthKey, String oauthSecret, String authorizationURL,
      String step, String requestURL) {
    super();
    this.accessTokenEndPoint = accessTokenEndPoint;
    this.oauthKey = oauthKey;
    this.oauthSecret = oauthSecret;
    this.authorizationURL = authorizationURL;
    this.step = step;
    this.requestURL = requestURL;
  }


  /**
   * @return the accessTokenEndPoint
   */
  public String getAccessTokenEndPoint() {
    return accessTokenEndPoint;
  }

  /**
   * @param accessTokenEndPoint
   *          the accessTokenEndPoint to set
   */
  public void setAccessTokenEndPoint(String accessTokenEndPoint) {
    this.accessTokenEndPoint = accessTokenEndPoint;
  }

  /**
   * @return the oauthKey
   */
  public String getOauthKey() {
    return oauthKey;
  }

  /**
   * @param oauthKey
   *          the oauthKey to set
   */
  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  /**
   * @return the oauthSecret
   */
  public String getOauthSecret() {
    return oauthSecret;
  }

  /**
   * @param oauthSecret
   *          the oauthSecret to set
   */
  public void setOauthSecret(String oauthSecret) {
    this.oauthSecret = oauthSecret;
  }

  /**
   * @return the authorizationURL
   */
  public String getAuthorizationURL() {
    return authorizationURL;
  }

  /**
   * @param authorizationURL
   *          the authorizationURL to set
   */
  public void setAuthorizationURL(String authorizationURL) {
    this.authorizationURL = authorizationURL;
  }

  /**
   * @return the step
   */
  public String getStep() {
    return step;
  }

  /**
   * @param step
   *          the step to set
   */
  public void setStep(String step) {
    this.step = step;
  }

  /**
   * @return the requestURL
   */
  public String getRequestURL() {
    return requestURL;
  }

  /**
   * @param requestURL
   *          the requestURL to set
   */
  public void setRequestURL(String requestURL) {
    this.requestURL = requestURL;
  }

  /**
   * @return the implicitGrant
   */
  public boolean isImplicitGrant() {
    return implicitGrant;
  }

  /**
   * @param implicitGrant the implicitGrant to set
   */
  public void setImplicitGrant(boolean implicitGrant) {
    this.implicitGrant = implicitGrant;
  }

  /**
   * @return the authorizationURLComplete
   */
  public String getAuthorizationURLComplete() {
    return authorizationURLComplete;
  }

  /**
   * @param authorizationURLComplete the authorizationURLComplete to set
   */
  public void setAuthorizationURLComplete(String authorizationURLComplete) {
    this.authorizationURLComplete = authorizationURLComplete;
  }

  /**
   * @return the accessToken
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * @param accessToken the accessToken to set
   */
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

}
