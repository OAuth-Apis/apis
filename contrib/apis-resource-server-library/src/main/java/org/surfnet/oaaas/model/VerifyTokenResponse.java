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
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * Representation of the answer to a Resource Server when asked to verify
 * a token.
 *
 */

@SuppressWarnings("serial")
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VerifyTokenResponse implements Serializable {
  /*
   * The application that is the intended target of the token.
   */
  private String audience;
  /*
   * The space delimited set of scopes that the user consented to.
   */
  private List<String> scopes;
  /*
   * The principal
   */
  private AuthenticatedPrincipal principal;
  /*
   * The number of seconds left in the lifetime of the token.
   */
  @JsonProperty("expires_in")
  private Long expiresIn;

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

  public VerifyTokenResponse(String audience, List<String> scopes, AuthenticatedPrincipal principal, Long expiresIn) {
    super();
    this.audience = audience;
    this.scopes = scopes;
    this.principal = principal;
    this.expiresIn = expiresIn;
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
   * @return the expiresIn
   */
  public Long getExpiresIn() {
    return expiresIn;
  }

  /**
   * @param expiresIn the expiresIn to set
   */
  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }

  /**
   * @return the principal
   */
  public AuthenticatedPrincipal getPrincipal() {
    return principal;
  }

  /**
   * @param principal the principal to set
   */
  public void setPrincipal(AuthenticatedPrincipal principal) {
    this.principal = principal;
  }

}
