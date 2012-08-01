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
package org.surfnet.oaaas.resource;

import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.surfnet.oaaas.auth.UserConsentHandler;
import org.surfnet.oaaas.auth.principal.RolesPrincipal;
import org.surfnet.oaaas.model.AuthorizationRequest;

/**
 * 
 *
 */
@Named
public class UserConsentResource implements UserConsentHandler{

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.UserConsentHandler#isConsentRequired(org.surfnet.oaaas.model.AuthorizationRequest)
   */
  @Override
  public boolean isConsentRequired(AuthorizationRequest authorizationRequest) {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.UserConsentHandler#handleConsent(org.surfnet.oaaas.model.AuthorizationRequest, org.surfnet.oaaas.auth.principal.RolesPrincipal)
   */
  @Override
  public Response handleConsent(AuthorizationRequest authorizationReques, RolesPrincipal rolesPrincipal) {
    // TODO Auto-generated method stub
    return null;
  }

}
