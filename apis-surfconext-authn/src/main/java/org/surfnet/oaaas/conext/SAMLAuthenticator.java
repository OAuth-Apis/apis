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

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.spring.security.opensaml.AuthnRequestGenerator;
import nl.surfnet.spring.security.opensaml.Provisioner;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandler;
import nl.surfnet.spring.security.opensaml.ServiceProviderAuthenticationException;
import nl.surfnet.spring.security.opensaml.util.IDService;
import nl.surfnet.spring.security.opensaml.util.TimeService;
import nl.surfnet.spring.security.opensaml.xml.EndpointGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.RequesterID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Scoping;
import org.opensaml.saml2.core.impl.RequesterIDBuilder;
import org.opensaml.saml2.core.impl.ScopingBuilder;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

@Component
public class SAMLAuthenticator extends AbstractAuthenticator {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticator.class);
  private static final String RELAY_STATE_FROM_SAML = "RELAY_STATE_FROM_SAML";
  private static final String PRINCIPAL_FROM_SAML = "PRINCIPAL_FROM_SAML";
  private static final String CLIENT_SAML_ENTITY_NAME = "CLIENT_SAML_ENTITY_NAME";

  private TimeService timeService = new TimeService();
  private IDService idService = new IDService();
  private ScopingBuilder scopingBuilder = new ScopingBuilder();
  private RequesterIDBuilder requesterIDBuilder = new RequesterIDBuilder();

  private OpenSAMLContext openSAMLContext;
  private OpenConextOAuthClient apiClient;
  private String callbackFlagParameter = "apiOauthCallback";
  private boolean enrichPricipal;
  private String adminGroup;


  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  private final Properties properties;

  {
    try {
      properties = PropertiesLoaderUtils.loadAllProperties("surfconext.authn.properties");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    try {
      super.init(filterConfig);
      openSAMLContext = createOpenSAMLContext(properties);
      enrichPricipal = Boolean.valueOf(properties.getProperty("api-enrich-principal"));
      if (enrichPricipal) {
        apiClient = createOpenConextOAuthClient(properties);
        adminGroup = properties.getProperty("admin.client.apis.teamname");
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }


  protected OpenConextOAuthClient createOpenConextOAuthClient(Properties properties) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
    OpenConextOAuthClient apiClient = (OpenConextOAuthClient) getClass().getClassLoader().loadClass(properties.getProperty("openConextApiClient")).newInstance();
    BeanUtils.setProperty(apiClient, "callbackUrl", properties.getProperty("api-callbackuri"));
    BeanUtils.setProperty(apiClient, "consumerSecret", properties.getProperty("api-consumersecret"));
    BeanUtils.setProperty(apiClient, "consumerKey", properties.getProperty("api-consumerkey"));
    BeanUtils.setProperty(apiClient, "endpointBaseUrl", properties.getProperty("api-baseurl"));
    return apiClient;
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
    SAMLProvisioner samlProvisioner = new SAMLProvisioner();
    samlProvisioner.setUuidAttribute((String) properties.get("samlUuidAttribute"));
    return samlProvisioner;
  }

  @Override
  public boolean canCommence(HttpServletRequest request) {
    return isSAMLResponse(request) || isOAuthCallback(request);
  }

  @Override
  public void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                           String authStateValue, String returnUri) throws IOException, ServletException {
    LOG.debug("Hitting SAML Authenticator filter");
    if (isSAMLResponse(request)) {
      Response samlResponse = extractSamlResponse(request);
      SAMLAuthenticatedPrincipal principal = (SAMLAuthenticatedPrincipal) openSAMLContext.assertionConsumer().consume(samlResponse);
      if (enrichPricipal) {
        //need to save the Principal and the AuthState somewhere
        request.getSession().setAttribute(PRINCIPAL_FROM_SAML, principal);
        request.getSession().setAttribute(RELAY_STATE_FROM_SAML, getSAMLRelayState(request));
        response.sendRedirect(apiClient.getAuthorizationUrl());
      } else {
        proceedWithChain(request, response, chain, principal, getSAMLRelayState(request));
      }
    } else if (isOAuthCallback(request)) {
      SAMLAuthenticatedPrincipal principal = (SAMLAuthenticatedPrincipal) request.getSession().getAttribute(PRINCIPAL_FROM_SAML);
      String authState = (String) request.getSession().getAttribute(RELAY_STATE_FROM_SAML);
      if (principal == null) { //huh
        throw new ServiceProviderAuthenticationException("No principal anymore in the session");
      }
      String userId = principal.getName();
      if (StringUtils.isEmpty(userId)) {
        throw new ServiceProviderAuthenticationException("No userId in SAML assertion!");
      }
      apiClient.oauthCallback(request, userId);
      List<Group20> groups = apiClient.getGroups20(userId, userId);
      if (!CollectionUtils.isEmpty(groups)) {
        for (Group20 group : groups) {
          principal.addGroup(group.getId());
          if (StringUtils.isNotBlank(this.adminGroup) && adminGroup.equalsIgnoreCase(group.getId())) {
            principal.setAdminPrincipal(true);
          }
        }
      }
      proceedWithChain(request, response, chain, principal, authState);
    } else {
      sendAuthnRequest(response, authStateValue, getReturnUri(request));
    }
  }

  private void proceedWithChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain, AuthenticatedPrincipal principal, String authStateValue) throws IOException, ServletException {
    super.setPrincipal(request, principal);
    super.setAuthStateValue(request, authStateValue);
    chain.doFilter(request, response);
  }

  private boolean isOAuthCallback(HttpServletRequest request) {
    return request.getParameter(callbackFlagParameter) != null;
  }

  protected String getSAMLRelayState(HttpServletRequest request) {
    return request.getParameter("RelayState");
  }

  protected boolean isSAMLResponse(HttpServletRequest request) {
    return request.getParameter("SAMLResponse") != null;
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

    final String target = openSAMLContext.getIdpUrl();

    Endpoint endpoint = endpointGenerator.generateEndpoint(
            SingleSignOnService.DEFAULT_ELEMENT_NAME, target, openSAMLContext.assertionConsumerUri());

    AuthnRequest authnRequest = authnRequestGenerator.generateAuthnRequest(target, openSAMLContext.assertionConsumerUri());

    Client client = getClientByRequest(authState);
    String spEntityIdBy = client.getAttributes().get(CLIENT_SAML_ENTITY_NAME);

    if (StringUtils.isNotEmpty(spEntityIdBy)) {
      Scoping scoping = scopingBuilder.buildObject();
      scoping.getRequesterIDs().add(createRequesterID(spEntityIdBy));
      authnRequest.setScoping(scoping);
    } else {
      LOG.warn("For Client {} there is no key CLIENT_SAML_ENTITY_NAME configured to identify the SP entity name. NO SCOPING IS APPLIED", client.getClientId());
    }

    CriteriaSet criteriaSet = new CriteriaSet();
    criteriaSet.add(new EntityIDCriteria(openSAMLContext.entityId()));
    criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
    try {

      Credential signingCredential = openSAMLContext.keyStoreCredentialResolver().resolveSingle(criteriaSet);
      String relayState = authState;
      LOG.debug("Sending authnRequest to {}", target);
      openSAMLContext.samlMessageHandler().sendSAMLMessage(authnRequest, endpoint, response, relayState, signingCredential);
    } catch (MessageEncodingException mee) {
      LOG.error("Could not send authnRequest to Identity Provider.", mee);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (org.opensaml.xml.security.SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  private RequesterID createRequesterID(String id) {
    RequesterID requesterID = requesterIDBuilder.buildObject();
    requesterID.setRequesterID(id);
    return requesterID;
  }

  /**
   * Get the Client
   */
  protected Client getClientByRequest(String authState) {
    AuthorizationRequest authorizationRequest = authorizationRequestRepository.findByAuthState(authState);
    return authorizationRequest.getClient();

  }


}
