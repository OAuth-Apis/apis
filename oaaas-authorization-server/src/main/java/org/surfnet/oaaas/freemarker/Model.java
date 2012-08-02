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
package org.surfnet.oaaas.freemarker;

import java.util.Map;

/**
 * Model for Freemarker
 *
 */
public class Model {
  
  private Map<String, Object> model;

  private String view;

  public Model(Map<String, Object> model, String view) {
    super();
    this.model = model;
    this.view = view;
  }


  /**
   * @return the model
   */
  public Map<String, Object> getModel() {
    return model;
  }

  /**
   * @return the view
   */
  public String getView() {
    return view;
  }

}
