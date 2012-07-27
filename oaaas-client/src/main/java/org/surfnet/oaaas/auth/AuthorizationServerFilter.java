/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Base64;

import com.sun.jersey.api.client.Client;

/**
 * {@link Filter} which can be used to protect all relevant resources by
 * validating the oauth access token with the Authorization server. This is an
 * example configuration:
 * 
 * <pre>
 * {@code
 * <filter> 
 *   <filter-name>authorization-server</filter-name>
 *   <filter-class>org.surfnet.oaaas.auth.AuthorizationServerFilter</filter-class>
 *   <init-param> 
 *     <param-name>resource-server-name</param-name>
 *     <param-value>university-foo</param-value> 
 *   </init-param> 
 *   <init-param> 
 *     <param-name>resource-server-secret</param-name>
 *     <param-value>58b749f7-acb3-44b7-a38c-53d5ad740cf6</param-value> 
 *   </init-param> 
 *   <init-param> 
 *     <param-name>authorization-server-url</param-name>
 *     <param-value>http://<host-name>/v1/tokeninfo</param-value> 
 *   </init-param> 
 * </filter> 
 * <filter-mapping> 
 *   <filter-name>authorization-server</filter-name>
 *  <url-pattern>/*</url-pattern> 
 * </filter-mapping> 
 * }
 * </pre>
 * 
 * The response of the Authorization Server is put on the
 * {@link HttpServletRequest}.
 * 
 * Of course it might be better to use a properties file depending on the
 * environment (e.g. OTAP) to get the name, secret and url. This can be achieved
 * simple to override the {@link AuthorizationServerFilter#init(FilterConfig)}
 */
public class AuthorizationServerFilter implements Filter {

  /*
   * Endpoint of the authorization server (e.g. something like
   * http://<host-name>/v1/tokeninfo)
   */
  private String authorizationServerUrl;

  /*
   * Base64-encoded concatenation of the name of the resource server and the
   * secret separated with a semicolon
   */
  private String authorizationValue;

  /*
   * Client to make GET calls to the authorization server
   */
  private Client client = Client.create();

  /*
   * Constant for the access token (oauth2 spec)
   */
  private static final String BEARER = "bearer";

  /*
   * Constant name of the request attribute where the userId is stored
   */
  public static final String VERIFY_TOKEN_RESPONSE = "VERIFY_TOKEN_RESPONSE";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    String name = filterConfig.getInitParameter("resource-server-name");
    String secret = filterConfig.getInitParameter("resource-server-secret");

    this.authorizationServerUrl = filterConfig.getInitParameter("authorization-server-url");
    /*
     * See http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=
     * c742d1615af66e3dd9568f7632ab3?bug_id=6947917
     */
    this.authorizationValue = new String(Base64.encodeBase64String(name.concat(":").concat(secret).getBytes()))
        .replaceAll("\r\n?", "");

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String accessToken = getAccessToken(request);
    if (accessToken != null) {
      VerifyTokenResponse tokenResponse = client
          .resource(String.format(authorizationServerUrl.concat("?access_token=%s"), accessToken))
          .header(HttpHeaders.AUTHORIZATION, authorizationValue).accept("application/json")
          .get(VerifyTokenResponse.class);
      if (tokenResponse.getUser_id() != null) {
        request.setAttribute(VERIFY_TOKEN_RESPONSE, tokenResponse);
        chain.doFilter(request, response);
        return;
      }
    }
    sendError(response);
  }

  private void sendError(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "OAuth2 endpoint");
    response.flushBuffer();
  }

  private String getAccessToken(HttpServletRequest request) {
    String accessToken = null;
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null) {
      int space = header.indexOf(' ');
      if (space > 0) {
        String method = header.substring(0, space);
        if (BEARER.equalsIgnoreCase(method)) {
          accessToken = header.substring(space + 1);
        }
      }
    }
    return accessToken;
  }

  @Override
  public void destroy() {
  }

}
