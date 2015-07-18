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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;

import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.assertEquals;

/**
 * {@link Test} that verifies the new Connector handles correctly the request schema depending on
 * X-Forwarded-Proto HTTP haeder.
 * 
 */
public class SelectChannelConnectorHttpsTest {

	private static final String HTTP_SCHEME = "http";
	private static final String HTTPS_SCHEME = "https";
	
	private static final String XFORWARDED_PROTO = "X-Forwarded-Proto";

	@Test
	public void testSchemaIsChangedAccordingToXForwardedProto() throws IOException {
		Request baseRequest = new Request();
		final Request request = Mockito.spy(baseRequest);
		EndPoint endPoint = Mockito.mock(EndPoint.class);

		Vector<String> headers = new Vector<String>();
		headers.add(XFORWARDED_PROTO);
		Mockito.doReturn(headers.elements()).when(request).getHeaderNames();
		Mockito.doReturn(HTTPS_SCHEME).when(request).getHeader(XFORWARDED_PROTO);

		SelectChannelConnectorHttps connector = new SelectChannelConnectorHttps();
		connector.customize(endPoint, request);

		assertEquals(HTTPS_SCHEME, request.getScheme());
	}

	@Test
	public void testSchemaIsNotChangedForNoXForwardedProto() throws IOException {
		Request baseRequest = new Request();
		final Request request = Mockito.spy(baseRequest);
		EndPoint endPoint = Mockito.mock(EndPoint.class);

		Vector<String> headers = new Vector<String>();
		Mockito.doReturn(headers.elements()).when(request).getHeaderNames();

		SelectChannelConnectorHttps connector = new SelectChannelConnectorHttps();
		connector.customize(endPoint, request);

		assertEquals(HTTP_SCHEME, request.getScheme());
	}

}
