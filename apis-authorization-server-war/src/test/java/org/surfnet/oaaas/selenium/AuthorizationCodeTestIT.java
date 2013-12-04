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

package org.surfnet.oaaas.selenium;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.surfnet.oaaas.model.AccessTokenResponse;

import java.net.URLEncoder;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * Integration test (using Selenium) for the Authorization Code flow.
 */
public class AuthorizationCodeTestIT extends SeleniumSupport {
  
  private String clientId = "it-test-client";
  private String secret = "somesecret";


  @Test
  public void authCode() throws Exception {
    String accessTokenRedirectUri = startAuthorizationCallbackServer(clientId, secret);

    WebDriver webdriver = getWebDriver();
    String responseType = "code";
    String scopes = "read,write";
    String url = String.format(
        "%s/oauth2/authorize?response_type=%s&scope=%s&client_id=%s&redirect_uri=%s",
        baseUrl(), responseType, scopes, clientId, accessTokenRedirectUri);
    webdriver.get(url);

    login(webdriver,false);
    
    // get token response
    String tokenResponse = getAuthorizationCodeRequestHandler().getTokenResponseBlocking();
    
    AccessTokenResponse accessTokenResponse = getMapper().readValue(tokenResponse, AccessTokenResponse.class);

    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getAccessToken()));
    assertTrue(StringUtils.isBlank(accessTokenResponse.getRefreshToken()));
    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getScope()));
    assertTrue(StringUtils.isNotBlank(accessTokenResponse.getTokenType()));
    assertEquals(accessTokenResponse.getExpiresIn(), 0L);
  }

  @Test
  public void invalidParams() {
    final WebDriver webdriver = getWebDriver();
    webdriver.get(baseUrlWith("/oauth2/authorize"));

    String pageSource = webdriver.getPageSource();
    assertThat(pageSource, containsString("The supported response_type values are 'token' and 'code'"));
  }

  @Test
  public void stateParam() throws Exception {
    String accessTokenRedirectUri = startAuthorizationCallbackServer(clientId, secret);
    WebDriver webdriver = getWebDriver();

    /*
    The RFC says (http://tools.ietf.org/html/rfc6749#appendix-A.5):
           state      = 1*VSCHAR
    Defined in http://tools.ietf.org/html/rfc6749#appendix-A:
         VSCHAR     = %x20-7E

    The variable 'state' below contains all chars in 0x20-0x7E
     */
    String state = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmno0070pqrstuvwxyz{|}~";
    String url = String.format(
            "%s/oauth2/authorize?response_type=%s&scope=%s&client_id=%s&redirect_uri=%s&state=%s",
            baseUrl(), "code", "read,write", clientId,
            URLEncoder.encode(accessTokenRedirectUri, "UTF-8"),
            URLEncoder.encode(state, "UTF-8"));
    webdriver.get(url);

    login(webdriver,false);

    // wait for token response to arrive, therefore block
    getAuthorizationCodeRequestHandler().getTokenResponseBlocking();

    String stateFromResponse = getAuthorizationCodeRequestHandler().getAuthorizationResponseState();

    assertEquals("State from response should be equal to provided state", state, stateFromResponse);
  }
}