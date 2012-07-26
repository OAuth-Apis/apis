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
package org.surfnet.oaaas.example.api.resource;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

/**
 * {@link Test} for {@link UniversityResource}
 *
 */
public class UniversityResourceTest {

  private final UniversityResource resource = new UniversityResource();
  //http://java.dzone.com/articles/how-serialize-javautildate
  @Test
  public void test() throws JsonGenerationException, JsonMappingException, IOException {
    String value = resource.getObjectMapper().writeValueAsString(new Date());
    System.out.println(value);
  }

}
