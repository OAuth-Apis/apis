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

import org.apache.commons.lang.StringUtils;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

@Named
public class AuthenticationFilter implements Filter {


  private Filter authenticator;

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    if (principalSet(request)) {
      chain.doFilter(request, response);
    } else if (initialRequest(request)) {
      String csrfValue = UUID.randomUUID().toString();

      AuthorizationRequest authReq = new AuthorizationRequest(
          request.getParameter("response_type"),
          request.getParameter("client_id"),
          request.getParameter("redirect_uri"),
          request.getParameter("scope"),
          request.getParameter("state"),
          csrfValue);
      authorizationRequestRepository.save(authReq);
      request.setAttribute("csrfValue", csrfValue);
      request.setAttribute("returnUri", request.getRequestURI());
      authenticator.doFilter(request, response, chain);
    } else {
      authenticator.doFilter(request, response, chain);
    }
  }

  private boolean initialRequest(HttpServletRequest request) {
    return StringUtils.isNotEmpty(request.getParameter("response_type"));
  }

  private boolean principalSet(ServletRequest request) {
    return request.getAttribute("principal") != null && request.getAttribute("principal") instanceof Principal;
  }

  @Override
  public void destroy() {
  }

  public void setAuthenticator(Filter authenticator) {
    this.authenticator = authenticator;
  }
}
