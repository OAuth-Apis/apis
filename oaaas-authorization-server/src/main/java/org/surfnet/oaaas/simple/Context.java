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

/**
 * Wrapper for dynamic {@link LoginView} values
 * 
 */
public class Context {

  private String forwardUri;
  private String authState;

  public Context(String forwardUri, String authState) {
    super();
    this.forwardUri = forwardUri;
    this.authState = authState;
  }

  /**
   * @return the forwardUri
   */
  public String getForwardUri() {
    return forwardUri;
  }

  /**
   * @param forwardUri
   *          the forwardUri to set
   */
  public void setForwardUri(String forwardUri) {
    this.forwardUri = forwardUri;
  }

  /**
   * @return the authState
   */
  public String getAuthState() {
    return authState;
  }

  /**
   * @param authState the authState to set
   */
  public void setAuthState(String authState) {
    this.authState = authState;
  }

 

}
