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
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse;
import org.surfnet.oaaas.auth.principal.RolesPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

@Named
public class UserConsentFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(UserConsentFilter.class);

  private static final String MAPPING_URL = "mapping-url";

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  private AbstractUserConsentHandler userConsentHandler;

  private String returnUri = "/oauth2/consent";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    AuthorizationRequest authorizationRequest = findAuthorizationRequest(request);
    if (authorizationRequest == null) {
      response
          .sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid AbstractAuthenticator.AUTH_STATE on the Request");
    }
    if (initialRequest(request)) {
      storePrincipal(request, response, authorizationRequest);
      request.setAttribute(AbstractAuthenticator.RETURN_URI, returnUri);
      if (!authorizationRequest.getClient().isSkipConsent()) {
        userConsentHandler.doFilter(request, response, chain);
      }
    } else {
      /*
       * Ok, the consentHandler wants to have control again (because he stepped
       * out)
       */
      chain.doFilter(request, response);
    }
  }

  private AuthorizationRequest findAuthorizationRequest(HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    return authorizationRequestRepository.findByAuthState(authState);
  }

  private void storePrincipal(HttpServletRequest request, HttpServletResponse response,
      AuthorizationRequest authorizationRequest) throws IOException {
    RolesPrincipal principal = (RolesPrincipal) request.getAttribute(AbstractAuthenticator.PRINCIPAL);
    if (principal == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid AbstractAuthenticator.PRINCIPAL on the Request");
    }
    authorizationRequest.complete(principal);
    authorizationRequestRepository.save(authorizationRequest);
  }

  private boolean initialRequest(HttpServletRequest request) {
    return (RolesPrincipal) request.getAttribute(AbstractAuthenticator.PRINCIPAL) != null;
  }

  /**
   * @param authorizationRequestRepository
   *          the authorizationRequestRepository to set
   */
  public void setAuthorizationRequestRepository(AuthorizationRequestRepository authorizationRequestRepository) {
    this.authorizationRequestRepository = authorizationRequestRepository;
  }

  /**
   * @param userConsentHandler
   *          the userConsentHandler to set
   */
  public void setUserConsentHandler(AbstractUserConsentHandler userConsentHandler) {
    this.userConsentHandler = userConsentHandler;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
//    this.returnUri = filterConfig.getInitParameter(MAPPING_URL);
//    if (StringUtils.isBlank(returnUri)) {
//      throw new ServletException("Must provide an init parameter '" + MAPPING_URL
//          + "' that can serve as returnUri for AbstractUserConsentHandler instances (e.g. 'oauth2/consent') ");
//    }
  }

  @Override
  public void destroy() {

  }

}
