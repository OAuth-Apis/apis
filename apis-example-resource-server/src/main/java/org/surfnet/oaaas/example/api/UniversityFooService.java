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

import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.example.api.resource.UniversityResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.oauth.OAuthProvider;
import com.yammer.dropwizard.config.Environment;

/**
 * Main entry
 *
 */
public class UniversityFooService extends Service<UniversityFooConfiguration> {

  /*
   * Used by DropWizard to bootstrap the application. See README.md
   */
  public static void main(String[] args) throws Exception {
    if (args == null || args.length != 2) {
      args = new String[] { "server", "university-foo-local.yml" };
    }
    new UniversityFooService().run(args);
  }

  private UniversityFooService() {
    super("university-foo");
  }

  @Override
  protected void initialize(UniversityFooConfiguration configuration, Environment environment)
      throws ClassNotFoundException {
    environment
        .addProvider(new OAuthProvider<AuthenticatedPrincipal>(new OAuthAuthenticator(configuration), "protected-resources"));
    environment.addResource(new UniversityResource());
    
  }

}
