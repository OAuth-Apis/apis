package org.surfnet.oaaas.conext.mock;
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

import nl.surfnet.coin.api.client.OAuthVersion;
import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * OpenConextOAuthClientMock.java
 */
public class OpenConextOAuthClientMock implements OpenConextOAuthClient {

  private String endpointBaseUrl;

  @Override
  public boolean isAccessTokenGranted(String userId) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public String getAuthorizationUrl() {
    return endpointBaseUrl;
  }

  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
  }

  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    if (userId.equalsIgnoreCase("admin")) {
      return asList(new Group20("urn:collab:group:dev.surfteams.nl:nl:surfnet:diensten:admin_apis"));
    }
    return new ArrayList<Group20>();
  }

  @Override
  public Group20 getGroup20(String userId, String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * The following is needed to be conform the contract of the real
   * OpenConextOAuthClient. For the same reason we get the values our selves
   * from the properties files, as we can't inject them
   */

  public void setCallbackUrl(String url) {
  }

  public void setConsumerSecret(String secret) {
  }

  public void setConsumerKey(String key) {
  }

  public void setEndpointBaseUrl(String url) {
    endpointBaseUrl = url;
  }

  public void setVersion(OAuthVersion v) {
  }


}
