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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents a Client as defined by the OAuth 2 specification:
 * <pre>
 *         An application making protected resource requests on behalf of the resource owner and with its
 *         authorization.  The term client does not imply any particular implementation characteristics (e.g. whether
 *         the application executes on a server, a desktop, or other devices).
 * </pre>
 */

@SuppressWarnings("serial")
@Entity
@Table(name="client")
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
public class Client extends AbstractEntity {

  @Column(unique = true)
  @NotNull
  private String name;

  @Column
  private String description;

  @Column
  private String contactName;

  @Column
  private String contactEmail;

  @Column
  private String scopes;
  
  @ManyToOne(optional=false) 
  @JoinColumn(name="resourceserver_id", nullable=false, updatable=false)
  private ResourceServer resourceServer;

  @Column
  private String thumbNailUrl;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  /**
   * @return the resourceServer
   */
  public ResourceServer getResourceServer() {
    return resourceServer;
  }

  /**
   * @param resourceServer the resourceServer to set
   */
  public void setResourceServer(ResourceServer resourceServer) {
    this.resourceServer = resourceServer;
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

}
