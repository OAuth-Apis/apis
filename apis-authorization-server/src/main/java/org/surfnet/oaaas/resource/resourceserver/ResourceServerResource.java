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

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.model.StatisticsResponse;
import org.surfnet.oaaas.model.StatisticsResponse.ClientStat;
import org.surfnet.oaaas.model.StatisticsResponse.ResourceServerStat;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.*;

import static org.apache.commons.collections.CollectionUtils.subtract;

/**
 * JAX-RS Resource for resource servers.
 */
@Named
@Path("/resourceServer")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class ResourceServerResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceServerResource.class);

  @Inject
  private ResourceServerRepository resourceServerRepository;

  @Inject
  private AccessTokenRepository accessTokenRepository;

  @Inject
  private ClientRepository clientRepository;

  /**
   * Get all existing resource servers for the provided credentials (== owner) or in case of an adminPrincipal we return all resource servers.
   */
  @GET
  public Response getAll(@Context
                         HttpServletRequest request) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    List<ResourceServer> resourceServers = getAllResourceServers(request);
    return Response.ok(resourceServers).build();
  }

  /**
   * Get one resource server.
   */
  @GET
  @Path("/{resourceServerId}")
  public Response getById(@Context
                          HttpServletRequest request, @PathParam("resourceServerId")
                          Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    return response(getResourceServer(request, id));
  }

  /**
   * Get statistics
   */
  @GET
  @Path("/stats")
  public Response stats(@Context
                        HttpServletRequest request) {
    Iterable<ResourceServer> resourceServers = this.getAllResourceServers(request);
    List<ResourceServerStat> resourceServerStats = new ArrayList<StatisticsResponse.ResourceServerStat>();
    for (ResourceServer resourceServer : resourceServers) {
      List<ClientStat> clientStats = new ArrayList<StatisticsResponse.ClientStat>();
      Set<Client> clients = resourceServer.getClients();
      for (Client client : clients) {
        clientStats.add(new StatisticsResponse.ClientStat(client.getName(), client.getDescription(),
                accessTokenRepository.countByUniqueResourceOwnerIdAndClientId(client.getId())));
      }
      resourceServerStats.add(new StatisticsResponse.ResourceServerStat(resourceServer.getName(), resourceServer
              .getDescription(), clientStats));
    }
    return Response.ok(new StatisticsResponse(resourceServerStats)).build();
  }

  /**
   * Get the principal
   */
  @GET
  @Path("/principal")
  public Response principal(@Context
                        HttpServletRequest request) {
    VerifyTokenResponse verifyTokenResponse = (VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE);
    return Response.ok(verifyTokenResponse.getPrincipal()).build();
  }

  /**
   * Save a new resource server.
   */
  @PUT
  public Response put(@Context
                      HttpServletRequest request, @Valid
                      ResourceServer newOne) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    String owner = getUserId(request);

    // Read only fields
    newOne.setKey(generateKey());
    newOne.setSecret(generateSecret());
    newOne.setOwner(owner);

    ResourceServer resourceServerSaved;
    try {
      //we run transactional modus, so any constraint violations only occur after the commit of the transaction (to late...)
      validate(newOne);
      resourceServerSaved = resourceServerRepository.save(newOne);
    } catch (Exception e) {
      return buildErrorResponse(e);
    }

    LOG.debug("New resourceServer has been saved: {}. ", resourceServerSaved);

    final URI uri = UriBuilder.fromPath("{resourceServerId}.json").build(resourceServerSaved.getId());
    return Response.created(uri).entity(resourceServerSaved).build();
  }

  /**
   * Delete an existing resource server.
   */
  @DELETE
  @Path("/{resourceServerId}")
  public Response delete(@Context
                         HttpServletRequest request, @PathParam("resourceServerId")
                         Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    ResourceServer resourceServer = getResourceServer(request, id);

    if (resourceServer == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.debug("About to delete resourceServer {}", id);
    resourceServerRepository.delete(id);
    return Response.noContent().build();
  }

  /**
   * Update an existing resource server.
   */
  @POST
  @Path("/{resourceServerId}")
  public Response post(@Valid
                       final ResourceServer resourceServer, @Context
                       HttpServletRequest request, @PathParam("resourceServerId")
                       Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    ResourceServer persistedResourceServer = getResourceServer(request, id);
    if (persistedResourceServer == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Copy over read-only fields
    resourceServer.setSecret(persistedResourceServer.getSecret());
    resourceServer.setKey(persistedResourceServer.getKey());
    resourceServer.setOwner(getUserId(request));

    pruneClientScopes(resourceServer.getScopes(), persistedResourceServer.getScopes(),
            persistedResourceServer.getClients());
    LOG.debug("About to update existing resourceServer {} with new properties: {}", persistedResourceServer,
            resourceServer);

    ResourceServer savedInstance;
    try {
      //we run transactional modus, so any constraint violations only occur after the commit of the transaction (to late...)
      validate(resourceServer);
      savedInstance = resourceServerRepository.save(resourceServer);
    } catch (Exception e) {
      return buildErrorResponse(e);
    }

    return Response.ok(savedInstance).build();
  }

  /**
   * Delete all scopes from clients that are not valid anymore with the new
   * resource server
   *
   * @param newScopes the newly saved scopes
   * @param oldScopes the scopes from the existing resource server
   * @param clients   the clients of the resource server
   */
  @SuppressWarnings("unchecked")
  protected void pruneClientScopes(final List<String> newScopes, List<String> oldScopes, Set<Client> clients) {
    if (!newScopes.containsAll(oldScopes)) {
      subtract(oldScopes, newScopes);
      Collection<String> outdatedScopes = subtract(oldScopes, newScopes);
      LOG.info("Resource server has updated scopes. Will remove all outdated scopes from clients: {}", outdatedScopes);

      for (Client c : clients) {
        final List<String> clientScopes = c.getScopes();
        if (CollectionUtils.containsAny(clientScopes, outdatedScopes)) {
          ArrayList<String> prunedScopes = new ArrayList<String>(subtract(clientScopes, outdatedScopes));
          LOG.info("Client scopes of client {} were: {}. After pruning (because resourceServer has new scopes): {}",
                  new Object[]{c.getClientId(), c.getScopes(), prunedScopes});
          c.setScopes(prunedScopes);
        }
      }
    }
  }

  protected String generateKey() {
    return super.generateRandom();
  }

  protected String generateSecret() {
    return super.generateRandom();
  }

  private ResourceServer getResourceServer(HttpServletRequest request, Long id) {
    ResourceServer resourceServer;
    if (isAdminPrincipal(request)) {
      resourceServer = resourceServerRepository.findOne(id);
    } else {
      String owner = getUserId(request);
      resourceServer = resourceServerRepository.findByIdAndOwner(id, owner);
    }
    LOG.debug("About to return one resourceServer with id {}: {}", id, resourceServer);
    return resourceServer;
  }

  private List<ResourceServer> getAllResourceServers(HttpServletRequest request) {
    List<ResourceServer> resourceServers;
    if (isAdminPrincipal(request)) {
      resourceServers = addAll(resourceServerRepository.findAll().iterator());
      LOG.debug("About to return all resource servers ({}) for adminPrincipal", resourceServers.size());
    } else {
      String owner = getUserId(request);
      resourceServers = resourceServerRepository.findByOwner(owner);
      LOG.debug("About to return all resource servers ({}) for owner {}", resourceServers.size(), owner);
    }
    return resourceServers;
  }


}
