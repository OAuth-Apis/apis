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
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
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
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.ResourceServerRepository;

/**
 * JAX-RS Resource for resource servers.
 */
@Named
@Path("/resourceServer")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceServerResource {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceServerResource.class);
  
  @Inject
  private ResourceServerRepository resourceServerRepository;

  /**
   * Get all existing resource servers for the provided credentials (== owner).
   */
  @GET
  public Response getAll(@Context HttpServletRequest request) {
    Response.ResponseBuilder responseBuilder;
    String owner = getUserId(request);
    final List<ResourceServer> resourceServers = resourceServerRepository.findByOwner(owner);

    if (resourceServers == null || !resourceServers.iterator().hasNext()) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(resourceServers);
    }
    return responseBuilder.build();
  }

  /**
   * Get one resource server.
   */
  @GET
  @Path("/{resourceServerId}.json")
  public Response getById(@Context HttpServletRequest request, @PathParam("resourceServerId") Long id) {

    String owner = getUserId(request);

    Response.ResponseBuilder responseBuilder;
    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(id, owner);

    if (resourceServer == null) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(resourceServer);
    }
    return responseBuilder.build();
  }

  /**
   * Save a new resource server.
   */
  @PUT
  public Response put(@Context HttpServletRequest request, @Valid ResourceServer newOne) {
    String owner = getUserId(request);

    newOne.setSecret(UUID.randomUUID().toString());
    newOne.setOwner(owner);
    final ResourceServer resourceServerSaved = resourceServerRepository.save(newOne);
    LOG.debug("nr of entities in store now: {}", resourceServerRepository.count());
    final URI uri = UriBuilder.fromPath("{resourceServerId}.json").build(resourceServerSaved.getId());
    return Response
        .created(uri)
        .entity(resourceServerSaved)
        .build();
  }

  /**
   * Delete an existing resource server.
   */
  @DELETE
  @Path("/{resourceServerId}.json")
  public Response delete(@Context HttpServletRequest request, @PathParam("resourceServerId") Long id) {
    String owner = getUserId(request);

    if (resourceServerRepository.findByIdAndOwner(id, owner) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    resourceServerRepository.delete(id);
    return Response.noContent().build();
  }

  /**
   * Update an existing resource server.
   */
  @POST
  @Path("/{resourceServerId}.json")
  public Response post(@Valid ResourceServer resourceServer,
                       @Context HttpServletRequest request,
                       @PathParam("resourceServerId") Long id) {
    String owner = getUserId(request);

    ResourceServer persistedResourceServer = resourceServerRepository.findByIdAndOwner(id, owner);
    if (persistedResourceServer == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    resourceServer.setSecret(persistedResourceServer.getSecret());
    resourceServer.setOwner(owner);
    ResourceServer savedInstance = resourceServerRepository.save(resourceServer);
    return Response.ok(savedInstance).build();
  }

  private String getUserId(HttpServletRequest request) {
    return ((VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE)).getUser_id();
  }
}
