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
package org.surfnet.oaaas.jetty;

import java.util.Enumeration;
import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.io.EndPoint;

/**
 * {@link SelectChannelConnector} that sets the request schema according to the
 * value eventually specified in the HTTP header name "X-ForwardedProto.
 */
public class SelectChannelConnectorHttps extends SelectChannelConnector {

	private static final String X_FORWARDED_PROTO = "x-forwarded-proto";

	private String getHeaderCaseInsensitive(Request request, String headerName) {
		Enumeration<String> headerNames = (Enumeration<String>) request.getHeaderNames();
		while (headerNames.hasMoreElements()){
			String curHeaderName = (String) headerNames.nextElement();
			if (curHeaderName.toLowerCase().equals(headerName)) {
				return request.getHeader(curHeaderName);
			}
		}
		return null;
	}

	@Override
	public void customize(EndPoint endpoint, Request request) throws IOException {

		String forwardedProtocol = getHeaderCaseInsensitive(request, X_FORWARDED_PROTO);
		if (forwardedProtocol != null) {
			if (forwardedProtocol.indexOf("https") >= 0) {
				request.setScheme("https");
			}
		}

		super.customize(endpoint, request);
	}

}

