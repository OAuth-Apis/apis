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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.surfnet.oaaas.auth.ObjectMapperProvider;
import org.surfnet.oaaas.model.StatisticsResponse.ResourceServerStat;

/**
 * {@link Test} for {@link StatisticsResponse}
 * 
 */
public class StatisticsResponseTest {

  private ObjectMapper mapper = new ObjectMapperProvider().getContext(ObjectMapper.class);

  @Test
  public void test() throws JsonGenerationException, JsonMappingException, IOException {
    List<ResourceServerStat> resourceServers = Arrays
        .asList(new ResourceServerStat[] { new StatisticsResponse.ResourceServerStat("resourceServer1",
            "resourceServer1 description", Arrays.asList(new StatisticsResponse.ClientStat("client1",
                "client 1 description", 500))) });
    StatisticsResponse response = new StatisticsResponse(resourceServers);
    String value = mapper.writeValueAsString(response);
    assertEquals(
        "{\"resourceServers\":[{\"name\":\"resourceServer1\",\"description\":\"resourceServer1 description\",\"clients\":[{\"name\":\"client1\",\"description\":\"client 1 description\",\"tokenCount\":500}]}]}",
        value);
  }

}
