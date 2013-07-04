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
package org.surfnet.oaaas.auth;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.ws.rs.core.HttpHeaders;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.VerifyTokenResponse;

import nl.surfnet.coin.mock.AbstractMockHttpServerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link Test} that uses a dummy http server to org.surfnet.oaaas.conext.mock return values from the
 * authorization server.
 * 
 */
public class AuthorizationServerFilterTest extends AbstractMockHttpServerTest {

  private static final ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);
 
  private AuthorizationServerFilter filter;

  @Before
  public void before() throws ServletException {
    MockFilterConfig filterConfig = new MockFilterConfig();
    filterConfig.addInitParameter("resource-server-key", "org.surfnet.oaaas.conext.mock-server-name");
    filterConfig.addInitParameter("resource-server-secret", UUID.randomUUID().toString());
    filterConfig.addInitParameter("authorization-server-url", "http://localhost:8088/org.surfnet.oaaas.conext.mock/endpoint");
    filterConfig.addInitParameter("type-information-is-included", "true");
    //enable caching as we want to test this
    filter = new AuthorizationServerFilter(){
      @Override
      protected boolean cacheAccessTokens() {
        return true;
      }
    };
    filter.init(filterConfig);
  }

  /**
   * Test method for
   * {@link org.surfnet.oaaas.auth.AuthorizationServerFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
   * .
   * 
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testDoFilterHappyFlow() throws IOException, ServletException {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("key", "value");
    VerifyTokenResponse recorderdResponse = new VerifyTokenResponse("org.surfnet.oaaas.conext.mock-client", Collections.singletonList("read"),
        new AuthenticatedPrincipal("john.doe", Arrays.asList("user", "admin"), attributes), 0L);
    MockFilterChain chain = doCallFilter(recorderdResponse);
    /*
     * Verify that the FilterChain#doFilter is called and the
     * VerifyTokenResponse is set on the Request
     */
    VerifyTokenResponse response = (VerifyTokenResponse) chain.getRequest().getAttribute(
        AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE);
    assertEquals("value",response.getPrincipal().getAttributes().get("key"));
    assertEquals("*", ((MockHttpServletResponse)chain.getResponse()).getHeader("Access-Control-Allow-Origin"));


    /*
     * Also test the cache by repeating the call and setting the expected result
     * to null (which would cause an exception in MockHandler#invariant if the
     * cache does not kick in)
     */
    Resource[] resource = null;
    doCallFilter(resource, new MockHttpServletResponse());
  }

  /**
   * Test method for
   * {@link org.surfnet.oaaas.auth.AuthorizationServerFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
   * .
   * 
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testDoFilterWrongAccessToken() throws IOException, ServletException {
    VerifyTokenResponse recorderdResponse = new VerifyTokenResponse("wtf");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = doCallFilter(recorderdResponse, response);
    /*
     * Verify that the response is 403 and that the chain is stopped
     */
    assertNull(chain.getRequest());
    assertEquals(403, response.getStatus());
  }

  @Test
  public void testCorsHeadersPreflight() throws IOException, ServletException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "bearer dummy-access-token");
    request.addHeader("Origin", "http://www.test.org");
    request.addHeader("Access-Control-Request-Method", "PATCH");
    request.addHeader("Access-Control-Request-Headers", HttpHeaders.AUTHORIZATION + ", X-Custom-Header");
    request.setMethod("OPTIONS");

    MockHttpServletResponse response = new MockHttpServletResponse();
    //null FilterChain as chain.filter to proceed the flow is not called with preflight
    filter.doFilter(request, response, null);

    assertEquals("86400", response.getHeader("Access-Control-Max-Age"));
    assertEquals(HttpHeaders.AUTHORIZATION + ", X-Custom-Header", response.getHeader("Access-Control-Allow-Headers"));
    assertEquals("GET, OPTIONS, HEAD, PUT, PATCH, POST, DELETE", response.getHeader("Access-Control-Allow-Methods"));
    assertEquals("*", response.getHeader("Access-Control-Allow-Origin"));

  }

  private MockFilterChain doCallFilter(VerifyTokenResponse recorderdResponse) throws IOException, ServletException {
    return doCallFilter(recorderdResponse, new MockHttpServletResponse());
  }

  private MockFilterChain doCallFilter(VerifyTokenResponse recorderdResponse, MockHttpServletResponse response)
      throws IOException, ServletException {
    return doCallFilter(new Resource[] { new ByteArrayResource(mapper.writeValueAsString(recorderdResponse)
        .getBytes(), "json") }, response);
  }

  private MockFilterChain doCallFilter(Resource[] resource, MockHttpServletResponse response) throws IOException,
      ServletException {
    super.setResponseResource(resource);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Origin", "http://www.test.org");
    request.addHeader(HttpHeaders.AUTHORIZATION, "bearer dummy-access-token");
    MockFilterChain chain = new MockFilterChain();
    filter.doFilter(request, response, chain);
    return chain;
  }

}
