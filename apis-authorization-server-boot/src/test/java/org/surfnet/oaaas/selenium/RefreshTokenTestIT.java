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
package org.surfnet.oaaas.selenium;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.model.AccessTokenResponse;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Test refresh token flow
 */
public class RefreshTokenTestIT extends SeleniumSupport {

  private String clientId = "it-test-client-no-consent-refresh";
  private String secret = "somesecret2";

  @Test
  public void refreshCode() throws Exception {
    /*
     * First do a normal authorization and obtain a AccessToken (with refreshToken) 
     */
    String accessTokenRedirectUri = startAuthorizationCallbackServer(clientId, secret);

    restartBrowserSession();
    WebDriver webdriver = getWebDriver();

    String responseType = "code";
    String url = String.format("%s/oauth2/authorize?response_type=%s&client_id=%s&scope=read&redirect_uri=%s",
            baseUrl(), responseType, clientId, accessTokenRedirectUri);
    webdriver.get(url);

    /*
     * Consent is not necessary for this Client
     */
    login(webdriver, false);

    // get token response
    System.out.println("Getting response");
    String tokenResponse = getAuthorizationCodeRequestHandler().getTokenResponseBlocking();
    System.out.println("Got response");

    AccessTokenResponse accessTokenResponse = getMapper().readValue(tokenResponse, AccessTokenResponse.class);

    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getAccessToken()));
    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getRefreshToken()));
    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getScope()));
    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getTokenType()));
    assertTrue(accessTokenResponse.getExpiresIn() > 0);

    String tokenUrl = String.format("%s/oauth2/token", baseUrl());

    final HttpPost tokenRequest = new HttpPost(tokenUrl);
    
    /*
     * Now make a request for a new AccessToken based on the refreshToken
     */
    String postBody = String.format("grant_type=%s&refresh_token=%s&state=%s",
            OAuth2Validator.GRANT_TYPE_REFRESH_TOKEN, accessTokenResponse.getRefreshToken(), "dummy");

    tokenRequest.setEntity(new ByteArrayEntity(postBody.getBytes()));
    tokenRequest.addHeader("Authorization", AuthorizationCodeTestIT.authorizationBasic(clientId, secret));

    tokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");

    HttpResponse tokenHttpResponse = new DefaultHttpClient().execute(tokenRequest);
    final InputStream responseContent = tokenHttpResponse.getEntity().getContent();
    String responseAsString = IOUtils.toString(responseContent);

    AccessTokenResponse refreshTokenResponse = getMapper().readValue(responseAsString, AccessTokenResponse.class);

    assertTrue(StringUtils.isNotBlank(refreshTokenResponse.getAccessToken()));
    assertTrue(StringUtils.isNotBlank(refreshTokenResponse.getRefreshToken()));
    assertTrue(StringUtils.isNotBlank(refreshTokenResponse.getScope()));
    assertTrue(StringUtils.isNotBlank(refreshTokenResponse.getTokenType()));
    assertTrue(accessTokenResponse.getExpiresIn() > 0);

    assertNotSame(refreshTokenResponse.getAccessToken(), accessTokenResponse.getAccessToken());
    assertNotSame(refreshTokenResponse.getRefreshToken(), accessTokenResponse.getRefreshToken());
    assertEquals(refreshTokenResponse.getScope(), accessTokenResponse.getScope());
    assertEquals(refreshTokenResponse.getExpiresIn(), accessTokenResponse.getExpiresIn());

  }

}
