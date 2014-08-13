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

package org.surfnet.oaaas.resource.resourceserver;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal;
import org.surfnet.oaaas.model.ResourceOwner;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.ResourceOwnerRepository;

public class ResourceOwnerResourceTest {
  
  @InjectMocks
  private ResourceOwnerResource resourceOwnerResource;
  
  @Mock
  private HttpServletRequest request;
  
  @Mock
  private ResourceOwnerRepository resourceOwnerRepository;

  @Before
  public void before() throws Exception {
    MockitoAnnotations.initMocks(this);
    VerifyTokenResponse verifyTokenResponse = new VerifyTokenResponse();
    verifyTokenResponse.setPrincipal(new AuthenticatedPrincipal("user"));
    verifyTokenResponse.setScopes(Arrays.asList("read"));
    when(request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE)).thenReturn(verifyTokenResponse);
  }

  @Test
  public void getAllWhenNoneFound() {
    
    Response response = resourceOwnerResource.getAll(request);
    
    assertEquals(200, response.getStatus());
    List<ResourceOwner> owners = (List<ResourceOwner>) response.getEntity();
    assertEquals(0, owners.size());
  }
}
