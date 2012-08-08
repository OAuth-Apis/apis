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
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.ClientResponse.Status;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.AbstractUserConsentHandler;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.auth.principal.UserPassCredentials;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.AccessTokenRequest;
import org.surfnet.oaaas.model.AccessTokenResponse;
import org.surfnet.oaaas.model.AuthorizationRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ErrorResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

/**
 * Resource for handling all calls related to tokens. It adheres to <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2"> the OAuth spec</a>.
 * 
 */
@Named
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Inject
  private AccessTokenRepository accessTokenRepository;

  private static final Logger LOG = LoggerFactory.getLogger(TokenResource.class);

  private static final String GRANT_TYPE = "authorization_code";
  private static final String BEARER = "bearer";

  
  @GET
  @Path("/test")
  public Response test(@Context
  HttpServletRequest request) {
    return Response.ok(new ErrorResponse("no_error","wtf")).build();
  }

  @GET
  @Path("/authorize")
  public Response authorizeCallbackGet(@Context
  HttpServletRequest request) {
    return authorizeCallback(request);
  }

  /**
   *  Entry point for the authorize call which needs to return an authorization code or (implicit grant) an access token
   * 
   * @param request
   *          the {@link HttpServletRequest}
   * @return Response the response
   */
  @POST
  @Path("/authorize")
  public Response authorizeCallback(@Context
  HttpServletRequest request) {
    return doProcess(request);
  }

  /**
   *  Called after the user has given consent
   * 
   * @param request
   *          the {@link HttpServletRequest}
   * @return Response the response
   */
  @POST
  @Path("/consent")
  public Response consentCallback(@Context
  HttpServletRequest request) {
    return doProcess(request);
  }
  
  private Response doProcess(HttpServletRequest request) {
    AuthorizationRequest authReq = findAuthorizationRequest(request);
    if (authReq == null) {
      return serverError("Not a valid AbstractAuthenticator.AUTH_STATE on the Request");
    }
    processScopes(authReq, request);
    if (authReq.getResponseType().equals(OAuth2Validator.IMPLICIT_GRANT_RESPONSE_TYPE)) {
      AccessToken token = createAccessToken(authReq);
      return sendImplicitGrantResponse(authReq, token);
    } else {
      return sendAuthorizationCodeResponse(authReq);
    }
  }
  
  

  /*
   * In the user consent filter the scopes are (possible) set on the Request
   */
  private void processScopes(AuthorizationRequest authReq, HttpServletRequest request) {
    String[] scopes = (String[]) request.getAttribute(AbstractUserConsentHandler.GRANTED_SCOPES);
    if (ArrayUtils.isNotEmpty(scopes)) {
      authReq.setScopes(StringUtils.join(scopes, ","));
    }
  }

  private AccessToken createAccessToken(AuthorizationRequest authReq) {
    Client client = authReq.getClient();
    long expireDuration = client.getExpireDuration();
    long expires = (expireDuration == 0L ? 0L : (System.currentTimeMillis() + (1000 * expireDuration)));
    AccessToken token = new AccessToken(getTokenValue(), authReq.getPrincipal(), client, expires, authReq.getScopes());
    return accessTokenRepository.save(token);
  }

  private AuthorizationRequest findAuthorizationRequest(HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    return authorizationRequestRepository.findByAuthState(authState);
  }

  @POST
  @Path("/token")
  @Consumes("application/x-www-form-urlencoded")
  public Response token(@HeaderParam("Authorization")
  String authorization, final MultivaluedMap<String, String> formParameters) {
    AccessTokenRequest accessTokenRequest = AccessTokenRequest.fromMultiValuedFormParameters(formParameters);
    AuthorizationRequest authReq = authorizationRequestRepository.findByAuthorizationCode(accessTokenRequest.getCode());
    if (authReq == null) {
      return sendErrorResponse("invalid_grant", "The authorization code is not valid");
    }
    Client client = authReq.getClient();
    UserPassCredentials credentials = getUserPassCredentials(authorization, accessTokenRequest);

    if (!client.isExactMatch(credentials)) {
      return Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"OAuth2 Secure\"").build();
    }
    
    if (!GRANT_TYPE.equals(accessTokenRequest.getGrantType())) {
      return sendErrorResponse("unsupported_grant_type", "Grant Type must be 'authorization_code'");
    }
    
    String uri = accessTokenRequest.getRedirectUri();
    if (!authReq.getRedirectUri().equalsIgnoreCase(uri)) {
      return sendErrorResponse("invalid_request", "The redirect_uri does not match the initial authorization request");
    }
    
    AccessToken token = createAccessToken(authReq);
    AccessTokenResponse response = new AccessTokenResponse(token.getToken(), BEARER, client.getExpireDuration(),
        null, token.getScopes());
    return Response.ok().entity(response).build();
  }

  /*
   * http://tools.ietf.org/html/draft-ietf-oauth-v2#section-2.3.1
   * 
   * We support both options. Clients can use the Basic Authentication or
   * include the secret and id in the request body
   */

  private UserPassCredentials getUserPassCredentials(String authorization, AccessTokenRequest accessTokenRequest) {
    return StringUtils.isBlank(authorization) ? new UserPassCredentials(accessTokenRequest.getClientId(),
        accessTokenRequest.getClientSecret()) : new UserPassCredentials(authorization);
  }

  private Response sendAuthorizationCodeResponse(AuthorizationRequest authReq) {
    String uri = authReq.getRedirectUri();
    String authorizationCode = getAuthorizationCodeValue();
    authReq.setAuthorizationCode(authorizationCode);
    authorizationRequestRepository.save(authReq);
    uri = uri + appendQueryMark(uri) + "code=" + authorizationCode + appendStateParameter(authReq);
    return redirect(uri);
  }

  protected String getTokenValue() {
    return UUID.randomUUID().toString();
  }

  protected String getAuthorizationCodeValue() {
    return getTokenValue();
  }

  private Response sendErrorResponse(String error, String description) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorResponse(error, description)).build();
  }

  private Response sendImplicitGrantResponse(AuthorizationRequest authReq, AccessToken accessToken) {
    String uri = authReq.getRedirectUri();
    uri = String.format(uri + "#access_token=%s&token_type=bearer&expires_in=%s&scope=%s"
        + appendStateParameter(authReq), accessToken.getToken(), accessToken.getExpires(), authReq.getScopes());
    return redirect(uri);
  }

  private String appendQueryMark(String uri) {
    return uri.contains("?") ? "&" : "?";
  }

  private String appendStateParameter(AuthorizationRequest authReq) {
    String state = authReq.getState();
    return StringUtils.isBlank(state) ? "" : "&state=".concat(state);
  }

  private Response serverError(String msg) {
    LOG.warn(msg);
    return Response.serverError().build();
  }

  private Response redirect(String uri) {
    try {
      return Response.seeOther(new URI(uri)).build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Redirect URI '%s' is not valid", uri));
    }
  }

  /**
   * @param authorizationRequestRepository
   *          the authorizationRequestRepository to set
   */
  public void setAuthorizationRequestRepository(AuthorizationRequestRepository authorizationRequestRepository) {
    this.authorizationRequestRepository = authorizationRequestRepository;
  }

  /**
   * @param accessTokenRepository
   *          the accessTokenRepository to set
   */
  public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
    this.accessTokenRepository = accessTokenRepository;
  }


}
