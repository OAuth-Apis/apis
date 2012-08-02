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

import java.util.Collections;
import java.util.List;

import nl.surfnet.spring.security.opensaml.Provisioner;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class SAMLProvisioner implements Provisioner {

  private static final String UID = "urn:oid:1.3.6.1.4.1.1076.20.40.40.1";

  @Override
  public UserDetails provisionUser(Assertion assertion) {
    String userId = getValueFromAttributeStatements(assertion, UID);
    return new User(userId, "", Collections.singletonList(new SimpleAuthority("USER")));
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


  private String getValueFromAttributeStatements(final Assertion assertion, final String name) {
    final List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
    for (AttributeStatement attributeStatement : attributeStatements) {
      final List<Attribute> attributes = attributeStatement.getAttributes();
      for (Attribute attribute : attributes) {
        if (name.equals(attribute.getName())) {
          return attribute.getAttributeValues().get(0).getDOM().getTextContent();
        }
      }
    }
    return "";
  }
}
