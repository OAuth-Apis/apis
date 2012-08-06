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

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ImplicitGrantTestIT extends SeleniumSupport {

  @Test
  public void happy() {
    WebDriver webdriver = getWebDriver();

      String responseType = "token";
      String clientId = "someValidClientId";
      String redirectUri = "http://localhost:8080/fourOhFour";
      String url = buildAuthorizeUrl(responseType, clientId, redirectUri);
      webdriver.get(url);
      assertThat(webdriver.getPageSource(), containsString("fourOhFour"));
  }

  String buildAuthorizeUrl(String responseType, String clientId, String redirectUri) {
    return String.format(
        "%s/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s",
        baseUrl(), responseType, clientId, redirectUri);

  }
}
