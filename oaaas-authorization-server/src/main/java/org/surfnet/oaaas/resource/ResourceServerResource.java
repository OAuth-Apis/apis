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
import java.util.UUID;

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
import org.surfnet.oaaas.model.ResourceServer;
import org.surfnet.oaaas.repository.ResourceServerRepository;

@Named
@Path("/admin/resourceServer")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceServerResource {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceServerResource.class);
  
  @Inject
  private ResourceServerRepository resourceServerRepository;

  @GET
  @Timed
  public Response getAll() {
    Response.ResponseBuilder responseBuilder;
    final Iterable<ResourceServer> resourceServers = resourceServerRepository.findAll();

    if (resourceServers == null || !resourceServers.iterator().hasNext()) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(resourceServers);
    }
    return responseBuilder.build();
  }


  @GET
  @Timed
  @Path("/{resourceServerId}.json")
  public Response getById(@PathParam("resourceServerId") Long id) {
    Response.ResponseBuilder responseBuilder;
    final ResourceServer resourceServer = resourceServerRepository.findOne(id);

    if (resourceServer == null) {
      responseBuilder = Response.status(Response.Status.NOT_FOUND);
    } else {
      responseBuilder = Response.ok(resourceServer);
    }
    return responseBuilder.build();
  }

  @PUT
  @Timed
  public Response put(@Valid ResourceServer newOne) {
    newOne.setSecret(UUID.randomUUID().toString());
    final ResourceServer resourceServerSaved = resourceServerRepository.save(newOne);
    LOG.debug("nr of entities in store now: {}", resourceServerRepository.count());
    final URI uri = UriBuilder.fromPath("{resourceServerId}.json").build(resourceServerSaved.getId());
    return Response
        .created(uri)
        .entity(resourceServerSaved)
        .build();
  }

  @DELETE
  @Timed
  @Path("/{resourceServerId}.json")
  public Response delete(@PathParam("resourceServerId") Long id) {
    if (resourceServerRepository.findOne(id) == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    resourceServerRepository.delete(id);
    return Response.noContent().build();
  }

  @POST
  @Timed
  @Path("/{resourceServerId}.json")
  public Response post(@Valid ResourceServer resourceServer, @PathParam("resourceServerId") Long id) {
    ResourceServer findOne = resourceServerRepository.findOne(id);
    if (findOne == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    resourceServer.setSecret(findOne.getSecret());
    ResourceServer savedInstance = resourceServerRepository.save(resourceServer);
    return Response.ok(savedInstance).build();
  }
}
