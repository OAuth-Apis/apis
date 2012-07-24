/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;

import com.yammer.dropwizard.views.View;

/**
 * An {@link AuthenticationHandler} that simply shows a form to login and
 * accepts all
 * 
 */
@Component
@Produces(MediaType.TEXT_HTML)
@Path("/doAuthorize")
public class SimpleAuthenticationHandler implements AuthenticationHandler {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.surfnet.oaaas.auth.AuthenticationHandler#handle(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * java.lang.String)
   */
  @Override
  public Response handle(HttpServletRequest request, HttpServletResponse response, String forwardUri, String csrfValue) {
    String baseUrl = getBaseUrl(request);
    try {
      return Response.seeOther(
          new URI("doAuthorize?forwardUri=".concat(forwardUri).concat("&csrfValue=").concat(csrfValue))).build();
    } catch (URISyntaxException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  public View login(@QueryParam("forwardUri")
  String forwardUri,@QueryParam("csrfValue") String csrfValue) {
    return new LoginView(forwardUri,csrfValue);
  }

  @POST
  public void authorize(@FormParam("username")
  final String username, @FormParam("password")
  String password, @FormParam("forwardUri")
  String forwardUri,@FormParam("csrfValue")
  String csrfValue , @Context
  HttpServletRequest request, @Context
  HttpServletResponse response) {
    try {
      request.setAttribute(PRINCIPAL, new Principal() {
        @Override
        public String getName() {
          return username;
        }
      });
      request.getRequestDispatcher(forwardUri.concat("?").concat(username)).forward(request, response);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getBaseUrl(HttpServletRequest request) {
    return request.getRequestURL().toString().replace(request.getRequestURI().substring(1), request.getContextPath());
  }

}
