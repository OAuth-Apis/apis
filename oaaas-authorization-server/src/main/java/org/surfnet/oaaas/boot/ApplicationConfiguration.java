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

package org.surfnet.oaaas.boot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.yammer.dropwizard.config.Configuration;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationConfiguration extends Configuration {

  @JsonProperty
  @NotNull
  private String authenticatorClass;

  @JsonProperty
  @NotNull
  @Valid
  private AdminServiceConfiguration adminService = new AdminServiceConfiguration();

  public String getAuthenticatorClass() {
    return authenticatorClass;
  }

  public AdminServiceConfiguration getAdminService() {
    return adminService;
  }

  public static class AdminServiceConfiguration extends Configuration {
    @JsonProperty
    @NotNull
    private String tokenVerificationUrl;

    @JsonProperty
    @NotNull
    private String resourceServerKey;

    @JsonProperty
    @NotNull
    private String resourceServerSecret;

    public String getTokenVerificationUrl() {
      return tokenVerificationUrl;
    }

    public String getResourceServerKey() {
      return resourceServerKey;
    }

    public String getResourceServerSecret() {
      return resourceServerSecret;
    }


  }
}
