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
package org.surfnet.oaaas.example.api;

/**
 * Configuration for Authorization Server
 * 
 */
public class AuthConfiguration {
  private String authorizationServerUrl;
  private String secret;
  private String key;

  /**
   * @return the authorizationServerUrl
   */
  public String getAuthorizationServerUrl() {
    return authorizationServerUrl;
  }

  /**
   * @param authorizationServerUrl
   *          the authorizationServerUrl to set
   */
  public void setAuthorizationServerUrl(String authorizationServerUrl) {
    this.authorizationServerUrl = authorizationServerUrl;
  }

 

  /**
   * @return the secret
   */
  public String getSecret() {
    return secret;
  }

  /**
   * @param secret the secret to set
   */
  public void setSecret(String secret) {
    this.secret = secret;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

}
