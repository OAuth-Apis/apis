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
package org.surfnet.oaaas.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

/**
 * Resource for handling all calls related to tokens. It adheres to <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2"> the OAuth spec</a>.
 * 
 */
@Component
@Path("/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;
  private static final Logger LOG = LoggerFactory.getLogger(TokenResource.class);

  @GET
  @Path("/authorize")
  public Response authorizeCallbackGet(@Context HttpServletRequest request) {
    return authorizeCallback(request);
  }

  @POST
  @Path("/authorize")
  public Response authorizeCallback(@Context HttpServletRequest request) {
    String csrfValue = (String) request.getAttribute("csrfValue");
    AuthorizationRequest authReq = authorizationRequestRepository.findByCsrfValue(csrfValue);

    Principal principal = (Principal) request.getAttribute("principal");
    /*
     * TODO save principal in AuthorizationRequest
     */
    LOG.debug("principal: {}", principal);
    String authorizationCode = UUID.randomUUID().toString();
    /*
     * TODO implicit grant check
     */
    String uri = String.format(authReq.getRedirectUri().concat("?").concat("code=%s").concat("&state=%s"),
        authorizationCode, authReq.getState());
    try {
      return Response.seeOther(new URI(uri)).build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Redirect URI '%s' is not valid", uri));
    }
    // return Response.status(Response.Status.UNAUTHORIZED).build();
  }
}
