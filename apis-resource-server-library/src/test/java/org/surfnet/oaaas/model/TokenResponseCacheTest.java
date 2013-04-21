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
package org.surfnet.oaaas.model;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class TokenResponseCacheTest {

  private TokenResponseCache cache;

  @Before
  public void before() throws Exception {
    cache = new TokenResponseCacheImpl(3, 60 * 60 * 24);
  }

  @Test
  public void testGetVerifyToken() throws Exception {
    VerifyTokenResponse verifyToken = cache.getVerifyToken(null);
    assertNull(verifyToken);

    VerifyTokenResponse token = new VerifyTokenResponse();
    cache.storeVerifyToken("123456", token);

    VerifyTokenResponse res1 = cache.getVerifyToken("123456");
    assertEquals(token, res1);

  }

  @Test
  public void testStoreVerifyTokenWithMaxSize() throws Exception {
    for (int i = 0; i < 5; i++) {
      cache.storeVerifyToken(Integer.toString(i), new VerifyTokenResponse());
      Thread.sleep(5);
    }
    for (int i = 0; i < 2; i++) {
      VerifyTokenResponse verifyToken = cache.getVerifyToken(Integer.toString(i));
      assertNull(verifyToken);
    }
    for (int i = 2; i < 5; i++) {
      VerifyTokenResponse verifyToken = cache.getVerifyToken(Integer.toString(i));
      assertNotNull(verifyToken);
    }
  }

  @Test
  public void testStoreVerifyTokenWithExpires() throws Exception {
    cache = new TokenResponseCacheImpl(3, 1);
    for (int i = 0; i < 5; i++) {
      cache.storeVerifyToken(Integer.toString(i), new VerifyTokenResponse());
    }
    Thread.sleep(1500);
    cache.storeVerifyToken(Integer.toString(10), new VerifyTokenResponse());
    for (int i = 0; i < 5; i++) {
      VerifyTokenResponse verifyToken = cache.getVerifyToken(Integer.toString(i));
      assertNull(verifyToken);
    }
    VerifyTokenResponse verifyToken = cache.getVerifyToken(Integer.toString(10));
    assertNotNull(verifyToken);
  }
}
