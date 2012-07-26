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
package org.surfnet.oaaas.example.api;

import java.security.Principal;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

/**
 * {@link Authenticator} that ask the Authorization Server to check
 *
 */
public class OAuthAuthenticator implements Authenticator<String, Principal> {

  private AuthConfiguration auth;

  /**
   * @param configuration
   */
  public OAuthAuthenticator(UniversityFooConfiguration configuration) {
    this.auth = configuration.getAuth();
  }

  /* (non-Javadoc)
   * @see com.yammer.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
   */
  @Override
  public Optional<Principal> authenticate(String credentials) throws AuthenticationException {
    //TODO go to the authorization service
    
    //http://www.mkyong.com/webservices/jax-rs/restful-java-client-with-jersey-client/
    
    Principal principal= new Principal() {
      @Override
      public String getName() {
        return "TOOD";
      }};
    return Optional.of(principal);
  }

}
