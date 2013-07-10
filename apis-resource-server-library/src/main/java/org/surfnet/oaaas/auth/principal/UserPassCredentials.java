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
 * Holder and parser for the username and password from the authentication header.
 */
public class UserPassCredentials {

  private static final char SEMI_COLON = ':';
  private static final int BASIC_AUTH_PREFIX_LENGTH = "Basic ".length();

  private String username;
  private String password;

  /**
   * Parse the username and password from the authorization header. If
   * the username and password cannot be found they are set to null.
   * @param authorizationHeader the authorization header
   */
  public UserPassCredentials(final String authorizationHeader) {
    if (authorizationHeader == null || authorizationHeader.length() < BASIC_AUTH_PREFIX_LENGTH) {
      noValidAuthHeader();
      return;
    }

    String authPart = authorizationHeader.substring(BASIC_AUTH_PREFIX_LENGTH);
    String userpass = new String(Base64.decodeBase64(authPart.getBytes()));
    int index = userpass.indexOf(SEMI_COLON);
    if (index < 1) {
      noValidAuthHeader();
      return;
    }
    username = userpass.substring(0, index);
    password = userpass.substring(index + 1);
  }

  public UserPassCredentials(String username, String password) {
    super();
    this.username = username;
    this.password = password;
  }

  private void noValidAuthHeader() {
    username = null;
    password = null;
  }

  public boolean isValid() {
    return !StringUtils.isBlank(username) && !StringUtils.isBlank(password);
  }
  
  /**
   * Get the username.
   * @return the username or null if the username was not found
   */
  public String getUsername() {
    return username;
  }

  /**
   * Get the password.
   * @return the password or null if the password was not found
   */
  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "UserPassCredentials [username=" + username + "]";
  }

  public String getAuthorizationHeaderValue() {
    String result = null;
    if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
      String value = username + ":" + password;
      result = "Basic " + new String(Base64.encodeBase64(value.getBytes())) ;
    }
    return result;
  }

}