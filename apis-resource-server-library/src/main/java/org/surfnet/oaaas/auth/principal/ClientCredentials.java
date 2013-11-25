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
package org.surfnet.oaaas.auth.principal;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * Holder and parser for the clientId and secret from the authentication header.
 */
public class ClientCredentials {

  private static final char SEMI_COLON = ':';
  private static final int BASIC_AUTH_PREFIX_LENGTH = "Basic ".length();

  private String clientId;
  private String secret;

  /**
   * Parse the clientId and secret from the authorization header. If
   * the clientId and secret cannot be found they are set to null.
   * @param authorizationHeader the authorization header
   */
  public ClientCredentials(final String authorizationHeader) {
    if (authorizationHeader == null || authorizationHeader.length() < BASIC_AUTH_PREFIX_LENGTH) {
      noValidAuthHeader();
      return;
    }

    String authPart = authorizationHeader.substring(BASIC_AUTH_PREFIX_LENGTH);
    String clientSecret = new String(Base64.decodeBase64(authPart.getBytes()));
    int index = clientSecret.indexOf(SEMI_COLON);
    if (index < 1) {
      noValidAuthHeader();
      return;
    }
    clientId = clientSecret.substring(0, index);
    secret = clientSecret.substring(index + 1);
  }

  public ClientCredentials(String clientId, String secret) {
    super();
    this.clientId = clientId;
    this.secret = secret;
  }

  private void noValidAuthHeader() {
    clientId = null;
    secret = null;
  }

  public boolean isValid() {
    return !StringUtils.isBlank(clientId) && !StringUtils.isBlank(secret);
  }
  
  /**
   * Get the clientId.
   * @return the clientId or null if the clientId was not found
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Get the secret.
   * @return the secret or null if the secret was not found
   */
  public String getSecret() {
    return secret;
  }

  @Override
  public String toString() {
    return "ClientCredentials [clientId=" + clientId + "]";
  }

  public String getAuthorizationHeaderValue() {
    String result = null;
    if (!StringUtils.isBlank(clientId) && !StringUtils.isBlank(secret)) {
      String value = clientId + ":" + secret;
      result = "Basic " + new String(Base64.encodeBase64(value.getBytes())) ;
    }
    return result;
  }

}