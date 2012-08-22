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

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.ExceptionTranslator;
import org.surfnet.oaaas.repository.ResourceServerRepository;

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

    Response.ResponseBuilder responseBuilder;
    String owner = getUserId(request);
    List<AccessToken> tokens = accessTokenRepository.findByResourceOwnerId(owner);

    if (tokens == null || tokens.isEmpty()) {
      LOG.debug("No access tokens found for owner {}", owner);
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      LOG.debug("About to return all access tokens ({}) for owner {}", tokens.size(), owner);
      responseBuilder = Response.ok(tokens);
    }
    return responseBuilder.build();
  }

  /**
   * Get one resource server.
   */
  @GET
  @Path("/{accessTokenId}")
  public Response getById(@Context HttpServletRequest request, @PathParam("accessTokenId") Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    String owner = getUserId(request);

    Response.ResponseBuilder responseBuilder;
    final AccessToken token = accessTokenRepository.findByIdAndResourceOwnerId(id, owner);

    if (token == null) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(token);
    }
    LOG.debug("About to return one accessToken with id {}: {}", id, token);
    return responseBuilder.build();
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

    String owner = getUserId(request);

    if (accessTokenRepository.findByIdAndResourceOwnerId(id, owner) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.debug("About to delete accessToken {}", id);
    accessTokenRepository.delete(id);
    return Response.noContent().build();
  }


}
