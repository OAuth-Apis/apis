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

package org.surfnet.oaaas.it;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Test for Client Credential. Prerequisite is the client 'it-test-client-credential-grant', 'some-secret-client-credential-grant' that may issue client credential grants
 */
public class ClientCredentialGrantTestIT extends AbstractAuthorizationServerTest {

  /*
   * The ObjectMapper from the super class is expecting class meta data as it converts VerifyTokenResponse instances and this is not conform spec for AccessTokenResponses
   */
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void clientCredentialAccessTokenHappy() throws IOException {

    InputStream responseContent = performClientCredentialTokenPost("it-test-client-credential-grant", "some-secret-client-credential-grant");

    String content = IOUtils.toString(responseContent);

    AccessTokenResponse accessTokenResponse =  mapper.readValue(content,AccessTokenResponse.class);
    assertNotNull(accessTokenResponse.getAccessToken());
    assertEquals(0, accessTokenResponse.getExpiresIn());
    assertEquals(OAuth2Validator.BEARER, accessTokenResponse.getTokenType());

    //now check the actual result for an resource server (the one 'owning' the client we used) checking this access token

    final ClientResponse response = client.resource(baseUrlWith("/v1/tokeninfo")).queryParam("access_token", accessTokenResponse.getAccessToken())
            .header("Authorization", authorizationBasic("it-test-resource-server", "somesecret")).get(ClientResponse.class);
    assertEquals(200, response.getStatus());
    String json = response.getEntity(String.class);
    final VerifyTokenResponse verifyTokenResponse = mapper.readValue(json, VerifyTokenResponse.class);

    //The client name equals the principal name as we did not authenticate with the AbstractAuthenticator
    assertEquals("it-test-client-credential-grant", verifyTokenResponse.getPrincipal().getName());
  }

  @Test
  public void clientCredentialAccessTokenWithClientNotAllowed() throws IOException {
    InputStream responseContent = performClientCredentialTokenPost("it-test-client-grant", "somesecret-grant");

    Map response =  mapper.readValue(responseContent,HashMap.class);
    assertEquals("unauthorized_client", response.get("error"));
    assertEquals("The client has no permisssion for client credentials", response.get("error_description"));
  }

  private InputStream performClientCredentialTokenPost(String username, String password) throws IOException {
    String tokenUrl = String.format("%s/oauth2/token", baseUrl());
    final HttpPost tokenRequest = new HttpPost(tokenUrl);
    String postBody = String.format("grant_type=%s", OAuth2Validator.GRANT_TYPE_CLIENT_CREDENTIALS );

    tokenRequest.setEntity(new ByteArrayEntity(postBody.getBytes()));
    tokenRequest.addHeader("Authorization", authorizationBasic(username, password));
    tokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");

    HttpResponse tokenHttpResponse = new DefaultHttpClient().execute(tokenRequest);
    return tokenHttpResponse.getEntity().getContent();
  }


}
