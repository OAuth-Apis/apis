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
package org.surfnet.oaaas.auth.api.principal;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.surfnet.oaaas.auth.api.AbstractAuthenticator;

/**
 * {@link Principal} that can contain roles and additional attributes. This is
 * the return Object for {@link AbstractAuthenticator} implementations. Note
 * that the key and values of the {@link AuthenticatedPrincipal#attributes} must
 * be {@link Serializable}.
 * 
 */
public class AuthenticatedPrincipal implements Serializable, Principal {

  private static final long serialVersionUID = 1L;

  private String name;

  private Collection<String> roles;

  /*
   * Extra attributes, depending on the authentication implementation
   */
  private Map<String, Object> attributes;

  public AuthenticatedPrincipal() {
    super();
  }

  public AuthenticatedPrincipal(String username) {
    this(username, Collections.<String> emptyList());
  }

  public AuthenticatedPrincipal(String username, Collection<String> roles) {
    this(username, roles, Collections.<String, Object> emptyMap());
  }

  public AuthenticatedPrincipal(String username, Collection<String> roles, Map<String, Object> attributes) {
    this.name = username;
    this.roles = roles;
    this.attributes = attributes;
  }

  /**
   * @return the roles
   */
  public Collection<String> getRoles() {
    return roles;
  }

  /**
   * @return the attributes
   */
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.security.Principal#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AuthenticatedPrincipalImpl [name=" + name + ", roles=" + roles + ", attributes=" + attributes + "]";
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param roles
   *          the roles to set
   */
  public void setRoles(Collection<String> roles) {
    this.roles = roles;
  }

  /**
   * @param attributes
   *          the attributes to set
   */
  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

}
