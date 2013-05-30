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

package org.surfnet.oaaas.resource.resourceserver;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.repository.AccessTokenRepository;

/**
 * JAX-RS Resource for maintaining owns access tokens.
 */
@Named
@Path("/accessToken")
@Produces(MediaType.APPLICATION_JSON)
public class AccessTokenResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(AccessTokenResource.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  /**
   * Get all access token for the provided credentials (== owner).
   */
  @GET
  public Response getAll(@Context HttpServletRequest request) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    List<AccessToken> tokens = getAllAccessTokens(request);
    return Response.ok(tokens).build();
  }

  /**
   * Get one token.
   */
  @GET
  @Path("/{accessTokenId}")
  public Response getById(@Context HttpServletRequest request, @PathParam("accessTokenId") Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    return response(getAccessToken(request, id));
  }


  /**
   * Delete an existing access token.
   */
  @DELETE
  @Path("/{accessTokenId}")
  public Response delete(@Context HttpServletRequest request, @PathParam("accessTokenId") Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    AccessToken accessToken = getAccessToken(request, id);
    if (accessToken == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.debug("About to delete accessToken {}", id);
    accessTokenRepository.delete(id);
    return Response.noContent().build();
  }

  private AccessToken getAccessToken(HttpServletRequest request, Long id) {
    AccessToken accessToken;
    if (isAdminPrincipal(request)) {
      accessToken = accessTokenRepository.findOne(id);
    } else {
      String owner = getUserId(request);
      accessToken = accessTokenRepository.findByIdAndResourceOwnerId(id, owner);
    }
    LOG.debug("About to return one accessToken with id {}: {}", id, accessToken);
    return accessToken;
  }

  private List<AccessToken> getAllAccessTokens(HttpServletRequest request) {
    List<AccessToken> accessTokens;
    if (isAdminPrincipal(request)) {
      accessTokens = addAll(accessTokenRepository.findAll().iterator());
      LOG.debug("About to return all resource servers ({}) for adminPrincipal", accessTokens.size());
    } else {
      String owner = getUserId(request);
      accessTokens = accessTokenRepository.findByResourceOwnerId(owner);
      LOG.debug("About to return all resource servers ({}) for owner {}", accessTokens.size(), owner);
    }
    return accessTokens;
  }


}
