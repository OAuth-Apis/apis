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

import java.net.URI;
import java.util.Collections;
import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;
import org.surfnet.oaaas.model.ResourceOwner;
import org.surfnet.oaaas.repository.ResourceOwnerRepository;

/**
 * JAX-RS Resource for resource owners.
 */
@Named
@Path("/resourceOwner")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class ResourceOwnerResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceOwnerResource.class);

  @Inject
  private ResourceOwnerRepository resourceOwnerRepository;

  /**
   * Get all existing resource owners for the provided credentials (== owner) or in case of an adminPrincipal we return all resource servers.
   */
  @GET
  public Response getAll(@Context
                         HttpServletRequest request) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    List<ResourceOwner> resourceOwners = getAllResourceOwners(request);
    return Response.ok(resourceOwners).build();
  }

  /**
   * Get one resource owner.
   */
  @GET
  @Path("/{resourceOwnerId}")
  public Response getById(@Context
                          HttpServletRequest request, @PathParam("resourceOwnerId")
                          Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    return response(getResourceOwner(request, id));
  }

  /**
   * Save a new resource owner.
   */
  @PUT
  public Response put(@Context
                      HttpServletRequest request, @Valid
                      ResourceOwner newOne) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    ResourceOwner resourceOwnerSaved;
    try {
      //we run transactional modus, so any constraint violations only occur after the commit of the transaction (to late...)
      validate(newOne);
      resourceOwnerSaved = resourceOwnerRepository.save(newOne);
    } catch (Exception e) {
      return buildErrorResponse(e);
    }

    LOG.debug("New resourceOwner has been saved: {}. ", resourceOwnerSaved);

    final URI uri = UriBuilder.fromPath("{resourceServerId}.json").build(resourceOwnerSaved.getId());
    return Response.created(uri).entity(resourceOwnerSaved).build();
  }

  /**
   * Delete an existing resource owner.
   */
  @DELETE
  @Path("/{resourceOwnerId}")
  public Response delete(@Context
                         HttpServletRequest request, @PathParam("resourceOwnerId")
                         Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    ResourceOwner resourceOwner = getResourceOwner(request, id);

    if (resourceOwner == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.debug("About to delete resourceServer {}", id);
    resourceOwnerRepository.delete(id);
    return Response.noContent().build();
  }

  /**
   * Update an existing resource server.
   */
  @POST
  @Path("/{resourceOwnerId}")
  public Response post(@Valid
                       final ResourceOwner resourceOwner, @Context
                       HttpServletRequest request, @PathParam("resourceServerId")
                       Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    ResourceOwner persistedResourceOwner = getResourceOwner(request, id);
    if (persistedResourceOwner == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    LOG.debug("About to update existing resourceOwner {} with new properties: {}", persistedResourceOwner,
            resourceOwner);

    ResourceOwner savedInstance;
    try {
      //we run transactional modus, so any constraint violations only occur after the commit of the transaction (to late...)
      validate(resourceOwner);
      savedInstance = resourceOwnerRepository.save(resourceOwner);
    } catch (Exception e) {
      return buildErrorResponse(e);
    }

    return Response.ok(savedInstance).build();
  }

  private ResourceOwner getResourceOwner(HttpServletRequest request, Long id) {
    ResourceOwner resourceOwner;
    if (isAdminPrincipal(request)) {
      resourceOwner = resourceOwnerRepository.findOne(id);
    } else {
      String owner = getUserId(request);
      resourceOwner = resourceOwnerRepository.findByUsername(owner);
      if (resourceOwner != null && resourceOwner.getId() != id) {
        resourceOwner = null;
      }
    }
    LOG.debug("About to return one resourceServer with id {}: {}", id, resourceOwner);
    return resourceOwner;
  }

  private List<ResourceOwner> getAllResourceOwners(HttpServletRequest request) {
    List<ResourceOwner> resourceOwners;
    if (isAdminPrincipal(request)) {
      resourceOwners = addAll(resourceOwnerRepository.findAll().iterator());
      LOG.debug("About to return all resource owners ({}) for adminPrincipal", resourceOwners.size());
    } else {
      String owner = getUserId(request);
      ResourceOwner resourceOwner = resourceOwnerRepository.findByUsername(owner);
      if (resourceOwner == null) {
        resourceOwners = Collections.emptyList();
      } else {
        resourceOwners = Collections.singletonList(resourceOwner);
      }
      LOG.debug("About to return all resource owners ({}) for owner {}", resourceOwners.size(), owner);
    }
    return resourceOwners;
  }


}
