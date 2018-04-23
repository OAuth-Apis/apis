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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
@Path("/accessTokenForOwner")
@Produces(MediaType.APPLICATION_JSON)
public class AccessTokenForOwnerResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(AccessTokenForOwnerResource.class);

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
   * Get all tokens for a user.
   */
  @GET
  @Path("/{accessTokenOwner}")
  public Response getByOwner(@Context HttpServletRequest request, @PathParam("accessTokenOwner") String owner) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    List<AccessToken> tokens = getAccessTokensForOwner(request, owner);
    return Response.ok(tokens).build();
  }

  /**
   * Delete all existing access tokens for a user.
   */
  @DELETE
  @Path("/{accessTokenOwner}")
  public Response delete(@Context HttpServletRequest request, @PathParam("accessTokenOwner") String owner) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    List<AccessToken> tokens = getAccessTokensForOwner(request, owner);
    if (tokens == null || tokens.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
	LOG.debug("About to delete accessTokens {}", Arrays.toString(tokens.toArray()));
	accessTokenRepository.delete(tokens);
    return Response.noContent().build();
  }

  @GET
  @Path("/{accessTokenOwner}")
  public Response getByOwnerEncrypted(@Context HttpServletRequest request, @PathParam("accessTokenOwner") String owner) {
      return getByOwner(request, decode(owner));
  }

  @DELETE
  @Path("/{accessTokenOwner}")
  public Response deleteEncrypted(@Context HttpServletRequest request, @PathParam("accessTokenOwner") String owner) {
      return delete(request, decode(owner));
  }
  
  private String decode(String owner) {
      try {
          owner = URLDecoder.decode(owner, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
          LOG.error(String.format("Error while decoding '%s'", owner), e);
      }
      return owner;
}

  private List<AccessToken> getAccessTokensForOwner(HttpServletRequest request, String owner) {
    List<AccessToken> accessTokens;
    String userName = getUserId(request);
    if (isAdminPrincipal(request) || owner.equals(userName )) {
        accessTokens = accessTokenRepository.findByResourceOwnerId(owner);
        LOG.debug("About to return all resource servers ({}) for owner {}", accessTokens.size(), owner);
    } else {
        accessTokens = new ArrayList<>();
        LOG.debug("User {} is neither admin nor owner. Returning empty list", userName);
    }
    return accessTokens;
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
