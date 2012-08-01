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

package org.surfnet.oaaas.model;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Representation of the server hosting the protected resources, capable of
 * accepting and responding to protected resource requests using access tokens.
 */
@SuppressWarnings("serial")
@Entity
@Table(name="resourceserver")
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
public class ResourceServer extends AbstractEntity {

  @Column(unique = true)
  @NotNull
  private String name;

  @Column(unique = true)
  @NotNull
  private String key;

  @Column
  private String scopes;

  @Column
  @NotNull
  private String secret;

  @Column
  @NotNull
  private String contactName;
  
  @Column
  @JsonIgnore
  private String owner;

  @Column
  private String contactEmail;

  @JsonIgnore
  @OneToMany(mappedBy = "resourceServer")
  private List<Client> clients;

  @Column
  private String thumbNailUrl;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  /**
   * @return the thumbNailUrl
   */
  public String getThumbNailUrl() {
    return thumbNailUrl;
  }

  /**
   * @param thumbNailUrl the thumbNailUrl to set
   */
  public void setThumbNailUrl(String thumbNailUrl) {
    this.thumbNailUrl = thumbNailUrl;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
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

  /**
   * @return the clients
   */
  public List<Client> getClients() {
    return clients;
  }

  /**
   * @param clients the clients to set
   */
  public void setClients(List<Client> clients) {
    this.clients = clients;
  }
}
