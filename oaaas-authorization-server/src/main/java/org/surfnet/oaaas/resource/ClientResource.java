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
import org.surfnet.oaaas.auth.VerifyTokenResponse;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ClientRepository;
import org.surfnet.oaaas.repository.ResourceServerRepository;

/**
 * JAX-RS Resource for CRUD operations on Clients. (clients in OAuth 2 context).
 */
@Named
@Path("/admin/resourceServer/{resourceServerId}/client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

  private static final Logger LOG = LoggerFactory.getLogger(ClientResource.class);

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

    String owner = getUserId(request);

    Response.ResponseBuilder responseBuilder;
    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(resourceServerId, owner);

    final Iterable<Client> clients = clientRepository.findByResourceServer(resourceServer);

    if (clients == null || !clients.iterator().hasNext()) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(clients);
    }
    return responseBuilder.build();
  }


  /**
   * Get a specific Client.
   */
  @GET
  @Path("/{clientId}.json")
  public Response getById(@Context HttpServletRequest request,
                          @PathParam("resourceServerId") Long resourceServerId,
                          @PathParam("clientId") Long id) {
    Response.ResponseBuilder responseBuilder;

    String owner = getUserId(request);

    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(resourceServerId, owner);
    final Client client = clientRepository.findByIdAndResourceServer(id, resourceServer);

    if (client == null) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(client);
    }
    return responseBuilder.build();
  }

  /**
   * Save a new client.
   */
  @PUT
  public Response put(@Context HttpServletRequest request,
                      @PathParam("resourceServerId") Long resourceServerId,
                      @Valid Client client) {

    String owner = getUserId(request);
    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(resourceServerId, owner);

    client.setResourceServer(resourceServer);

    final Client clientSaved = clientRepository.save(client);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Saved client: {}", clientSaved);
    }
    final URI uri = UriBuilder.fromPath("{clientId}.json").build(clientSaved.getId());
    return Response
        .created(uri)
        .entity(clientSaved)
        .build();
  }

  /**
   * Delete a given client.
   */
  @DELETE
  @Path("/{clientId}.json")
  public Response delete(@Context HttpServletRequest request,
                         @PathParam("clientId") Long id,
                         @PathParam("resourceServerId") Long resourceServerId) {

    String owner = getUserId(request);
    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(resourceServerId, owner);

    Client client = clientRepository.findByIdAndResourceServer(id, resourceServer);
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
  @Path("/{clientId}.json")
  public Response post(@Valid Client newOne, @PathParam("clientId") Long id,
                       @Context HttpServletRequest request,
                       @PathParam("resourceServerId") Long resourceServerId
  ) {

    String owner = getUserId(request);
    final ResourceServer resourceServer = resourceServerRepository.findByIdAndOwner(resourceServerId, owner);

    final Client clientFromStore = clientRepository.findByIdAndResourceServer(id, resourceServer);
    if (clientFromStore == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    clientFromStore.setResourceServer(resourceServer);
    Client savedInstance = clientRepository.save(newOne);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Saving client: {}", savedInstance);
    }
    return Response.ok(savedInstance).build();
  }

  private String getUserId(HttpServletRequest request) {
    return ((VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE)).getUser_id();
  }
}
