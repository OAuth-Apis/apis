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

import org.apache.http.localserver.LocalTestServer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.it.AbstractAuthorizationServerTest;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 *
 *
 */
public abstract class SeleniumSupport extends AbstractAuthorizationServerTest {

  private static WebDriver driver;

  private AuthorizationCodeRequestHandler authorizationCodeRequestHandler;

  private LocalTestServer oauthClientServer;

  private ObjectMapper mapper = new ObjectMapper();

  @BeforeClass
  public static void initializeOnce() {
    if (driver != null) {
      driver.close();
      driver.quit();
      driver = null;
    }
    if ("firefox".equals(System.getProperty("selenium.webdriver", "firefox"))) {
      initFirefoxDriver();
    } else {
      initHtmlUnitDriver();
    }

  }

  protected String startAuthorizationCallbackServer(String clientId, String secret) throws Exception {
    this.oauthClientServer = new LocalTestServer(null, null);
    oauthClientServer.start();
    // report how to access the server
    String oauthClientBaseUri = String.format("http://%s:%d", oauthClientServer.getServiceAddress().getHostName(),
            oauthClientServer.getServiceAddress().getPort());

    String accessTokenRedirectUri = oauthClientBaseUri + "/codeCatcher";

    authorizationCodeRequestHandler = new AuthorizationCodeRequestHandler(accessTokenRedirectUri, baseUrl(), clientId,
            secret, OAuth2Validator.GRANT_TYPE_AUTHORIZATION_CODE);
    oauthClientServer.register("/codeCatcher", authorizationCodeRequestHandler);
    return accessTokenRedirectUri;
  }

  protected AuthorizationCodeRequestHandler getAuthorizationCodeRequestHandler() {
    return authorizationCodeRequestHandler;
  }

  private static void initHtmlUnitDriver() {
    initDriver(new HtmlUnitDriver());
  }

  private static void initFirefoxDriver() {
    initDriver(new FirefoxDriver());
  }

  private static void initDriver(WebDriver remoteWebDriver) {
    SeleniumSupport.driver = remoteWebDriver;
    SeleniumSupport.driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (driver != null) {
          driver.quit();
        }
      }
    });
  }

  /**
   * @return the webDriver
   */
  protected WebDriver getWebDriver() {
    return driver;
  }

  public void restartBrowserSession() {
    initializeOnce();
  }


  protected void login(WebDriver webdriver, boolean consent) {
    // Login end user.
    webdriver.findElement(By.name("j_username")).sendKeys("enduser");
    webdriver.findElement(By.name("j_password")).sendKeys("enduserpass");
    webdriver.findElement(By.xpath("//form")).submit();
    if (consent) {
      consent(webdriver);
    }
  }

  private void consent(WebDriver webdriver) {
    // consent form
    assertThat(webdriver.getPageSource(), containsString("Grant permission"));
    webdriver.findElement(By.id("user_oauth_approval")).click();
  }

  /**
   * @return the oauthClientServer
   */
  public LocalTestServer getOauthClientServer() {
    return oauthClientServer;
  }

  /**
   * @return the mapper
   */
  public ObjectMapper getMapper() {
    return mapper;
  }

}
