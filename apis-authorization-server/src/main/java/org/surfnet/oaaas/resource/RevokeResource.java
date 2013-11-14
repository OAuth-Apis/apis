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
package org.surfnet.oaaas.resource;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.auth.ValidationResponseException;
import org.surfnet.oaaas.auth.principal.ClientCredentials;
import org.surfnet.oaaas.auth.OAuth2Validator.*;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.AccessTokenRequest;
import org.surfnet.oaaas.model.Client;
import org.surfnet.oaaas.model.ErrorResponse;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.AccessTokenRepository;
import org.surfnet.oaaas.repository.ClientRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.surfnet.oaaas.auth.OAuth2Validator.ValidationResponse.UNKNOWN_CLIENT_ID;
import static org.surfnet.oaaas.resource.TokenResource.BASIC_REALM;
import static org.surfnet.oaaas.resource.TokenResource.WWW_AUTHENTICATE;

/**
 * Resource for handling the call from resource servers to validate an access
 * token. As this is not part of the oauth2 <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2">spec</a>, we have taken
 * the Google <a href=
 * "https://developers.google.com/accounts/docs/OAuth2Login#validatingtoken"
 * >specification</a> as basis.
 */
@Named
@Path("/revoke")
@Produces(MediaType.APPLICATION_JSON)
@Consumes("application/x-www-form-urlencoded")
public class RevokeResource implements EnvironmentAware {

  private static final Logger LOG = LoggerFactory.getLogger(VerifyResource.class);

  private static final ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  @Inject
  private ClientRepository clientRepository;

  private boolean jsonTypeInfoIncluded;

  @POST
  public Response revokeAccessToken(@HeaderParam("Authorization")
  String authorization, final MultivaluedMap<String, String> formParameters) {
	String accessToken = null;
    Client client = null;  
	AccessTokenRequest accessTokenRequest = AccessTokenRequest.fromMultiValuedFormParameters(formParameters);
    ClientCredentials credentials = getClientCredentials(authorization, accessTokenRequest);
    try { 
    	client = validateClient(credentials);
    	if (!client.isExactMatch(credentials)) {
            return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
          }
    	List<String> params = formParameters.get("token");
        accessToken = CollectionUtils.isEmpty(params) ? null : params.get(0);
    } catch (ValidationResponseException e) {
    	ValidationResponse validationResponse = e.v;
    	return  Response.status(Status.BAD_REQUEST).entity(new ErrorResponse(validationResponse.getValue(), validationResponse.getDescription())).build();
    }
	AccessToken token = accessTokenRepository.findByTokenAndClient(accessToken, client);
    if (token == null) {
    	LOG.warn("Access token {} not found for client '{}'.", accessToken, client.getClientId());
        return Response.status(Status.NOT_FOUND).entity(new VerifyTokenResponse("not_found")).build();
    }
    accessTokenRepository.delete(token);
    return Response.ok().build();
  }
  
  protected Client validateClient(ClientCredentials credentials) {
	    String clientId = credentials.getClientId();
	    Client client = StringUtils.isBlank(clientId) ? null : clientRepository.findByClientId(clientId);
	    if (client == null) {
	      throw new ValidationResponseException(UNKNOWN_CLIENT_ID);
	    }
	    return client;
	  }

  private ClientCredentials getClientCredentials(String authorization, AccessTokenRequest accessTokenRequest) {
    return StringUtils.isBlank(authorization) ? new ClientCredentials(accessTokenRequest.getClientId(),
        accessTokenRequest.getClientSecret()) : new ClientCredentials(authorization);
  }

  protected Response unauthorized() {
    return Response.status(Status.UNAUTHORIZED).header(WWW_AUTHENTICATE, BASIC_REALM).build();
  }

  public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
    this.accessTokenRepository = accessTokenRepository;
  }



  @Override
  public void setEnvironment(Environment environment) {
    jsonTypeInfoIncluded = Boolean.valueOf(environment.getProperty("adminService.jsonTypeInfoIncluded", "false"));
    if (jsonTypeInfoIncluded) {
      mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    } else {
      mapper.disableDefaultTyping();
    }
  }

  public boolean isJsonTypeInfoIncluded() {
    return jsonTypeInfoIncluded;
  }

}
