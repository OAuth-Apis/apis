Example Resource Server War
======
The Example Resource Server War is a very simple Spring MVC web application that demonstrates how a Resource Server can communicate with the Authorization Server using the `org.surfnet.oaaas.auth.AuthorizationServerFilter` (which is a simple `javax.servlet.Filter`). The `AuthorizationServerFilter` only protects a single JSP page in the apis-example-resource-server-war module.

To see the in action first start the Authorization Server. Go the authorization-server-war and start the application

    cd apis-authorization-server-war
    mvn jetty:run

Go the apis-example-resource-server-war and start the application (new Terminal session)

    cd apis-example-resource-server-war
    mvn jetty:run
	
Then perform a curl (new Terminal session):

    curl -i -v -H "Authorization: bearer 00-11-22-33"  http://localhost:8082

You will see the response of the `/apis-example-resource-server-war/src/main/webapp/index.jsp` which should look this:

	AuthenticatedPrincipalImpl [name=it-test-enduser, roles=[user, admin], attributes={}

This works because of the fact that access token '00-11-22-33' is configured in the dummy data defined in /apis-authorization-server/src/main/resources/db/migration/hsqldb/V1__auth-server-admin.sql

Also configured in the dummy data defined in /apis-authorization-server/src/main/resources/db/migration/hsqldb/V1__auth-server-admin.sql are the resource server with the key-secret as defined in /apis-example-resource-server-war/src/test/resources/apis-resource-server.properties read by the `org.surfnet.oaaas.auth.AuthorizationServerFilter` configured in `/apis-example-resource-server-war/src/main/webapp/WEB-INF/web.xml`

See the documentation in the [README.md](https://github.com/OpenConextApps/apis/tree/master/apis-example-resource-server) in the other Example Resource Server for detailed instructions on how to demo the entire flow.

