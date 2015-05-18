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

import com.sun.jersey.api.client.ClientResponse.Status;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AbstractAuthenticator;
import org.surfnet.oaaas.auth.AbstractUserConsentHandler;
import org.surfnet.oaaas.auth.AuthenticationFilter;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.auth.ResourceOwnerAuthenticator;
import org.surfnet.oaaas.auth.OAuth2Validator.*;
import org.surfnet.oaaas.auth.ValidationResponseException;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.auth.principal.BasicAuthCredentials;
import org.surfnet.oaaas.model.*;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.AuthorizationRequestRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.UUID;

import static org.surfnet.oaaas.auth.OAuth2Validator.*;

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
  
  @Inject
  private ResourceOwnerAuthenticator resourceOwnerAuthenticator;

  private static final Logger LOG = LoggerFactory.getLogger(TokenResource.class);

  /**
   * The "authorization endpoint" as described in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-3.1">Section 3.1</a> of
   * the OAuth spec.  This provides the optional GET support.  Access to this endpoint requires
   * authentication of the requestor (the resource owner) which must be accomplished via a
   * configured {@link AuthenticationFilter}.
   * 
   * @param request
   *          the {@link HttpServletRequest}
   * @return the response
   */
  @GET
  @Path("/authorize")
  public Response authorizeCallbackGet(@Context HttpServletRequest request) {
    return authorizeCallback(request);
  }

  /**
   * Entry point for the authorize call which needs to return an authorization
   * code or (implicit grant) an access token.    Access to this endpoint requires
   * authentication of the requestor (the resource owner) which must be accomplished via a
   * configured {@link AuthenticationFilter}.
   *
   * @param request
   *          the {@link HttpServletRequest}
   * @return Response the response
   */
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Path("/authorize")
  public Response authorizeCallback(@Context HttpServletRequest request) {
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
  public Response consentCallback(@Context HttpServletRequest request) {
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
      if (!ArrayUtils.isEmpty(scopes)) {
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
    String refreshToken = (client.isUseRefreshTokens() && !isImplicitGrant) ? getTokenValue(true) : null;
    AuthenticatedPrincipal principal = request.getPrincipal();
    AccessToken token = new AccessToken(getTokenValue(false), principal, client, expires, request.getGrantedScopes(), refreshToken);
    return accessTokenRepository.save(token);
  }

  private AuthorizationRequest findAuthorizationRequest(HttpServletRequest request) {
    String authState = (String) request.getAttribute(AbstractAuthenticator.AUTH_STATE);
    return authorizationRequestRepository.findByAuthState(authState);
  }

  /**
   * The "token endpoint" as described in <a
   * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-3.2">Section 3.2</a> of
   * the OAuth spec.
   * 
   * @param authorization the HTTP Basic auth header.
   * @param formParameters the request parameters
   * @return the response
   */
  @POST
  @Path("/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes("application/x-www-form-urlencoded")
  public Response token(@HeaderParam("Authorization") String authorization, 
          final MultivaluedMap<String, String> formParameters) {
    // Convert incoming parameters into internal form and validate them
    AccessTokenRequest accessTokenRequest = 
            AccessTokenRequest.fromMultiValuedFormParameters(formParameters);
    BasicAuthCredentials credentials = 
        BasicAuthCredentials.createCredentialsFromHeader(authorization);

    ValidationResponse vr = oAuth2Validator.validate(accessTokenRequest, credentials);
    if (!vr.valid()) {
      return sendErrorResponse(vr);
    }
    
    // The request looks valid, attempt to process
    String grantType = accessTokenRequest.getGrantType();
    AuthorizationRequest request;
    try {
      if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
        request = authorizationCodeToken(accessTokenRequest);
      } else if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
        request = refreshTokenToken(accessTokenRequest);
      } else if (GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
        request = clientCredentialToken(accessTokenRequest);
      } else if (GRANT_TYPE_PASSWORD.equals(grantType)) {
        request = passwordToken(accessTokenRequest);
      } else {
        return sendErrorResponse(ValidationResponse.UNSUPPORTED_GRANT_TYPE);
      }
    } catch (ValidationResponseException e) {
      return sendErrorResponse(e.v);
    }
    AccessToken token = createAccessToken(request, false);

    AccessTokenResponse response = new AccessTokenResponse(token.getToken(), BEARER, token.getExpiresIn(), token.getRefreshToken(), StringUtils.join(token.getScopes(), ' '));

    return Response
            .ok()
            .entity(response)
            .cacheControl(cacheControlNoStore())
            .header("Pragma", "no-cache")
            .build();

  }

  private CacheControl cacheControlNoStore() {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoStore(true);
    return cacheControl;
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
  
  private AuthorizationRequest clientCredentialToken(AccessTokenRequest accessTokenRequest) {
    AuthorizationRequest request =  new AuthorizationRequest();
    request.setClient(accessTokenRequest.getClient());
    // We have to construct a AuthenticatedPrincipal on-the-fly as there is only key-secret authentication
    request.setPrincipal(new AuthenticatedPrincipal(request.getClient().getClientId()));
    // Get scopes (either from request or the client's default set)
    request.setGrantedScopes(accessTokenRequest.getScopeList());
    return request;
  }
  
  private AuthorizationRequest passwordToken(AccessTokenRequest accessTokenRequest) {
    // Authenticate the resource owner
    AuthenticatedPrincipal principal = 
        resourceOwnerAuthenticator.authenticate(accessTokenRequest.getUsername(), 
            accessTokenRequest.getPassword());
    if (principal == null) {
      throw new ValidationResponseException(ValidationResponse.INVALID_GRANT_PASSWORD);
    }
    
    AuthorizationRequest request = new AuthorizationRequest();
    request.setClient(accessTokenRequest.getClient());
    request.setPrincipal(principal);
    request.setGrantedScopes(accessTokenRequest.getScopeList());
    return request;
  }


  private Response sendAuthorizationCodeResponse(AuthorizationRequest authReq) {
    String uri = authReq.getRedirectUri();
    String authorizationCode = getAuthorizationCodeValue();
    authReq.setAuthorizationCode(authorizationCode);
    authorizationRequestRepository.save(authReq);
    uri = uri + appendQueryMark(uri) + "code=" + authorizationCode + appendStateParameter(authReq);
    return Response
            .seeOther(UriBuilder.fromUri(uri).build())
            .cacheControl(cacheControlNoStore())
            .header("Pragma", "no-cache")
            .build();
  }

  protected String getTokenValue(boolean isRefreshToken) {
    return UUID.randomUUID().toString();
  }

  protected String getAuthorizationCodeValue() {
    return getTokenValue(false);
  }

  private Response sendErrorResponse(String error, String description, Status status) {
    if (status == Status.UNAUTHORIZED) {
      return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
    }
    return Response.status(status).entity(new ErrorResponse(error, description)).build();
  }

  private Response sendErrorResponse(ValidationResponse response) {
    return sendErrorResponse(response.getValue(), response.getDescription(), response.getStatus());
  }

  private Response sendImplicitGrantResponse(AuthorizationRequest authReq, AccessToken accessToken) {
    String uri = authReq.getRedirectUri();
    String fragment = String.format("access_token=%s&token_type=bearer&expires_in=%s&scope=%s", 
      accessToken.getToken(), accessToken.getExpiresIn(), StringUtils.join(authReq.getGrantedScopes(), ',')) + 
      appendStateParameter(authReq);
    if (authReq.getClient().isIncludePrincipal()) {
      fragment += String.format("&principal=%s", authReq.getPrincipal().getDisplayName()) ;
    }
    return Response
            .seeOther(UriBuilder.fromUri(uri)
            .fragment(fragment).build())
            .cacheControl(cacheControlNoStore())
            .header("Pragma", "no-cache")
            .build();


  }

  private String appendQueryMark(String uri) {
    return uri.contains("?") ? "&" : "?";
  }

  private String appendStateParameter(AuthorizationRequest authReq) {
    String state = authReq.getState();
    try {
      return StringUtils.isBlank(state) ? "" : "&state=".concat(URLEncoder.encode(state, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private Response serverError(String msg) {
    LOG.warn(msg);
    return Response.serverError().build();
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
