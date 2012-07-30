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

package org.surfnet.oaaas.conext;

import java.util.Arrays;

import javax.inject.Named;

import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import nl.surfnet.spring.security.opensaml.Provisioner;

@Named
public class SAMLProvisioner implements Provisioner {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLProvisioner.class);

  @Override
  public UserDetails provisionUser(Assertion assertion) {
    LOG.debug("Assertion: {}", assertion);
    return new User(assertion.getID(), "", Arrays.asList(new SimpleAuthority("USER")));
  }

  public static class SimpleAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    String name;

    public SimpleAuthority(String name) {
      this.name = name;
    }

    @Override
    public String getAuthority() {
      return name;
    }
  }
}
