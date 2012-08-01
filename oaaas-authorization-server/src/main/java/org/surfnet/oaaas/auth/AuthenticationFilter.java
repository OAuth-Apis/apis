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
import java.security.Principal;
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

  private Filter authenticator;

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Inject
  private OAuth2Validator oAuth2Validator;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (principalSet(request)) {
      chain.doFilter(request, res);
    } else if (initialRequest(request)) {
      String responseType = request.getParameter("response_type");
      String clientId = request.getParameter("client_id");
      String redirectUri = request.getParameter("redirect_uri");
      String scope = request.getParameter("scope");
      String state = request.getParameter("state");
      String authState = UUID.randomUUID().toString();

      AuthorizationRequest authReq = new AuthorizationRequest(responseType, clientId, redirectUri, scope, state,
          authState);

      ValidationResponse validate = oAuth2Validator.validate(authReq);
      if (validate.valid()) {
        try {
          authorizationRequestRepository.save(authReq);
        } catch (Exception e) {
          LOG.error("while saving authorization request", e);
          throw new ServletException("Cannot save authorization request");
        }

        request.setAttribute(AbstractAuthenticator.AUTH_STATE, authState);
        request.setAttribute(AbstractAuthenticator.RETURN_URI, request.getRequestURI());
        authenticator.doFilter(request, response, chain);
      } else {
        sendError(response, authReq, validate);
      }
    } else {
      authenticator.doFilter(request, response, chain);
    }
  }

  private void sendError(HttpServletResponse response, AuthorizationRequest authReq, ValidationResponse validate)
      throws IOException {
    LOG.info("Will send error response for authorization request '{}', validation result: {}", authReq, validate);
    String redirectUri = authReq.getRedirectUri();
    String state = authReq.getState();
    if (isValidUrl(redirectUri)) {
      String location = redirectUri;
      if (redirectUri.contains("?")) {
        location += "&";
      } else {
        location += "?";
      }
      location = location
          .concat("error=")
          .concat(validate.getValue())
          .concat("&error_description=")
          .concat(validate.getDescription())
          .concat(StringUtils.isBlank(state) ? "" : "&state=".concat(state));
      LOG.info("Sending error response, a redirect to: {}", location);
      response.sendRedirect(location);
    } else {
      LOG.info("Sending error response 'bad request': {}", validate.getDescription());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, validate.getDescription());
    }
  }

  private boolean initialRequest(HttpServletRequest request) {
    return StringUtils.isNotEmpty(request.getParameter("response_type"));
  }

  protected boolean principalSet(ServletRequest request) {
    return request.getAttribute(AbstractAuthenticator.PRINCIPAL) != null
        && request.getAttribute(AbstractAuthenticator.PRINCIPAL) instanceof Principal;
  }

  @Override
  public void destroy() {
  }

  public void setAuthenticator(Filter authenticator) {
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
}
