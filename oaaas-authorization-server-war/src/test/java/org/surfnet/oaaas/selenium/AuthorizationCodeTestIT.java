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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * Integration test (using Selenium) for the Authorization Code flow.
 */
public class AuthorizationCodeTestIT extends SeleniumSupport {
  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeTestIT.class);
  private AuthorizationCodeRequestHandler authorizationCodeRequestHandler;
  private String accessTokenRedirectUri;


  public static class AuthorizationCodeRequestHandler implements HttpRequestHandler {

    public AuthorizationCodeRequestHandler(String redirectUri, String oauthServerBaseUrl) {
      this.redirectUri = redirectUri;
      this.oauthServerBaseUrl = oauthServerBaseUrl;
    }

    private String oauthServerBaseUrl;
    private String redirectUri;

    private String tokenResponse;

    /**
     * Get the token response, wait for it if not set yet.
     */
    public String getTokenResponseBlocking() {
      while (tokenResponse == null) {
      }
      return tokenResponse;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

      final String uri = request.getRequestLine().getUri();
      String authorizationCode = uri.substring(uri.indexOf("code=") + 5);

      final HttpPost tokenRequest = new HttpPost(oauthServerBaseUrl + "/oauth2/token");
      String postBody = String.format("grant_type=authorization_code&code=%s&redirect_uri=%s",
          authorizationCode, redirectUri);

      tokenRequest.setEntity(new ByteArrayEntity(postBody.getBytes()));
      tokenRequest.addHeader("Authorization", authorizationBasic("it-test-client", "somesecret"));
      tokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");

      HttpResponse tokenHttpResponse = new DefaultHttpClient().execute(tokenRequest);
      final InputStream responseContent = tokenHttpResponse.getEntity().getContent();
      String responseAsString = new Scanner(responseContent).useDelimiter("\\A").next();
      responseContent.close();
      tokenResponse = responseAsString;
    }
  }

  @Before
  public void setupOauthClientServer() throws Exception {
    LocalTestServer oauthClientServer = new LocalTestServer(null, null);
    oauthClientServer.start();
    // report how to access the server
    String oauthClientBaseUri = String.format("http://%s:%d",
        oauthClientServer.getServiceAddress().getHostName(), oauthClientServer.getServiceAddress().getPort());
    System.out.println("HTTP server running at " + oauthClientBaseUri);

    accessTokenRedirectUri = oauthClientBaseUri + "/codeCatcher";

    authorizationCodeRequestHandler = new AuthorizationCodeRequestHandler(accessTokenRedirectUri, baseUrl());
    oauthClientServer.register("/codeCatcher", authorizationCodeRequestHandler);
  }


  @Test
  public void authCode() {
    WebDriver webdriver = getWebDriver();

    String responseType = "code";
    String clientId = "it-test-client";

    String url = String.format(
        "%s/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
        baseUrl(), responseType, clientId, accessTokenRedirectUri);
    webdriver.get(url);
    assertThat(webdriver.getPageSource(), containsString("Login with your identifier and password"));

    // Login end user.
    webdriver.findElement(By.id("username")).sendKeys("enduser");
    webdriver.findElement(By.id("password")).sendKeys("enduserpass");
    webdriver.findElement(By.xpath("//form")).submit();

    // consent form
    assertThat(webdriver.getPageSource(), containsString("Yes, grant access"));
    webdriver.findElement(By.id("accept_terms_button")).click();

    // authorization code response
    final String responseUrl = webdriver.getCurrentUrl();
    URI responseURI = URI.create(webdriver.getCurrentUrl());


    // get token response
    String tokenResponse = authorizationCodeRequestHandler.getTokenResponseBlocking();
    LOG.debug("Token response: " + tokenResponse);
    assertThat(tokenResponse, containsString("access_token: "));
    assertThat(tokenResponse, containsString("scope: "));
  }
}