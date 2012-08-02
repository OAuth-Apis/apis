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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of the answer to the a Resource Server when asked to verify
 * the token.
 * 
 * We don't use conversion of camelCase to snakeCase as this class is
 * potentially used by clients which have different dependencies where
 * JsonSnakeCase is ignored.
 * 
 */

@SuppressWarnings("serial")
@XmlRootElement
public class VerifyTokenResponse implements Serializable {
  /*
   * The application that is the intended target of the token.
   */
  private String audience;
  /*
   * The space delimited set of scopes that the user consented to.
   */
  private String scopes;
  /*
   * The space delimited set of roles that the user consented to.
   */
  private String roles;
  /*
   * The userId
   */
  private String user_id;
  /*
   * The number of seconds left in the lifetime of the token.
   */
  private long expires_in;

  /*
   * If the token is no good then we return with an error
   */
  private String error;

  public VerifyTokenResponse() {
    super();
  }

  public VerifyTokenResponse(String error) {
    super();
    this.error = error;
  }

  public VerifyTokenResponse(String audience, String scopes, String roles, String userId, long expiresIn) {
    super();
    this.audience = audience;
    this.scopes = scopes;
    this.roles = roles;
    this.user_id = userId;
    this.expires_in = expiresIn;
  }

  /**
   * @return the audience
   */
  public String getAudience() {
    return audience;
  }

  /**
   * @param audience
   *          the audience to set
   */
  public void setAudience(String audience) {
    this.audience = audience;
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
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * @param error
   *          the error to set
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * @return the user_id
   */
  public String getUser_id() {
    return user_id;
  }

  /**
   * @param user_id
   *          the user_id to set
   */
  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  /**
   * @return the expires_in
   */
  public long getExpires_in() {
    return expires_in;
  }

  /**
   * @param expires_in
   *          the expires_in to set
   */
  public void setExpires_in(long expires_in) {
    this.expires_in = expires_in;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "VerifyTokenResponse [audience=" + audience + ", scopes=" + scopes + ", roles=" + roles + ", user_id="
        + user_id + ", expires_in=" + expires_in + "]";
  }

  /**
   * @return the roles
   */
  public String getRoles() {
    return roles;
  }

  /**
   * @param roles
   *          the roles to set
   */
  public void setRoles(String roles) {
    this.roles = roles;
  }

}
