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

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.auth.principal.UserPassCredentials;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

import static org.surfnet.oaaas.resource.TokenResource.BASIC_REALM;
import static org.surfnet.oaaas.resource.TokenResource.WWW_AUTHENTICATE;

/**
 * Resource for handling the call from resource servers to validate an access
 * token. As this is not part of the oauth2 <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2">spec</a>, we have taken
 * the Google <a href=
 * "https://developers.google.com/accounts/docs/OAuth2Login#validatingtoken"
 * >specification</a> as basis.
 */
@Named
@Path("/tokeninfo")
@Produces(MediaType.APPLICATION_JSON)
public class VerifyResource implements EnvironmentAware {

  private static final Logger LOG = LoggerFactory.getLogger(VerifyResource.class);

  private static final ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  @Inject
  private ResourceServerRepository resourceServerRepository;

  private boolean jsonTypeInfoIncluded;

  @GET
  public Response verifyToken(@HeaderParam(HttpHeaders.AUTHORIZATION)
                              String authorization, @QueryParam("access_token")
                              String accessToken) throws IOException {

    UserPassCredentials credentials = new UserPassCredentials(authorization);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Incoming verify-token request, access token: {}, credentials from authorization header: {}", accessToken, credentials);
    }

    ResourceServer resourceServer = getResourceServer(credentials);
    if (resourceServer == null || !resourceServer.getSecret().equals(credentials.getPassword())) {
      LOG.warn("For access token {}: Resource server not found for credentials {}. Responding with 401 in VerifyResource#verifyToken.", accessToken, credentials);
      return unauthorized();
    }

    AccessToken token = accessTokenRepository.findByToken(accessToken);
    if (token == null || !resourceServer.containsClient(token.getClient())) {
      LOG.warn("Access token {} not found for resource server '{}'. Responding with 404 in VerifyResource#verifyToken for user {}", accessToken, resourceServer.getName(), credentials);
      return Response.status(Status.NOT_FOUND).entity(new VerifyTokenResponse("not_found")).build();
    }
    if (tokenExpired(token)) {
      LOG.warn("Token {} is expired. Responding with 410 in VerifyResource#verifyToken for user {}", accessToken, credentials);
      return Response.status(Status.GONE).entity(new VerifyTokenResponse("token_expired")).build();
    }

    final VerifyTokenResponse verifyTokenResponse = new VerifyTokenResponse(token.getClient().getName(),
            token.getScopes(), token.getPrincipal(), token.getExpires());

    if (LOG.isDebugEnabled()) {
      LOG.debug("Responding with 200 in VerifyResource#verifyToken for access token {} and user {}", accessToken, credentials);
    }
    return Response.ok(mapper.writeValueAsString(verifyTokenResponse)).build();
  }

  private boolean tokenExpired(AccessToken token) {
    return token.getExpires() != 0 && token.getExpires() < System.currentTimeMillis();
  }

  private ResourceServer getResourceServer(UserPassCredentials credentials) {
    String key = credentials.getUsername();
    return resourceServerRepository.findByKey(key);
  }

  protected Response unauthorized() {
    return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
  }

  /**
   * @param accessTokenRepository the accessTokenRepository to set
   */
  public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
    this.accessTokenRepository = accessTokenRepository;
  }

  /**
   * @param resourceServerRepository the resourceServerRepository to set
   */
  public void setResourceServerRepository(ResourceServerRepository resourceServerRepository) {
    this.resourceServerRepository = resourceServerRepository;
  }

  @Override
  public void setEnvironment(Environment environment) {
    jsonTypeInfoIncluded = Boolean.valueOf(environment.getProperty("adminService.jsonTypeInfoIncluded", "false"));
    if (jsonTypeInfoIncluded) {
      mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    } else {
      mapper.disableDefaultTyping();
    }
  }

  public boolean isJsonTypeInfoIncluded() {
    return jsonTypeInfoIncluded;
  }

}
