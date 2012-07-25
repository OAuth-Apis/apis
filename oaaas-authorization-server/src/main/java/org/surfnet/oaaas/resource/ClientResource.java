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
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.yammer.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.repository.ClientRepository;

@Named
@Path("/admin/client")
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

  private static final Logger LOG = LoggerFactory.getLogger(ClientResource.class);

  @Inject
  private ClientRepository clientRepository;


  @GET
  @Timed
  public Response getAll() {
    Response.ResponseBuilder responseBuilder;
    final Iterable<Client> clients = clientRepository.findAll();

    if (clients == null || !clients.iterator().hasNext()) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(clients);
    }
    return responseBuilder.build();
  }


  @GET
  @Timed
  @Path("/{clientId}.json")
  public Response getById(@PathParam("clientId") Long id) {
    Response.ResponseBuilder responseBuilder;
    final Client client = clientRepository.findOne(id);

    if (client == null) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(client);
    }
    return responseBuilder.build();
  }

  @PUT
  @Timed
  public Response put(@Valid Client client) {
    final Client clientSaved = clientRepository.save(client);
    LOG.debug("nr of entities in store now: {}", clientRepository.count());
    final URI uri = UriBuilder.fromPath("{clientId}.json").build(clientSaved.getId());
    return Response
        .created(uri)
        .entity(clientSaved)
        .build();
  }

  @DELETE
  @Timed
  @Path("/{clientId}.json")
  public Response delete(@PathParam("clientId") Long id) {
    if (clientRepository.findOne(id) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    clientRepository.delete(id);
    return Response.noContent().build();
  }

  @POST
  @Timed
  @Path("/{clientId}.json")
  public Response post(@Valid Client newOne, @PathParam("clientId") Long id) {
    if (clientRepository.findOne(id) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    Client savedInstance = clientRepository.save(newOne);
    return Response.ok(savedInstance).build();
  }
}
