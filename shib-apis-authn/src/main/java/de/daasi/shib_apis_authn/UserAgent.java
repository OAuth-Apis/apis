package de.daasi.shib_apis_authn;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class UserAgent {

	private HashMap<String, String> attributes;
	
	public UserAgent (HttpServletRequest request) {

		UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
		ReadableUserAgent agent = parser.parse(request.getHeader("User-Agent"));
		
		attributes = new HashMap<String, String>();
		attributes.put("platform", agent.getOperatingSystem().getName());
		attributes.put("model", agent.getVersionNumber().toVersionString());
		attributes.put("useragent", agent.getName());

        Enumeration<Locale> locales = request.getLocales();
        if (locales.hasMoreElements()) { // do not use 'while' because we only need the first language available
        	Locale firstLocale = (Locale) locales.nextElement();
        	attributes.put("locale", firstLocale.toString()); // use full string de_DE_xxx_yyy	
        } else {
        	attributes.put("locale", "unknown");
        }
	}
	
	public 	HashMap<String, String> getAttributes () {
		return attributes;
	}
}
