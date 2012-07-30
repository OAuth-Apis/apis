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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Representation of an <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-30#section-1.4"
 * >AccessToken</a>
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="accesstoken")
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
public class AccessToken extends AbstractEntity {

  @Column(unique = true)
  @NotNull
  private String token;

  @Column
  @NotNull
  private String principal;
  
  @ManyToOne(optional=false) 
  @JoinColumn(name="client_id", nullable=false, updatable=false)
  private Client client;

  @Column
  private long expires;

  @Column
  private String scopes;

  public AccessToken() {
    super();
  }

  public AccessToken(String token, String principal, Client client, long expires, String scopes) {
    super();
    this.token = token;
    this.principal = principal;
    this.client = client;
    this.expires = expires;
    this.scopes = scopes;
  }



  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the principal
   */
  public String getPrincipal() {
    return principal;
  }

  /**
   * @param principal the principal to set
   */
  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }

  /**
   * @param client the client to set
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
   * @param expires the expires to set
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
   * @param scopes the scopes to set
   */
  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  
  
}
