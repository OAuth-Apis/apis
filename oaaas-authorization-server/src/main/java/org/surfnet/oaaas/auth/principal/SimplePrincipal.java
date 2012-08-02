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

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SimplePrincipal implements RolesPrincipal, Serializable {

  private static final long serialVersionUID = 1L;

  private String username;

  private Collection<String> roles;

  public SimplePrincipal(String username) {
    this(username,null);
  }

  public SimplePrincipal(String username, Collection<String> roles) {
    this.username = username;
    this.roles = roles;
  }

  @Override
  public String getName() {
    return username;
  }

  public String toString() {
    return "SimplePrincipal['" + username + "']";
  }

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.principal.RolesPrincipal#getRoles()
   */
  @Override
  public Collection<String> getRoles() {
    return roles ;
  }
}
