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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

/**
 *
 * {@link Filter} that ensures the Resource Owner grants consent for the use of
 * the Resource Server data to the Client app.
 *
 */
public class UserConsentFilter extends AuthorizationSupport implements Filter {

  private static final String RETURN_URI = "/oauth2/consent";

  private final AuthorizationRequestRepository authorizationRequestRepository;

  private final AbstractUserConsentHandler userConsentHandler;

  public UserConsentFilter(AuthorizationRequestRepository authorizationRequestRepository, AbstractUserConsentHandler userConsentHandler) {
    this.authorizationRequestRepository = authorizationRequestRepository;
    this.userConsentHandler = userConsentHandler;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    AuthorizationRequest authorizationRequest = findAuthorizationRequest(request);
    if (authorizationRequest == null) {
      response
          .sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid AbstractAuthenticator.AUTH_STATE on the Request");
    } else {
      if (initialRequest(request)) {
        storePrincipal(request, response, authorizationRequest);
        request.setAttribute(AbstractAuthenticator.RETURN_URI, RETURN_URI);
        request.setAttribute(AbstractUserConsentHandler.CLIENT, authorizationRequest.getClient());
        if (!authorizationRequest.getClient().isSkipConsent()) {
          userConsentHandler.handleUserConsent(request, response, chain, getAuthStateValue(request), getReturnUri(request), authorizationRequest.getClient());
        } else {
          chain.doFilter(request, response);
        }
      } else {
        /*
         * Ok, the consentHandler wants to have control again (because he stepped
         * out)
         */
        userConsentHandler.handleUserConsent(request, response, chain, getAuthStateValue(request), getReturnUri(request), authorizationRequest.getClient());
      }
    }
  }

  private AuthorizationRequest findAuthorizationRequest(HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    if (StringUtils.isBlank(authState)) {
      authState = request.getParameter(AbstractAuthenticator.AUTH_STATE);
    }
    return authorizationRequestRepository.findByAuthState(authState);
  }

  private void storePrincipal(HttpServletRequest request, HttpServletResponse response,
      AuthorizationRequest authorizationRequest) throws IOException {
    AuthenticatedPrincipal principal = (AuthenticatedPrincipal) request.getAttribute(AbstractAuthenticator.PRINCIPAL);
    if (principal == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No valid AbstractAuthenticator.PRINCIPAL on the Request");
    }
    authorizationRequest.setPrincipal(principal);
    authorizationRequestRepository.save(authorizationRequest);
  }

  private boolean initialRequest(HttpServletRequest request) {
    return (AuthenticatedPrincipal) request.getAttribute(AbstractAuthenticator.PRINCIPAL) != null;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

}
