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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
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
import java.util.Collections;

/**
 * JAX-RS Resource for CRUD operations on Clients. (clients in OAuth 2 context).
 */
@Named
@Path("/resourceServer/{resourceServerId}/client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource extends AbstractResource {

  private static final Logger LOG = LoggerFactory.getLogger(ClientResource.class);
  private static final String FILTERED_CLIENT_ID_CHARS = "[^a-z0-9_\\x2D]";

  @Inject
  private ClientRepository clientRepository;

  @Inject
  private ResourceServerRepository resourceServerRepository;

  /**
   * Get a list of all clients linked to the given resourceServer.
   */
  @GET
  public Response getAll(@Context HttpServletRequest request,
                         @PathParam("resourceServerId") Long resourceServerId) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    ResourceServer resourceServer = getResourceServer(request, resourceServerId);
    Iterable<Client> clients = clientRepository.findByResourceServer(resourceServer);
    return response(addAll(clients.iterator()));
  }


  /**
   * Get a specific Client.
   */
  @GET
  @Path("/{clientId}")
  public Response getById(@Context HttpServletRequest request,
                          @PathParam("resourceServerId") Long resourceServerId,
                          @PathParam("clientId") Long id) {
    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_READ));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    Client client = getClientByResourceServer(request, id, resourceServerId);
    return response(client);
  }

  /**
   * Save a new client.
   */
  @PUT
  public Response put(@Context HttpServletRequest request,
                      @PathParam("resourceServerId") Long resourceServerId, Client client) {

    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }
    ResourceServer resourceServer = getResourceServer(request, resourceServerId);

    client.setResourceServer(resourceServer);
    client.setClientId(generateClientId(client));
    client.setSecret(client.isAllowedImplicitGrant() ? null : generateSecret());

    Client clientSaved;

    try {
      clientSaved = clientRepository.save(client);
    } catch (RuntimeException e) {
      return buildErrorResponse(e);
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Saved client: {}", clientSaved);
    }
    final URI uri = UriBuilder.fromPath("{clientId}.json").build(clientSaved.getId());
    return Response.created(uri).entity(clientSaved).build();
  }

  protected String generateSecret() {
    return super.generateRandom();
  }

  /**
   * Delete a given client.
   */
  @DELETE
  @Path("/{clientId}")
  public Response delete(@Context HttpServletRequest request,
                         @PathParam("clientId") Long id,
                         @PathParam("resourceServerId") Long resourceServerId) {

    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    Client client = getClientByResourceServer(request, id, resourceServerId);

    if (client == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Deleting client: {}", client);
    }
    clientRepository.delete(id);
    return Response.noContent().build();
  }

  /**
   * Update an existing client.
   */
  @POST
  @Path("/{clientId}")
  public Response post(@Valid Client newOne, @PathParam("clientId") Long id,
                       @Context HttpServletRequest request,
                       @PathParam("resourceServerId") Long resourceServerId
  ) {

    Response validateScopeResponse = validateScope(request, Collections.singletonList(AbstractResource.SCOPE_WRITE));
    if (validateScopeResponse != null) {
      return validateScopeResponse;
    }

    ResourceServer resourceServer = getResourceServer(request, resourceServerId);

    final Client clientFromStore = clientRepository.findByIdAndResourceServer(id, resourceServer);
    if (clientFromStore == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Copy over read-only fields
    newOne.setResourceServer(resourceServer);
    newOne.setClientId(clientFromStore.getClientId());
    newOne.setSecret(newOne.isAllowedImplicitGrant() ? null : clientFromStore.getSecret());

    Client savedInstance;
    try {
      savedInstance = clientRepository.save(newOne);
    } catch (RuntimeException e) {
      return buildErrorResponse(e);
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Saving client: {}", savedInstance);
    }
    return Response.ok(savedInstance).build();
  }

  /**
   * Method that generates a unique client id, taking into account existing clientIds in the backend.
   *
   * @param client the client for whom to generate an id.
   * @return the generated id. Callers are responsible themselves for actually calling {@link Client#setClientId(String)}
   */
  protected String generateClientId(Client client) {
    String clientId = sanitizeClientName(client.getName());
    if (clientRepository.findByClientId(clientId) != null) {

      String baseClientId = clientId;

      /* if one with such name exists already, the next one would actually be number 2. Therefore,
       * start counting with 2.
       */
      int i = 2;
      do {
        clientId = baseClientId + (i++);
      } while (clientRepository.findByClientId(clientId) != null);
    }
    return clientId;
  }

  protected String sanitizeClientName(String name) {
    return name.toLowerCase().replaceAll(" ", "-").replaceAll(FILTERED_CLIENT_ID_CHARS, "");
  }

  private Client getClientByResourceServer(HttpServletRequest request, Long clientId, Long resourceServerId) {
    ResourceServer resourceServer = getResourceServer(request, resourceServerId);
    return clientRepository.findByIdAndResourceServer(clientId, resourceServer);
  }

  private ResourceServer getResourceServer(HttpServletRequest request, Long id) {
    ResourceServer resourceServer;
    if (isAdminPrincipal(request)) {
      resourceServer = resourceServerRepository.findOne(id);
    } else {
      String owner = getUserId(request);
      resourceServer = resourceServerRepository.findByIdAndOwner(id, owner);
    }
    return resourceServer;
  }

}
