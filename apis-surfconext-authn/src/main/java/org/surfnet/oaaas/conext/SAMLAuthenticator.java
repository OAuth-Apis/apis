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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.spring.security.opensaml.Provisioner;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

import nl.surfnet.spring.security.opensaml.AuthnRequestGenerator;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandler;
import nl.surfnet.spring.security.opensaml.ServiceProviderAuthenticationException;
import nl.surfnet.spring.security.opensaml.util.IDService;
import nl.surfnet.spring.security.opensaml.util.TimeService;
import nl.surfnet.spring.security.opensaml.xml.EndpointGenerator;

@Component
public class SAMLAuthenticator extends AbstractAuthenticator {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticator.class);

  private TimeService timeService = new TimeService();
  private IDService idService = new IDService();
  private OpenSAMLContext openSAMLContext;
  private String ssoUrl;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    super.init(filterConfig);

    try {
      final Properties properties = PropertiesLoaderUtils.loadAllProperties("surfconext.authn.properties");
      openSAMLContext = createOpenSAMLContext(properties);

      ssoUrl = properties.getProperty("ssoUrl", "no-property-named-ssoUrl");
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  /**
   * Default Context factory method.
   */
  protected OpenSAMLContext createOpenSAMLContext(Properties properties) {
    return new OpenSAMLContext(properties, createProvisioner());
  }

  /**
   * Default Provisioner factory method.
   */
  protected Provisioner createProvisioner() {
    return new SAMLProvisioner();
  }

  @Override
  public boolean canCommence(HttpServletRequest request) {
    return isSAMLResponse(request);
  }

  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      String authStateValue, String returnUri) throws IOException, ServletException {
    LOG.debug("Hitting SAML Authenticator filter");
    
    if (isSAMLResponse(request)) {
      final Response samlResponse = extractSamlResponse(request);

      if (samlResponse == null) {
        LOG.info("Invalid response gotten from SAML IdP");
        return;
      } else {
        final UserDetails ud = authenticate(samlResponse);
        if (ud == null) {
          LOG.info("Cannot get UserDetails from SAML response");
        } else {
          super.setPrincipal(request, convertToPrincipal(ud));
          super.setAuthStateValue(request, getSAMLRelayState(request));
          chain.doFilter(request, response);
          return;
        }
      }
    }
    sendAuthnRequest(response, authStateValue, getReturnUri(request));
  }
  

  private AuthenticatedPrincipal convertToPrincipal(UserDetails ud) {
    Collection<? extends GrantedAuthority> authorities = ud.getAuthorities();
    Collection<String> roles = new HashSet<String>();
    if (authorities != null) {
      for (GrantedAuthority authority : authorities) {
        roles.add(authority.getAuthority());
      }
    }
    return new AuthenticatedPrincipal(ud.getUsername(), roles);
  }
  
  protected String getSAMLRelayState(HttpServletRequest request) {
    return request.getParameter("RelayState");
  }
  protected boolean isSAMLResponse(HttpServletRequest request) {
    return request.getParameter("SAMLResponse") != null;
  }

  private UserDetails authenticate(Response samlResponse) {

    // throws AuthenticationException when not authenticated successfully.
    try {
      return openSAMLContext.assertionConsumer().consume(samlResponse);
    } catch (AuthenticationException e) {
      LOG.info("When authenticating SAML response", e);
      return null;
    }
  }

  private Response extractSamlResponse(HttpServletRequest request) {

    SAMLMessageContext messageContext;

    final SAMLMessageHandler samlMessageHandler = openSAMLContext.samlMessageHandler();
    try {
      messageContext = samlMessageHandler.extractSAMLMessageContext(request);
    } catch (MessageDecodingException me) {
      throw new ServiceProviderAuthenticationException("Could not decode SAML Response", me);
    } catch (org.opensaml.xml.security.SecurityException se) {
      throw new ServiceProviderAuthenticationException("Could not decode SAML Response", se);
    }

    LOG.debug("Message received from issuer: " + messageContext.getInboundMessageIssuer());

    if (!(messageContext.getInboundSAMLMessage() instanceof Response)) {
      throw new ServiceProviderAuthenticationException("SAML Message was not a Response.");
    }

    final Response inboundSAMLMessage = (Response) messageContext.getInboundSAMLMessage();

    try {
      openSAMLContext.validatorSuite().validate(inboundSAMLMessage);
      return inboundSAMLMessage;
    } catch (ValidationException ve) {
      LOG.warn("Response Message failed Validation", ve);
      throw new RuntimeException("Invalid SAML Response Message", ve);
    }
  }

  private void sendAuthnRequest(HttpServletResponse response, String authState, String returnUri) throws IOException {
    AuthnRequestGenerator authnRequestGenerator = new AuthnRequestGenerator(openSAMLContext.entityId(), timeService,
        idService);
    EndpointGenerator endpointGenerator = new EndpointGenerator();

    final String target = ssoUrl;

    Endpoint endpoint = endpointGenerator.generateEndpoint(
        SingleSignOnService.DEFAULT_ELEMENT_NAME, target, openSAMLContext.assertionConsumerUri());

    AuthnRequest authnRequest = authnRequestGenerator.generateAuthnRequest(target, openSAMLContext.assertionConsumerUri());

    LOG.debug("Sending authnRequest to {}", target);

    String relayState = authState;
    try {
      openSAMLContext.samlMessageHandler().sendSAMLMessage(authnRequest, endpoint, response, relayState);
    } catch (MessageEncodingException mee) {
      LOG.error("Could not send authnRequest to Identity Provider.", mee);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }


}
