APIs
======
The apis (APIs Secure) project offers an OAuth 2.0 Authorization Server that can be used to kickstart your API authentication. In essence it enables you to focus on your actual resource endpoints and use the out-of-the-box authorization server to authenticate resource owners and subsequently validate the access tokens that were granted to the Client applications. We will describe the typical use cases in more details in sections below.

## Features

- An OAuth2 Authorization Server compliant with [the draft v2-31 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2-31)
  * Pluggable authentication and userConsent handling (with default implementations provided)
  * Support for authorization code, implicit grant and client credentials
  * Optional refresh tokens
  * Implementation of a Java Filter to be used in Resource Servers for all required communication with the Authorization Server
  * GUI included for the registration of Resource Servers and Client apps
  * Clients are highly configurable (refresh tokens, allow implicit grant, allow client crendentials etc.)

- Two OAuth2 demo Resource Servers
  * DropWizard stand-alone Resource Server with limited functionality (using in-memory JSON-based backend) to demo the OAuth Authorization Server
  * Standard Java web application to showcase the use of the communication between typical Resource Servers and the Authorization Server 

- An example Client App implementation to demo the OAuth flow for a typical (possibly native) Client App
  * This 'involves' a running (example) Resource Server and the Authorization Server

- An implementation add-on for connecting to a compliant SAML IdP for authentication

## Getting Started

First clone this repo. To build the sources you need to have [maven 3](http://maven.apache.org/download.html) installed.

The default JVM size is not sufficient - it rarely is - so when running the various modules in test mode using `mvn` you will have to allocate more memory to the JVM:

	export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512M"

After the clone build the entire project

    mvn clean install

## Upgrades

Changes between versions are listed in [CHANGES.md](CHANGES.md)

### Run Authorization Server

Go the authorization-server-war and start the application

    cd apis-authorization-server-war
    mvn jetty:run

The authorization-server-war application is capable of authenticating Resource Owners (e.g. users) and granting and validating Access Tokens (and optional Refresh Tokens) on behalf of Resource Servers that are receiving resource calls from a Client app. It also offers a JavaScript application to manage Resource Servers and Client application instances. 

### Run Example Resource Server (war & standalone modus)

We have provided two example resource servers. One (apis-example-resource-server-war) is a very simple Java web application
that only demonstrates how a Resource Server can communicate with the Authorization Server using the `org.surfnet.oaaas.auth.AuthorizationServerFilter` (which is a simple `javax.servlet.Filter`). The `AuthorizationServerFilter` only protects a single JSP page in the apis-example-resource-server-war module. See the [README of this submodule] (https://github.com/OpenConextApps/apis/tree/master/apis-example-resource-server-war) on how to use curl to test this 'flavor'.

For now we will continue to use the other example resource server (apis-example-resource-server, NOT the apis-example-resource-server-war!) , built using [Dropwizard] (http://dropwizard.codahale.com/), to demonstrate the apis Authorization Server. We will need to start the apis-exampl	e-resource-server to demonstrate the entire flow (new Terminal session):

    cd apis-example-resource-server
    java -jar target/apis-example-resource-server-1.1.1-SNAPSHOT.jar

If the last command gives you an error check if the master version is still 1.1.1-SNAPSHOT.

### Run Example Client App

We have now an Authorization Server running and an example Resource Server (and we have not much to show for it, yet!). To demonstrate the entire flow we will start an example Client Application which will communicate with:

- first the Authorization Server to obtain an Access Token
  * Note that this only works because we have configured both the example-resource-server and the example-client-app in the dummy data defined in /apis-authorization-server/src/main/resources/db/migration/hsqldb/V1__auth-server-admin.sql
- then the example Resource Server to make an REST API call using the obtained OAuth Access Token
  * Note that the example Resource Server communicates with the Authorization Server to validate the token

Start the example-client-app (new Terminal session)

    cd apis-example-client-app
    mvn jetty:run

Now start your browser and go to <a href="http://localhost:8084/test" target="_blank">http://localhost:8084/test</a>. In three steps you can see what the client app has to do to make a REST call to example Resource Server. You can also very easily test this against any Resource Server (or for that case an Authorization Server running not running on local host) by changing the values in the client.apis.properties file.

### Resource Servers and Client apps GUI registration

The GUI for Resource Servers and Client apps registration can be found at <a href="http://localhost:8080/client/client.html" target="_blank">http://localhost:8080/client/client.html</a>:

For an overview of the different roles and the subsequent documentation please refer to the latest version of the [oauth v2 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2#section-1.1).

With the client you can create your own Resource Servers and Client applications. The admin client is actually an implicit-grant JavaScript OAuth client that uses the Authorization Server to obtain an access-token and subsequently used the Resource Server endpoints - included in the Authorization server - to manage the data.

![screenshot](https://raw.github.com/OpenConextApps/apis/master/apis-images/apis-client.png)

To login on the client the default authentication module - this is pluggable - is used: a Form based login which will accept anything (see FormLoginAuthenticator#processForm):

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

#### Admin privileges
It is possible to have admin privileges in the JS GUI, meaning you can edit / delete all of the known Resource Servers and Clients. A common usecase if you have a department responsible for the administration of ResourceServers and Clients. In order to obtain admin rights your
AuthenticatedPrincipal needs to return true for the `isAdminPrincipal` method. The default implementation does not so and everyone can only edit / delete their own ResourceServers and Clients. You can change this by changing the `authenticatorClass` configuration in the `apis-authorization-server-war/src/test/resources/apis.application.properties` file to `org.surfnet.oaaas.noop.NoopAdminAuthenticator`.

The [surfconext authn submodule](https://github.com/OpenConextApps/apis/tree/master/apis-surfconext-authn) is another production-ready implementation that uses group membership to determine the is-admin feature.

### Component overview

The following diagram shows all components and how they play together.

![Deployment overview](https://raw.github.com/oharsta/apis/master/apis-images/apis_deployment_diagram.png)

The authorization server is capable of handing out access tokens on behalf of the user (e.g. the resource owner) for registered Client applications. It uses a database but which flavor you want to use is up to you. We have been developing with mysql and for testing purposes we use the hsqldb file database. 

Once a Client app has obtained an access token (in combination with a refresh token if you have configured this for the Client app) it can query the resource server with whatever data you would like to share on behalf of the user (so we have made the implicit assumption that the resource server will only hand out data that is for the users eyes only or perhaps only for users that share a certain membership - the enforcement of that is of course the responsibility of the resource server). Note that the actual authentication of the user is pluggable and in real life the API provided by the resource server and subsequent data will 'know' of the identity returned by the authentication part of the authorization.

When the Client app queries the API of the resource server it will - prior to returning the data - ask the authorization server to validate the access token (For more info see the section [How do Resource Servers verify the Access Token?](https://github.com/OpenConextApps/apis#how-do-resource-servers-verify-the-access-token)). If the token is valid (e.g. not expired and belonging to the Resource Server and Client app) then the identity of the owner who has granted consent/ access is returned. Note that the actual information in the identity (group memberships, emails or whatever) returned to the resource server is the exact information that was returned by the authentication module when the user proved he could authenticate him/herself.

Client apps and resource servers are registered using the OAuth admin application that is part of the authorization server. Note that the JavaScript OAuth admin application is actually also a Client app which needs to be registered out-of-band (e.g. by SQL) otherwise you will have the chicken-egg paradigm to solve. The resource server is something you will want to provide for yourself. After registration of your resource server with the admin client the key / secret for secure communication with the authorization server are provided to you.

Typically you will want to deploy the authorization server in a servlet container like Tomcat or Jetty. You can of course also deploy the war on an Application Server if you like added complexity. One authorization server can serve up to many, many resource servers and as the communication between authorization server and resource server is a very simple REST / JSON API the technical nature of the resource server really does not care. See the AuthorizationServerFilter.java in the apis-resource-server-library module for an example on how to resource servers can 'talk' to the authorization server.

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

For an example of an `AbstractAuthenticator` that uses a federation of SAML2 compliant Identity Providers to perform the actual authentication have a look at `SAMLAuthenticator` in the submodule apis-surfconext-authn.

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

### How do Resource Servers verify the Access Token?

They don't. They ask the Authorization Server to do this. For Java implemented Resource Servers you can use (or extend) the `AuthorizationServerFilter` in the `apis-resource-server-library` module. For non-Java Resource Servers the protocol is simple:

    GET https://<domain-name-authorization-server>/v1/tokeninfo?access_token=<access_token>
    Authorization: Basic <Base64 encoded key:secret >
    Accept: application/json

The key/secret are obtained when creating the Resource Server in the admin GUI of the Authorization Server.

The Response is the json representation of the tokenInfo with the client name, the Principal information and the expiry time (if set for this client).

    {
        "audience": "Authorization Server Admin Client",
        "scopes": [
            "read",
            "write"
        ],
        "principal": {
            "name": "aaaaa",
            "roles": [],
            "groups": [],
            "adminPrincipal": false,
            "attributes": {
                "DISPLAY_NAME": "test-user",
                "IDENTITY_PROVIDER": "http://mock-idp"
            }
        },
        "expires_in": 1373178401833
    }

Default we omit the type-information in the JSON as it would tie the Resource Server to the libraries used in the Authorization Server. For those Resource Servers that depend on
a specific subtype of `AuthenticatedPrincipal` (e.g. would like to cast the principal to a known implementation) it is possible to override the default behaviour by changing the property
`adminService.jsonTypeInfoIncluded` in the properties file that drive the Authorization Server (`apis.application.properties`) and respectively the properties file that drive the Resource Server
(the property file driving the AuthorizationServletFilter - if used - is set using a servlet init-parameter named `apis-resource-server.properties.file`).

#### Authorization Server - Resource Server demo flow

First start up the Authorization Server (if you have not already done so):

    cd apis-authorization-server-war
    mvn jetty:run

When started up in local development mode with Jetty the content of the in-memory test database is populated with the content from `apis/apis-authorization-server-war/src/main/resources/db/migration/hsqldb_content/V1__auth-server-admin.sql`. One entry in that file
is a client configured with `allowedClientCredentials=true`.

```sql
/*
Client for client credentials
*/
INSERT INTO client (id, contactEmail, contactName, description,
                    clientName, thumbNailUrl, resourceserver_id,
                    clientId, secret, allowedClientCredentials)
VALUES
    (99993, 'it-test-client-credential@example.com',
    'john.client.credential.grant', 'it test client credential grant',
    'it test client credential grant', 'thumbnailurl', 99997,
    'it-test-client-credential-grant', 'some-secret-client-credential-grant', 1);
INSERT INTO Client_scopes values (99993, 'read');
```

Let's test this client with curl. We need the base64 encoded client_key:secret (`it-test-client-credential-grant:some-secret-client-credential-grant`):

    echo -n 'it-test-client-credential-grant:some-secret-client-credential-grant' | openssl base64

and we use this to - [client credential grant](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-1.3.4) does not involve any user involvement as it is granted to highly trusted clients - obtain a access token:

    curl -v -H "Accept: application/json" -H "Content-type: application/x-www-form-urlencoded" -H \
      "Authorization: Basic aXQtdGVzdC1jbGllbnQtY3JlZGVudGlhbC1ncmFudDpzb21lLXNlY3JldC1jbGllbnQtY3JlZGVudGlhbC1ncmFudA==" \
      -X POST -d 'grant_type=client_credentials' http://localhost:8080/oauth2/token

The result is a new access token:

    {
        "scope": "read",
        "access_token": "38b0b9e5-0ff0-42f9-a9df-28cfaf996de2",
        "token_type": "bearer",
        "expires_in": 0
    }

Now test the call that a Resource Server would make to the Authorization Server when this client uses his newly obtained access token to perform an API call against the Resource Server. We will mimic the Resource Server that actually is connected to the client we used (`id = 99997, key:secret is it-test-resource-server:somesecret`).

This is the call - proprietary API as described [out-of-scope](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-1.1) in the OAuth2 specification - to the Authorization Server to validate the access token:

    curl -v -H "Authorization: Basic aXQtdGVzdC1yZXNvdXJjZS1zZXJ2ZXI6c29tZXNlY3JldA==" \
      "http://localhost:8080/v1/tokeninfo?access_token=38b0b9e5-0ff0-42f9-a9df-28cfaf996de2"

And the result as expected (note as we used client credential flow the principal name is the client name);

    {
        "audience": "it test client credential grant",
        "scopes": [
            "read"
        ],
        "principal": {
            "name": "it-test-client-credential-grant",
            "roles": [],
            "groups": [],
            "adminPrincipal": false,
            "attributes": {}
        },
        "expires_in": 0
    }

If you write your own implementation of the above flow for your Resource Servers strongly consider caching subsequent calls & answers from the Authorization Server.

### Plugging in your custom implementations

The Authorization Server is wired up using a Spring configuration. The class `org.surfnet.oaaas.config.SpringConfiguration` in the apis-authorization-server-war module is responsible for wiring up the dependencies. You don't need to override this configuration if you only want to change the authentication (and/ or the user consent) as we have externalized this into the apis.application.properties file. The authorization server which you have started  up with the maven jetty plugin uses the apis.application.properties file in apis-authorization-server-war/src/test/resources.

In real life deployment you will need to provide the authorization server with an external apis.application.properties file. We have not put this into the war file as you would end up with an environment specific war application.The apis.application.properties file contains all environment specific variables like database settings and the authentication implementation:

<pre>
# The authentication module
authenticatorClass=org.surfnet.oaaas.authentication.FormLoginAuthenticator

# The user consent module
userConsentHandlerClass=org.surfnet.oaaas.consent.FormUserConsentHandler
</pre>
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

### The REST interface to build your own GUI

You can use the exposed REST interface of the ResourceServerResource, ClientResource and AccessTokenResource to build your own GUI. The resources offer full CRUD for the persistent objects. See the annotations on the mentioned Resources.

While we were working on the JavaScript Admin client included in the Authorization Server war we locally disabled the login and consent to speed up the local feedback cycle when developing in 'jetty-modus':

<pre>
# The authentication module
# authenticatorClass=org.surfnet.oaaas.authentication.FormLoginAuthenticator
authenticatorClass=org.surfnet.oaaas.noop.NoopAuthenticator

# The user consent module
# userConsentHandlerClass=org.surfnet.oaaas.consent.FormUserConsentHandler
userConsentHandlerClass=org.surfnet.oaaas.noop.NoopUserConsentHandler
</pre>

### SURFConext Authn Authenticator

See the information in the [surfconext authn submodule](https://github.com/OpenConextApps/apis/tree/master/apis-surfconext-authn) for detailed information on the SURFconext SAML Authentication implementation.

### SSL/TLS

As noted in the OAuth 2.0 RFC ([1](http://tools.ietf.org/html/rfc6749#section-3.1), [2](http://tools.ietf.org/html/rfc6749#section-3.1.2.1), [3](http://tools.ietf.org/html/rfc6749#section-3.2)), the use of TLS is required, as credentials are transmitted in clear-text.
This is not enforced at code level, but it must be when deploying the application.

### Tests

You can build the entire project including the integration and selenium tests (which are dependent on your version of FireFox):

mvn clean install -P integration

