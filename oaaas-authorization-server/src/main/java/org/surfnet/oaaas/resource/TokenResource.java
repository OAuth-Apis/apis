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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.surfnet.oaaas.auth.AuthenticationHandler;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

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
  private AuthenticationHandler authenticationHandler;

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  /**
   * 
   * @param responseType
   *          Value MUST be set to either code for requesting an authorization
   *          code or to token when requesting an access token (implicit grant)
   * @param clientId
   *          A unique client identifier issued by the authorization server when
   *          the client was registered
   * @param redirectUri
   *          The redirection endpoint where the authorization server redirects
   *          the resource owner's user-agent back to the client
   * @param scope
   *          Access Token scope allowing the client the client to specify the
   *          scope of the access request
   * @param state
   *          The client SHOULD utilize the "state" request parameter to deliver
   *          this value to the authorization server when making an
   *          authorization request.
   */
  @GET
  @Path("/authorize")
  public Response authorize(@QueryParam("response_type")
  String responseType, @QueryParam("client_id")
  String clientId, @QueryParam("redirect_uri")
  String redirectUri, @QueryParam("scope")
  String scope, @QueryParam("state")
  String state, @Context ThreadLocal<HttpServletRequest> treq, 
  @Context ThreadLocal<HttpServletResponse> tres ) {
    // TODO get the clientApp and validate / overwrite the redirectUri
    String csrfValue = UUID.randomUUID().toString();
    AuthorizationRequest authReq = new AuthorizationRequest(responseType, clientId, redirectUri, scope, state,
        csrfValue);
    authorizationRequestRepository.save(authReq);
    HttpServletRequest request = treq.get();
    HttpServletResponse response = tres.get();
    return authenticationHandler.handle(request, response, "authorizeCallback", csrfValue);
  }

  @GET
  @Path("/authorizeCallback")
  public Response authorizeCallback(@QueryParam("csrfValue")
  String csrfValue) {
    AuthorizationRequest authReq = authorizationRequestRepository.findByCsrfValue(csrfValue);
    // TODO get the principal from ???
    Principal principal = new Principal() {
      @Override
      public String getName() {
        return "Pitje Puk";
      }
    };
    String authorizationCode = UUID.randomUUID().toString();
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
