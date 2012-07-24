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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

/**
 * Responsible for handling the actual authentication of the user
 * 
 */
public interface AuthenticationHandler {

  /**
   * The request attribute name to be set by the implementation
   */
  String PRINCIPAL = "principal";

  /**
   * 
   * @param request
   *          the HttpServletRequest for possible forward using the
   *          {@link HttpServletRequest#getRequestDispatcher(String)} and
   *          accessing the query parameters (if needed)
   * @param response
   *          the HttpServletResponse for possible
   *          {@link HttpServletResponse#sendRedirect(String)}
   * 
   * @param forwardUri
   *          the absolute URI to forward the result of the (successful)
   *          authentication.
   * 
   * @param csrfValue
   *          {@link String} with a unique value to report back as a query
   *          parameter to the forwardUri
   */
  Response handle(HttpServletRequest request, HttpServletResponse response, String forwardUri, String csrfValue);

}
