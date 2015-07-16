package org.surfnet.oaaas.jetty;

import java.util.Enumeration;
import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SelectChannelConnectorHttps extends SelectChannelConnector {

	public static final String X_FORWARDED_PROTO = "x-forwarded-proto";

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

