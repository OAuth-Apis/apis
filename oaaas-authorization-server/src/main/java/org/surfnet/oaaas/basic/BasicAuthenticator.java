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

package org.surfnet.oaaas.basic;

import java.io.IOException;
import java.security.Principal;

import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;

/**
 * Authenticator that uses HTTP Basic Authentication.
 */
@Named("basicAuthenticator")
public class BasicAuthenticator extends AbstractAuthenticator {

  private String realm = "user: pietje, pass: puk";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
      ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    HttpServletRequest request = (HttpServletRequest) req;

    final UserPassCredentials credentials = new UserPassCredentials(request.getHeader("Authorization"));

    if (authenticate(credentials)) {
      request.setAttribute("principal", new Principal() {
        public String getName() {
           return credentials.getUsername();
        }
      });
      chain.doFilter(request, response);
    } else {
      response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization required");
      response.flushBuffer();
    }
  }

  private boolean authenticate(UserPassCredentials credentials) {
    // TODO: actually perform authentication at user repository
    return "pietje".equals(credentials.getUsername()) && "puk".equals(credentials.getPassword());
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }

  /**
   * Holder and parser for the username and password from the authentication header.
   */
  private static final class UserPassCredentials {

    private static final int BASIC_AUTH_PREFIX_LENGTH = "Basic ".length();

    private final String username;
    private final String password;
    private final static Logger LOG = LoggerFactory.getLogger(UserPassCredentials.class);

    /**
     * Parse the username and password from the authorization header. If
     * the username and password cannot be found they are set to null.
     * @param authorizationHeader the authorization header
     */
    UserPassCredentials(final String authorizationHeader) {
      if (authorizationHeader == null || authorizationHeader.length() < BASIC_AUTH_PREFIX_LENGTH) {
        LOG.debug("Authorization header not found.");
        username = null;
        password = null;
        return;
      }

      String authPart = authorizationHeader.substring(BASIC_AUTH_PREFIX_LENGTH);
      String userpass = new String(Base64.decodeBase64(authPart));
      if (userpass.indexOf(':') < 1) {
        LOG.debug("Invalid authorization header found.");
        username = null;
        password = null;
        return;
      }
      username = userpass.substring(0, userpass.indexOf(':'));
      password = userpass.substring(userpass.indexOf(':') + 1);
    }

    /**
     * Get the username.
     * @return the username or null if the username was not found
     */
    String getUsername() {
      return username;
    }

    /**
     * Get the password.
     * @return the password or null if the password was not found
     */
    String getPassword() {
      return password;
    }

  }
}
