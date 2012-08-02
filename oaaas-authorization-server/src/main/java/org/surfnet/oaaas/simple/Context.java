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
package org.surfnet.oaaas.simple;

import org.surfnet.oaaas.model.Client;

/**
 * Wrapper for dynamic view values
 * 
 */
public class Context {

  private String actionUri;
  private String authState;
  private Client client;

  public Context(String actionUri, String authState) {
    super();
    this.actionUri = actionUri;
    this.authState = authState;
  }

  public Context(String actionUri, String authState, Client client) {
    super();
    this.actionUri = actionUri;
    this.authState = authState;
    this.client = client;
  }

  /**
   * @return the forwardUri
   */
  public String getActionUri() {
    return actionUri;
  }

   /**
   * @return the authState
   */
  public String getAuthState() {
    return authState;
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }

 }
