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
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.surfnet.oaaas.model.AccessTokenResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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

    login(webdriver,true);
    
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
}