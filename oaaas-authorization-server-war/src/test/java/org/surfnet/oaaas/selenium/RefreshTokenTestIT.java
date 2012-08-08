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

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 * Test refresh token flow
 *
 */
public class RefreshTokenTestIT extends SeleniumSupport {

  private String accessTokenRedirectUri;
  private String clientId = "it-test-client-no-consent-refresh";
  private String secret = "somesecret2";
  

  @Before
  public void setupOauthClientServer() throws Exception {
    accessTokenRedirectUri = startAuthorizationCallbackServer(clientId, secret);
  }

  @Test
  public void refreshCode() {
    WebDriver webdriver = getWebDriver();
    String responseType = "code";
    String url = String.format(
        "%s/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
        baseUrl(), responseType, clientId, accessTokenRedirectUri);
    webdriver.get(url);
    assertThat(webdriver.getPageSource(), containsString("Login with your identifier and password"));

    login(webdriver,false);
    
    // get token response
    String tokenResponse = getAuthorizationCodeRequestHandler().getTokenResponseBlocking();
    
    assertThat(tokenResponse, containsString("access_token"));
    assertThat(tokenResponse, containsString("refresh_token"));
    assertThat(tokenResponse, containsString("token_type"));
    assertThat(tokenResponse, containsString("expires_in"));
    assertThat(tokenResponse, containsString("scope"));
    
    //TODO actually do a token refrsh to get a new one ----
    
  }

}
