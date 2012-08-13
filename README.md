apis 
======
The apis (APIs Secure) project offers an OAuth 2.0 Authorization Server that can be used to kickstart your API authentication. In essence it enables you to focus on your actual resource endpoints and use the out-of-the-box authorization server to authenticate resource owners and subsequently validate the access tokens that were granted to the Client applications. We will offer more details in later sections.

## Features

- An OAuth2 Authorization Server compliant with [the draft v2-30 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2-30)
  * Pluggable authentication and userConsent handling (with default implementations provided)
  * Support for authorization code and implicit grant
  * GUI included for the registration of Resource Servers and Client apps

- An OAuth2 demo Resource Server
  * In-memory JSON-based backend with limited functionality only to demo the OAuth Authorization Server

- An implementation add-on for connecting to a compliant SAML IdP for authentication

## Getting Started

First clone this repo. To build the sources you need to have [maven 3](http://maven.apache.org/download.html) installed.

After the clone build the entire project

    mvn clean install

### Run Authorization Server

Go the authorization-server-war and start the application

    cd apis-authorization-server-war
    mvn jetty:run

The authorization-server-war application is capable of authenticating users and granting access tokens (and optional refresh tokens). It also offers a JavaScript application to manage Resource Servers and Client application instances. 

### Run Example Resource Server

Go to the  

### Resource Servers and Client apps GUI registration

The GUI for Resource Servers and Client apps registration can be found at:
[http://localhost:8080/client/client.html/](http://localhost:8080/client/client.html)

For an overview of the different roles and the subsequent documentation please refer to the latest version of the [oauth v2 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2#section-1.1).

With the client you can create Resource Servers and Client applications. But to do so you will first need to login. So hit the login button and login. The default authentication module - this is pluggable - is a Form based login which will accept anything (see FormLoginAuthenticator#processForm):

```java
private void processForm(final HttpServletRequest request) {
  /*
   * Hook for actually validating the username/ password against a database,
   * ldap, external webservice or whatever to perform authentication
   */
  setAuthStateValue(request, request.getParameter(AUTH_STATE));
  setPrincipal(request, new AuthenticatedPrincipal(request.getParameter("username")));
}
```
After the login you will need to grant consent (the Authorization Server GUI is actually an OAuth2.0 Client as we are eating our own dogfood) in order for the client to access your personal resources (which you don't have for now but this is going to change).

After this you can add your own ResourceServer and Client instances. See the tooltip's in the insert/ edit forms for more information about the attributes of Resource Servers and Clients. 

### Component overview

![Deployment overview](https://raw.github.com/oharsta/apis/master/apis-images/apis_deployment_diagram.png)

## Extending the defaults

The defaults are alright for getting started, but in real life you must provide at least one implementation (and most likely two):

- Authentication
  * The default authentication module is very lenient accepting every username/ password combination. 
- UserConsent
  * The default user consent module will probably be sufficient, but most likely you'll want to change the L&F

The default implementations reside in the apis-authorization-server-war submodule. 

### Authentication module 

To change the authentication of Resource Owners that are redirected to the /authorize page you will either extend `org.surfnet.oaaas.authentication.FormLoginAuthenticator` and override `processForm`:

```java
/**
 * 
 * Hook for actually validating the username/ password against a database,
 * ldap, external webservice or whatever to perform authentication
 * 
 * @param request
 *          the {@link HttpServletRequest}
 */
protected void processForm(final HttpServletRequest request) {
  setAuthStateValue(request, request.getParameter(AUTH_STATE));
  setPrincipal(request, new AuthenticatedPrincipal(request.getParameter("username")));
}
```

Or you can implement your own 'org.surfnet.oaaas.auth.AbstractAuthenticator'. The `AbstractAuthenticator` is a plain `javax.servlet.Filter` implementation, so it possible to redirect to an entirely different application to perform the authentication. 

For an example of an `AbstractAuthenticator` that uses a federation of SAML2 compliant Identity Providers to perform the actual authentication have a look at in `SAMLAuthenticator` the the submodule apis-surfconext-authn.

### User Consent

The default User Consent page is handled by `org.surfnet.oaaas.consent.FormUserConsentHandler`. You can easily extend and override the default behavior:

```java
/**
 * 
 * Return the path to the User Consent page. Subclasses can use this hook by
 * providing a custom html/jsp.
 * 
 * @return the path to the User Consent page
 */
protected String getUserConsentUrl() {
  return "/WEB-INF/jsp/userconsent.jsp";
}
```

### Plugging in your custom implementations

The Authorization Server is wired up using a Spring configuration. The class `org.surfnet.oaaas.config.SpringConfiguration` in the apis-authorization-server-war module is responsible for wiring up the dependencies. You don't need to override this configuration if you only want to change the authentication (and/ or the user consent) as we have externalized this into the apis.application.properties file. The authorization server started up with the maven jetty plugin uses the apis.application.properties file in apis-authorization-server-war/src/test/resources. 

In real life deployment you will need to provide the authorization server with an external apis.application.properties file. We have not put this into the war file as you would end up with an environment specific war application.The apis.application.properties file contains all environment specific variables like database settings and the authentication implementation:

```java
# The authentication module
authenticatorClass=org.surfnet.oaaas.authentication.FormLoginAuthenticator

# The user consent module
userConsentHandlerClass=org.surfnet.oaaas.consent.FormUserConsentHandler
```
If you would prefer a different approach you can override the `org.surfnet.oaaas.config.SpringConfiguration` and implement your own logic to wire your Authentication implementation:

```java
/**
 * Returns the {@link AbstractAuthenticator} that is responsible for the
 * authentication of Resource Owners.
 * 
 * @return an {@link AbstractAuthenticator}
 */
@Bean
public AbstractAuthenticator authenticator() {
  return (AbstractAuthenticator) getConfiguredBean("authenticatorClass");
}
```

### The GUI

You can use the exposed REST interface of the ResourceServerResource and ClientResource to build your own GUI. The following URLs are available for a custom registration interface:

    GET     /admin/resourceServer
    GET     /admin/resourceServer/{resourceServerId}
    PUT     /admin/resourceServer
    POST    /admin/resourceServer/{resourceServerId}
    DELETE  /admin/resourceServer/{resourceServerId}

    GET     /admin/resourceServer/123/client
    GET     /admin/resourceServer/123/client/{clientId}
    PUT     /admin/resourceServer/123/client
    POST    /admin/resourceServer/123/client/{clientId}
    DELETE  /admin/resourceServer/123//client/{clientId}

### Tests

Optionally you can build the entire project with the integration tests enables

mvn clean install -P integration

