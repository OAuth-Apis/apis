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

package org.surfnet.oaaas.noop;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;

/**
 * Grants isAdmin authority to the Principal
 */
public class NoopAdminAuthenticator extends NoopAuthenticator {

  @Override
  protected AuthenticatedPrincipal getAuthenticatedPrincipal() {
    AuthenticatedPrincipal principal = super.getAuthenticatedPrincipal();
    principal.setAdminPrincipal(true);
    return principal;
  }
}
