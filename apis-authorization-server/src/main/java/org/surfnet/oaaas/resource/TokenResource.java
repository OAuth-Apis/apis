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

import static org.surfnet.oaaas.auth.OAuth2Validator.BEARER;
import static org.surfnet.oaaas.auth.OAuth2Validator.GRANT_TYPE_AUTHORIZATION_CODE;
import static org.surfnet.oaaas.auth.OAuth2Validator.GRANT_TYPE_REFRESH_TOKEN;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.*;
import org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse;
import org.surfnet.oaaas.auth.principal.UserPassCredentials;
import org.surfnet.oaaas.model.*;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * Resource for handling all calls related to tokens. It adheres to <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2"> the OAuth spec</a>.
 *
 */
@Named
@Path("/")
public class TokenResource {

  public static final String BASIC_REALM = "Basic realm=\"OAuth2 Secure\"";

  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

  @Inject
  private AuthorizationRequestRepository authorizationRequestRepository;

  @Inject
  private AccessTokenRepository accessTokenRepository;

  @Inject
  private OAuth2Validator oAuth2Validator;

  private static final Logger LOG = LoggerFactory.getLogger(TokenResource.class);

  @GET
  @Path("/authorize")
  public Response authorizeCallbackGet(@Context
  HttpServletRequest request) {
    return authorizeCallback(request);
  }

  /**
   * Entry point for the authorize call which needs to return an authorization
   * code or (implicit grant) an access token
   *
   * @param request
   *          the {@link HttpServletRequest}
   * @return Response the response
   */
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Path("/authorize")
  public Response authorizeCallback(@Context
  HttpServletRequest request) {
    return doProcess(request);
  }

  /**
   * Called after the user has given consent
   *
   * @param request
   *          the {@link HttpServletRequest}
   * @return Response the response
   */
  @POST
  @Produces(MediaType.TEXT_HTML)
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
      AccessToken token = createAccessToken(authReq, true);
      return sendImplicitGrantResponse(authReq, token);
    } else {
      return sendAuthorizationCodeResponse(authReq);
    }
  }

  /*
   * In the user consent filter the scopes are (possible) set on the Request
   */
  private void processScopes(AuthorizationRequest authReq, HttpServletRequest request) {
    if (authReq.getClient().isSkipConsent()) {
      // return the scopes in the authentication request since the requested scopes are stored in the
      // authorizationRequest.
      authReq.setGrantedScopes(authReq.getRequestedScopes());
    } else {
      String[] scopes = (String[]) request.getAttribute(AbstractUserConsentHandler.GRANTED_SCOPES);
      if (ArrayUtils.isNotEmpty(scopes)) {
        authReq.setGrantedScopes(Arrays.asList(scopes));
      } else {
        authReq.setGrantedScopes(null);
      }
    }
  }

  private AccessToken createAccessToken(AuthorizationRequest request, boolean isImplicitGrant) {
    Client client = request.getClient();
    long expireDuration = client.getExpireDuration();
    long expires = (expireDuration == 0L ? 0L : (System.currentTimeMillis() + (1000 * expireDuration)));
    String refeshToken = (client.isUseRefreshTokens() && !isImplicitGrant) ? getTokenValue(true) : null;
    AccessToken token = new AccessToken(getTokenValue(false), request.getPrincipal(), client, expires,
        request.getGrantedScopes(), refeshToken);
    return accessTokenRepository.save(token);
  }

  private AuthorizationRequest findAuthorizationRequest(HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    return authorizationRequestRepository.findByAuthState(authState);
  }

  @POST
  @Path("/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes("application/x-www-form-urlencoded")
  public Response token(@HeaderParam("Authorization")
  String authorization, final MultivaluedMap<String, String> formParameters) {
    AccessTokenRequest accessTokenRequest = AccessTokenRequest.fromMultiValuedFormParameters(formParameters);
    ValidationResponse vr = oAuth2Validator.validate(accessTokenRequest);
    if (!vr.valid()) {
      return sendErrorResponse(vr);
    }
    String grantType = accessTokenRequest.getGrantType();
    AuthorizationRequest request;
    try {
      if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
        request = authorizationCodeToken(accessTokenRequest);
      } else if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
        request = refreshTokenToken(accessTokenRequest);
      } else {
        return sendErrorResponse(ValidationResponse.UNSUPPORTED_GRANT_TYPE);
      }
    } catch (ValidationResponseException e) {
      return sendErrorResponse(e.v);
    }
    UserPassCredentials credentials = getUserPassCredentials(authorization, accessTokenRequest);
    if (!request.getClient().isExactMatch(credentials)) {
      return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
    }
    AccessToken token = createAccessToken(request, false);

    AccessTokenResponse response = new AccessTokenResponse(token.getToken(), BEARER, request.getClient()
        .getExpireDuration(), token.getRefreshToken(), StringUtils.join(token.getScopes(), ','));

    return Response.ok().entity(response).build();

  }

  private AuthorizationRequest authorizationCodeToken(AccessTokenRequest accessTokenRequest) {
    AuthorizationRequest authReq = authorizationRequestRepository.findByAuthorizationCode(accessTokenRequest.getCode());
    if (authReq == null) {
      throw new ValidationResponseException(ValidationResponse.INVALID_GRANT_AUTHORIZATION_CODE);
    }
    String uri = accessTokenRequest.getRedirectUri();
    if (!authReq.getRedirectUri().equalsIgnoreCase(uri)) {
      throw new ValidationResponseException(ValidationResponse.REDIRECT_URI_DIFFERENT);
    }
    authorizationRequestRepository.delete(authReq);
    return authReq;
  }

  private AuthorizationRequest refreshTokenToken(AccessTokenRequest accessTokenRequest) {
    AccessToken accessToken = accessTokenRepository.findByRefreshToken(accessTokenRequest.getRefreshToken());
    if (accessToken == null) {
      throw new ValidationResponseException(ValidationResponse.INVALID_GRANT_REFRESH_TOKEN);
    }
    AuthorizationRequest request = new AuthorizationRequest();
    request.setClient(accessToken.getClient());
    request.setPrincipal(accessToken.getPrincipal());
    request.setGrantedScopes(accessToken.getScopes());
    accessTokenRepository.delete(accessToken);
    return request;

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

  protected String getTokenValue(boolean isRefreshToken) {
    return UUID.randomUUID().toString();
  }

  protected String getAuthorizationCodeValue() {
    return getTokenValue(false);
  }

  private Response sendErrorResponse(String error, String description) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorResponse(error, description)).build();
  }

  private Response sendErrorResponse(ValidationResponse response) {
    return sendErrorResponse(response.getValue(), response.getDescription());
  }

  private Response sendImplicitGrantResponse(AuthorizationRequest authReq, AccessToken accessToken) {
    String uri = authReq.getRedirectUri();
    uri = String.format(uri + "#access_token=%s&token_type=bearer&expires_in=%s&scope=%s"
        + appendStateParameter(authReq), accessToken.getToken(), accessToken.getExpires(), StringUtils.join(authReq.getGrantedScopes(), ','));
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

  /**
   * @param oAuth2Validator
   *          the oAuth2Validator to set
   */
  public void setoAuth2Validator(OAuth2Validator oAuth2Validator) {
    this.oAuth2Validator = oAuth2Validator;
  }

}
