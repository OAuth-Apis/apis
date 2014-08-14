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

import java.net.URI;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * Integration test (using Selenium) for the Implicit Grant flow.
 */
public class ImplicitGrantTestIT extends SeleniumSupport {

  @Test
  public void implicitGrant() {
    performImplicitGrant(true);
    /*
     * The second time no consent is required (as we have already an access token for the client/ principal name
     */
    restartBrowserSession();
    performImplicitGrant(false);
  }

  private void performImplicitGrant(boolean needConsent) {

    WebDriver webdriver = getWebDriver();

    String responseType = "token";
    String clientId = "it-test-client-grant";
    String redirectUri = "http://localhost:8080/fourOhFour";

    String url = String.format(
        "%s/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
        baseUrl(), responseType, clientId, redirectUri);
    webdriver.get(url);

    login(webdriver, needConsent);

    // Token response
    URI responseURI = URI.create(webdriver.getCurrentUrl());

    assertThat(responseURI.getFragment(), containsString("access_token="));
    assertThat(responseURI.getPath(), equalTo("/fourOhFour"));
    assertThat(responseURI.getHost(), equalTo("localhost"));
  }
}