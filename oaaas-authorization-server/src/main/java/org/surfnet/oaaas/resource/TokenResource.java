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
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

/**
 * Resource for handling all calls related to tokens. It adheres to <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2"> the OAuth spec</a>.
 * 
 */
@Named
@Path("/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Inject
  private AccessTokenRepository accessTokenRepository;

  private static final Logger LOG = LoggerFactory.getLogger(TokenResource.class);

  private static final String GRANT_TYPE = "authorization_code";
  
  private static final String BASIC = "basic";

  @GET
  @Path("/authorize")
  public Response authorizeCallbackGet(@Context
  HttpServletRequest request) {
    return authorizeCallback(request);
  }

  @POST
  @Path("/authorize")
  public Response authorizeCallback(@Context
  HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    if (authState == null) {
      LOG.warn("Null authState while in TokenResource#authorizeCallback");
      return Response.serverError().build();
    }
    AuthorizationRequest authReq = authorizationRequestRepository.findByAuthState(authState);
    if (authReq == null) {
      LOG.warn("Null AuthorizationRequest while in TokenResource#authorizeCallback processing authState {}", authState);
      return Response.serverError().build();
    }
    Principal principal = (Principal) request.getAttribute(AbstractAuthenticator.PRINCIPAL);
    if (principal == null) {
      LOG.warn("Null principal while in TokenResource#authorizeCallback");
      return Response.serverError().build();
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Principal from HttpServletRequest: {}", principal);
    }
    if (authReq.getResponseType().equals(OAuth2Validator.IMPLICIT_GRANT_RESPONSE_TYPE)) {
      // TODO Implement refresh tokens
      AccessToken accessToken = new AccessToken(UUID.randomUUID().toString(), principal.getName(), authReq.getClient(),
          0, authReq.getScope());
      accessToken = accessTokenRepository.save(accessToken);
      return sendImplicitGrantResponse(authReq, accessToken);
    } else {
      return sendAuthorizationCodeResponse(authReq);
    }
  }

/*
  @POST
  @Path("/token")
  public Response token(@HeaderParam("Authorization")
  String authorization, @Valid AccessTokenRequest accessTokenRequest) {
    */
/*
     * http://tools.ietf.org/html/draft-ietf-oauth-v2#section-2.3.1
     *
     * We support both options. Clients can use the Basic Authentication or
     * include the secret and id  in the request body
     *//*

    if (StringUtils.isBlank(authorization)) {

    } else {

    }
    String uri = null;
    return redirect(uri);
  }
*/

  private Response sendAuthorizationCodeResponse(AuthorizationRequest authReq) {
    String uri = authReq.getRedirectUri();
    String authorizationCode = UUID.randomUUID().toString();
    uri = uri + appendQueryMark(uri) + "code=" + authorizationCode + appendStateParameter(authReq);
    return redirect(uri);
  }

  private Response sendImplicitGrantResponse(AuthorizationRequest authReq, AccessToken accessToken) {
    String uri = authReq.getRedirectUri();
    uri = String.format(uri + "#access_token=%s&token_type=bearer&expires_in=%s&scope=%s"
        + appendStateParameter(authReq), accessToken.getToken(), accessToken.getExpires(), authReq.getScope());
    return redirect(uri);
  }

  private String appendQueryMark(String uri) {
    return uri.contains("?") ? "&" : "?";
  }

  private String appendStateParameter(AuthorizationRequest authReq) {
    String state = authReq.getState();
    return StringUtils.isBlank(state) ? "" : "&state=".concat(state);
  }

  private Response redirect(String uri) {
    try {
      return Response.seeOther(new URI(uri)).build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Redirect URI '%s' is not valid", uri));
    }
  }

}
