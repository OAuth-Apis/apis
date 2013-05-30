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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.surfnet.spring.security.opensaml.Provisioner;

import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of Spring-security-opensaml's Provisioner interface, which provisions a UserDetails object based on a SAML Assertion.
 */
public class SAMLProvisioner implements Provisioner {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLProvisioner.class);

  private String uuidAttribute = "urn:oid:1.3.6.1.4.1.1076.20.40.40.1";
  private static final String DISPLAY_NAME_ATTRIBUTE = "urn:mace:dir:attribute-def:displayName";

  @Override
  public UserDetails provisionUser(Assertion assertion) {
    String userId = getValueFromAttributeStatements(assertion, uuidAttribute);
    String identityProvider = getAuthenticatingAuthority(assertion);
    if (identityProvider == null) {
      LOG.debug("No AuthenticatingAuthority present in the Assertion, cannot determine IdP. Will leave null in principal.");
    }
    String displayName =  getValueFromAttributeStatements(assertion, DISPLAY_NAME_ATTRIBUTE);
    return new SAMLAuthenticatedPrincipal(userId, new ArrayList<String>(), new HashMap<String, String>(), new ArrayList<String>(), identityProvider, displayName, false);
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

  private String getAuthenticatingAuthority(final Assertion assertion) {
    final List<AuthnStatement> authnStatements = assertion.getAuthnStatements();
    for (AuthnStatement as : authnStatements) {
      final List<AuthenticatingAuthority> authorities = as.getAuthnContext().getAuthenticatingAuthorities();
      for (AuthenticatingAuthority aa : authorities) {
        if (StringUtils.isNotBlank(aa.getURI())) {
          return aa.getURI();
        }
      }
    }
    return null;
  }


  public void setUuidAttribute(String uuidAttribute) {
    this.uuidAttribute = uuidAttribute;
  }
}
