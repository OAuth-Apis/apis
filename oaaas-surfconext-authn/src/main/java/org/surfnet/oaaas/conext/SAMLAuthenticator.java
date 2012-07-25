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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.surfnet.oaaas.auth.AbstractAuthenticator;

import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

public class SAMLAuthenticator extends AbstractAuthenticator {

  private static final Logger LOG = LoggerFactory.getLogger(SAMLAuthenticator.class);
  private static final String SSO_URL = "/conext-trigger";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
      ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    LOG.debug("Hitting SAML Authenticator filter");

    if (authentication instanceof SAMLAuthenticationToken && authentication.isAuthenticated()) {

      SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
      final Object principal = token.getPrincipal();
      LOG.debug("authentication: {}. Will set principal to: {}", authentication, principal);
      request.setAttribute("principal", principal);

      chain.doFilter(request, response);

    } else {

      LOG.debug("Not yet authenticated. Will redirect to SAML Login url '{}'", SSO_URL);
      response.sendRedirect(SSO_URL);

    }
  }
}
