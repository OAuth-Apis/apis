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
package org.surfnet.oaaas.example.api;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.VerifyTokenResponse;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.Client;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

import java.io.IOException;

/**
 * {@link Authenticator} that ask the Authorization Server to check
 * 
 */
public class OAuthAuthenticator implements Authenticator<String, AuthenticatedPrincipal> {

  private String authorizationServerUrl;
  private String authorizationValue;

  private Client client = Client.create();
  private static ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);

  static {
    mapper.disableDefaultTyping();
  }

  /**
   * @param configuration
   */
  public OAuthAuthenticator(UniversityFooConfiguration configuration) {
    AuthConfiguration auth = configuration.getAuth();
    authorizationServerUrl = auth.getAuthorizationServerUrl();
    authorizationValue = "Basic ".concat( new String(Base64.encodeBase64(auth.getKey().concat(":").concat(auth.getSecret()).getBytes())));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.yammer.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
   */
  @Override
  public Optional<AuthenticatedPrincipal> authenticate(String accessToken) throws AuthenticationException {
    String json = client
        .resource(String.format(authorizationServerUrl.concat("?access_token=%s"), accessToken))
        .header(HttpHeaders.AUTHORIZATION, authorizationValue).accept("application/json")
        .get(String.class);
    final VerifyTokenResponse response;
    try {
      response = mapper.readValue(json, VerifyTokenResponse.class);
    } catch (IOException e) {
      throw new AuthenticationException("Could not parse JSON: "+ json, e);
    }
    return Optional.fromNullable(response.getPrincipal());
  }
}
