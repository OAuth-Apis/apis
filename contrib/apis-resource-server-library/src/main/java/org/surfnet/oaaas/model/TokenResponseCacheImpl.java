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

import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very simple - but highly effective - TokenResponseCache implementation. Please carefully monitor the performance / cache hit-ratio
 * in production as the maxSize in combination with the expireTimeSeconds is important. If the maxSize is too small and the expireTimeSeconds to low
 * it will result in a cache that with each addition will try to make space (e.g. effectively only removing the oldest entry each time).
 */
public class TokenResponseCacheImpl implements TokenResponseCache {

  private Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();
  private int maxSize;
  private long expireTime;

  public TokenResponseCacheImpl(int maxSize, long expireTimeSeconds) {
    this.maxSize = maxSize;
    this.expireTime = expireTimeSeconds * 1000;
    invariant();
  }

  private void invariant() {
    Assert.isTrue(maxSize > 0, "Maxsize must be greater then 0");
    Assert.isTrue(expireTime < ((1000 * 60 * 60 * 24) + 1), "Maximal expireTime is one day");
    Assert.isTrue(expireTime > 0, "ExpireTimeMilliseconds must be greater then 0");
  }

  @Override
  public VerifyTokenResponse getVerifyToken(String accessToken) {
    VerifyTokenResponse response = null;
    if (accessToken != null) {
      CacheEntry cacheEntry = cache.get(accessToken);
      if (cacheEntry != null) {
        if (isExpired(cacheEntry)) {
          cache.remove(accessToken);
        } else {
          response = cacheEntry.value;
        }
      }
    }
    return response;
  }

  private boolean isExpired(CacheEntry cacheEntry) {
    return cacheEntry.expireBy < System.currentTimeMillis();
  }

  @Override
  public void storeVerifyToken(String accessToken, VerifyTokenResponse tokenResponse) {
    if (accessToken != null && tokenResponse != null) {
      if (cache.size() == maxSize) {
        cleanUpCache();
      }
      cache.put(accessToken, new CacheEntry(tokenResponse, System.currentTimeMillis() + expireTime));
    }
  }

  private void cleanUpCache() {
    Set<Map.Entry<String, CacheEntry>> entries = cache.entrySet();
    long ago = Long.MAX_VALUE;
    String oldestKey = null;
    for (Map.Entry<String, CacheEntry> entry : entries) {
      if (isExpired(entry.getValue())) {
        cache.remove(entry.getKey());
      } else if (entry.getValue().expireBy < ago) {
        oldestKey = entry.getKey();
        ago = entry.getValue().expireBy;
      }
    }
    if (oldestKey != null) {
      cache.remove(oldestKey);
    }
  }

  private class CacheEntry {
    private VerifyTokenResponse value;
    private long expireBy;

    CacheEntry(VerifyTokenResponse verifyTokenResponse, long expireBy) {
      this.value = verifyTokenResponse;
      this.expireBy = expireBy;
    }
  }
}
