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
package org.surfnet.oaaas.web;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.model.AccessTokenResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Entry point for testing the resource server
 * 
 */
@Controller
public class ClientController {

  private static final String AUTHORIZATION = "Authorization";
  private static final String REDIRECT_URI = "http://localhost:8084/redirect";
  private static final String SETTINGS = "settings";
  private static final String br = System.getProperty("line.separator");
  private static final ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);

  private Client client;

  public ClientController() {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonJsonProvider.class);
    this.client = Client.create(config);
  }

  @RequestMapping(value = { "test" }, method = RequestMethod.GET)
  public String socialQueries(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    modelMap.addAttribute(SETTINGS, createDefaultSettings());
    return "oauth-client";
  }

  @RequestMapping(value = "test", method = RequestMethod.POST, params = "step1")
  public String step1(ModelMap modelMap, @ModelAttribute("settings")
  ClientSettings settings, HttpServletRequest request, HttpServletResponse response) throws IOException {
    String responseType = "code";
    settings.setAuthorizationURLComplete(String.format(
        settings.getAuthorizationURL()
            .concat("?response_type=%s&client_id=%s&redirect_uri=%s&scope=read&state=example"), responseType, settings
            .getOauthKey(), REDIRECT_URI));
    settings.setStep("step2");
    modelMap.addAttribute(SETTINGS, settings);
    return "oauth-client";
  }

  @RequestMapping(value = "test", method = RequestMethod.POST, params = "step2")
  public void step2(ModelMap modelMap, @ModelAttribute("settings")
  ClientSettings settings, HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect(settings.getAuthorizationURLComplete());
  }

  @RequestMapping(value = "redirect", method = RequestMethod.GET)
  public String redirect(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
      throws JsonParseException, JsonMappingException, IOException {
    String code = request.getParameter("code");
    ClientSettings settings = createDefaultSettings();

    MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
    formData.add("grant_type", "authorization_code");
    formData.add("code", code);
    formData.add("redirect_uri", REDIRECT_URI);

    String auth = "Basic ".concat(new String(Base64.encodeBase64(settings.getOauthKey().concat(":")
        .concat(settings.getOauthSecret()).getBytes())));
    Builder builder = client.resource(settings.getAccessTokenEndPoint()).header(AUTHORIZATION, auth)
        .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    OutBoundHeaders headers = getHeadersCopy(builder);
    ClientResponse clientResponse = builder.post(ClientResponse.class, formData);
    String json = IOUtils.toString(clientResponse.getEntityInputStream());
    AccessTokenResponse tokenResponse = mapper.readValue(json, AccessTokenResponse.class);
    settings.setStep("step3");
    settings.setAccessToken(tokenResponse.getAccessToken());
    modelMap.put(SETTINGS, settings);
    modelMap.put(
        "requestInfo",
        "Method: POST".concat(br).concat("URL: ").concat(settings.getAccessTokenEndPoint()).concat(br)
            .concat("Headers: ").concat(headers.toString()).concat(br).concat("Body: ").concat(formData.toString()));
    addResponseInfo(modelMap, clientResponse);
    modelMap.put("rawResponseInfo", json);
    return "oauth-client";
  }

  @RequestMapping(value = "test", method = RequestMethod.POST, params = "step3")
  public String step3(ModelMap modelMap, @ModelAttribute("settings")
  ClientSettings settings, HttpServletRequest request, HttpServletResponse response) throws IOException {
    Builder builder = client.resource(settings.getRequestURL())
        .header(AUTHORIZATION, "bearer ".concat(settings.getAccessToken()))
        .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    OutBoundHeaders headers = getHeadersCopy(builder);
    ClientResponse clientResponse = builder.get(ClientResponse.class);
    String json = IOUtils.toString(clientResponse.getEntityInputStream());
    settings.setStep("step3");
    modelMap.put(SETTINGS, settings);
    modelMap.put("requestInfo", "Method: GET".concat(br).concat("URL: ").concat(settings.getRequestURL()).concat(br)
        .concat("Headers: ").concat(headers.toString()));
    addResponseInfo(modelMap, clientResponse);
    modelMap.put("rawResponseInfo", json);
    return "oauth-client";
  }

  
  private void addResponseInfo(ModelMap modelMap, ClientResponse clientResponse) {
    modelMap.put(
        "responseInfo",
        "Status: ".concat(String.valueOf(clientResponse.getStatus()).concat(br).concat("Headers:")
            .concat(clientResponse.getHeaders().toString())));
  }

  /*
   * Nasty trick to be able to print out the headers after the POST is done
   */
  private OutBoundHeaders getHeadersCopy(Builder builder) {
    Field metaData;
    try {
      metaData = PartialRequestBuilder.class.getDeclaredField("metadata");
      metaData.setAccessible(true);
      Object object = metaData.get(builder);
      return new OutBoundHeaders((OutBoundHeaders) object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private ClientSettings createDefaultSettings() {
    return new ClientSettings("http://localhost:8080/oauth2/token", "cool_app_id", "secret",
        "http://localhost:8080/oauth2/authorize", "step1", "http://localhost:8180/v1/api/course");
  }
}
