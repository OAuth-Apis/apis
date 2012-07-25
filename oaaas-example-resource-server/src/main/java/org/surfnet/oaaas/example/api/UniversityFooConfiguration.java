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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonProperty;

import com.yammer.dropwizard.config.Configuration;

/**
 * Main Configuration
 * 
 */
public class UniversityFooConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty
  private AuthConfiguration auth = new AuthConfiguration();

  /**
   * @return the auth
   */
  public AuthConfiguration getAuth() {
    return auth;
  }

  /**
   * @param auth the auth to set
   */
  public void setAuth(AuthConfiguration auth) {
    this.auth = auth;
  }

}
