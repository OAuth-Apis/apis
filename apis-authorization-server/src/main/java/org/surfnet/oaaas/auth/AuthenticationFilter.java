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

package org.surfnet.oaaas.auth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

@Named
public class AuthenticationFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

  private AbstractAuthenticator authenticator;

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Inject
  private OAuth2Validator oAuth2Validator;

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    /*
     * Create an authorizationRequest from the request parameters.
     * This can be either a valid or an invalid request, which will be determined by the oAuth2Validator.
     */
    AuthorizationRequest authorizationRequest = extractAuthorizationRequest(request);
    final ValidationResponse validationResponse = oAuth2Validator.validate(authorizationRequest);

    if (authenticator.canCommence(request)) {
      /*
      * Ok, the authenticator wants to have control again (because he stepped
      * out)
      */
      authenticator.doFilter(request, response, chain);
    } else if (validationResponse.valid()) {
      // Request contains correct parameters to be a real OAuth2 request.
      handleInitialRequest(authorizationRequest, request);
      authenticator.doFilter(request, response, chain);
    } else {
      // not an initial request but authentication module cannot handle it either
      sendError(response, authorizationRequest, validationResponse);
    }
  }

  protected AuthorizationRequest extractAuthorizationRequest(HttpServletRequest request) {
    String responseType = request.getParameter("response_type");
    String clientId = request.getParameter("client_id");
    String redirectUri = request.getParameter("redirect_uri");

    List<String> requestedScopes = null;
    if (StringUtils.isNotBlank(request.getParameter("scope"))) {
      requestedScopes = Arrays.asList(request.getParameter("scope").split(","));
    }

    String state = request.getParameter("state");
    String authState = getAuthStateValue();

    return new AuthorizationRequest(responseType, clientId, redirectUri, requestedScopes, state, authState);
  }

  private boolean handleInitialRequest(AuthorizationRequest authReq, HttpServletRequest request) throws
      ServletException {

      try {
        authorizationRequestRepository.save(authReq);
      } catch (Exception e) {
        LOG.error("while saving authorization request", e);
        throw new ServletException("Cannot save authorization request");
      }

      request.setAttribute(AbstractAuthenticator.AUTH_STATE, authReq.getAuthState());
      request.setAttribute(AbstractAuthenticator.RETURN_URI, request.getRequestURI());
      return true;
  }

  protected String getAuthStateValue() {
    return UUID.randomUUID().toString();
  }

  private void sendError(HttpServletResponse response, AuthorizationRequest authReq, ValidationResponse validate)
      throws IOException {
    LOG.info("Will send error response for authorization request '{}', validation result: {}", authReq, validate);
    String redirectUri = authReq.getRedirectUri();
    String state = authReq.getState();
    if (isValidUrl(redirectUri)) {
      redirectUri = redirectUri.concat(redirectUri.contains("?") ? "&" : "?");
      redirectUri = redirectUri.concat("error=").concat(validate.getValue()).concat("&error_description=")
          .concat(validate.getDescription()).concat(StringUtils.isBlank(state) ? "" : "&state=".concat(state));
      LOG.info("Sending error response, a redirect to: {}", redirectUri);
      response.sendRedirect(redirectUri);
    } else {
      LOG.info("Sending error response 'bad request': {}", validate.getDescription());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, validate.getDescription());
    }
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void setAuthenticator(AbstractAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  public static boolean isValidUrl(String redirectUri) {
    try {
      new URL(redirectUri);
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }

  /**
   * @param authorizationRequestRepository
   *          the authorizationRequestRepository to set
   */
  public void setAuthorizationRequestRepository(AuthorizationRequestRepository authorizationRequestRepository) {
    this.authorizationRequestRepository = authorizationRequestRepository;
  }

  /**
   * @param oAuth2Validator
   *          the oAuth2Validator to set
   */
  public void setOAuth2Validator(OAuth2Validator oAuth2Validator) {
    this.oAuth2Validator = oAuth2Validator;
  }

}
