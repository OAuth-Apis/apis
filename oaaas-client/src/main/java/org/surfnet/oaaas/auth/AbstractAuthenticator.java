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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * To be implemented by various authentication methods.
 */
public abstract class AbstractAuthenticator implements Filter {

  private static final String CSRF_VALUE = "csrfValue";
  private static final String RETURN_URI = "returnUri";

  /**
   * Implement this method to perform the actual authentication.
   * Use {@link org.surfnet.oaaas.basic.BasicAuthenticator BasicAuthenticator}
   * or {@link org.surfnet.oaaas.simple.FormLoginAuthenticator FormLoginAuthenticator} as an example.
   *
   * In general, the contract is:
   *   <p>assert that the user is authenticated. You can use the request and response for this. When
   *   not yet authenticated:</p>
   *   <ul>
   *    <li>use {@link #getCsrfValue(javax.servlet.ServletRequest)} to pass-around for user agent communication </li>
   *    <li>use {@link #getReturnUri(javax.servlet.ServletRequest)} if you need to step out and return to the current
   *    location
   *   </ul>
   *   <p>When authenticated:</p>
   *   <ul>
   *    <li>call chain.doFilter(request, response) to continue.
   *   </ul>
   * @param request the ServletRequest
   * @param response the ServletResponse
   * @param chain the original http servlet filter chain
   */
  @Override
  public abstract void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  public final String getCsrfValue(ServletRequest request) {
    return (String) request.getAttribute(CSRF_VALUE);
  }

  public final String getReturnUri(ServletRequest request) {
    return (String) request.getAttribute(RETURN_URI);
  }
}
