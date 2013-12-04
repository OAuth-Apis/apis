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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AuthorizationCodeRequestHandler implements HttpRequestHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeRequestHandler.class);

  private String grantType;
  private String clientId;
  private String secret;

  private String oauthServerBaseUrl;
  private String redirectUri;

  private String tokenResponse;
  private String authorizationResponseState;

  public AuthorizationCodeRequestHandler(String redirectUri, String oauthServerBaseUrl, String clientId, String secret,
      String grantType) {
    this.redirectUri = redirectUri;
    this.oauthServerBaseUrl = oauthServerBaseUrl;
    this.clientId = clientId;
    this.secret = secret;
    this.grantType = grantType;
  }

  /**
   * Get the token response, wait for it if not set yet. This causes wonky tests, so we wait a bit before we check the tokenResponse
   */
  public String getTokenResponseBlocking() {
    try {
      Thread.sleep(2500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    while (tokenResponse == null) {
    }
    return tokenResponse;
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
    final String uri = request.getRequestLine().getUri();
    Map<String, String> params = getParamsFromUri(uri);
    String authorizationCode = params.get("code");
    authorizationResponseState = params.get("state");
    LOG.debug("URL: {}, state: {}", uri, authorizationResponseState);

    final HttpPost tokenRequest = new HttpPost(oauthServerBaseUrl + "/oauth2/token");
    String postBody = getPostBody(authorizationCode, grantType);

    tokenRequest.setEntity(new ByteArrayEntity(postBody.getBytes()));
    tokenRequest.addHeader("Authorization", AuthorizationCodeTestIT.authorizationBasic(clientId, secret));
    tokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");

    HttpResponse tokenHttpResponse = new DefaultHttpClient().execute(tokenRequest);
    final InputStream responseContent = tokenHttpResponse.getEntity().getContent();
    String responseAsString = new Scanner(responseContent).useDelimiter("\\A").next();
    responseContent.close();
    tokenResponse = responseAsString;
  }

  private Map<String, String> getParamsFromUri(String uri) {
    String query = URI.create(uri).getRawQuery();
    List<NameValuePair> pairs = URLEncodedUtils.parse(query, Charset.forName("UTF-8"));

    Map<String, String> map = new HashMap<String, String>();
    for (NameValuePair p : pairs) {
      map.put(p.getName(), p.getValue());
    }
    return map;
  }

  private String getPostBody(String authorizationCode, String grantType) {
    String postBody = String.format("grant_type=%s&code=%s&redirect_uri=%s", grantType, authorizationCode, redirectUri);
    return postBody;
  }

  public String getAuthorizationResponseState() {
    return authorizationResponseState;
  }
}