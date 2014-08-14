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

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.mrbean.MrBeanModule;

import com.sun.jersey.api.client.Client;

/**
 * We need to be able to set the {@link ObjectMapper} on the {@link Client} to
 * make sure the {@link MrBeanModule} is used.
 *
 */
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  private ObjectMapper mapper;

  public ObjectMapperProvider(){
    mapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY).enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL).setVisibility(JsonMethod.FIELD, Visibility.ANY);
    mapper.registerModule(new MrBeanModule());
  }

  /* (non-Javadoc)
   * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
   */
  @Override
  public ObjectMapper getContext(Class<?> type) {
    return mapper;
  }

}
