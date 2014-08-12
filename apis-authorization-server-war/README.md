Authorization Server web application
======
The Authorization Server web application is a WAR-wrapper around the main functionality of the `apis-authorization-server` module.
It provides a fully functional web application ready to be deployed in a servlet container.

Components of the web application are:
* plain JS/HTML client for administration of resource servers and clients. (using implicit grant)
* `FormLoginAuthenticator`, `FormUserConsentHandler`
* Bean wiring, using `SpringConfiguration` class
* property files, for environment specific configuration.

## Extending the web application
With separating the components of the war and the core jar, it should be possible to pick only a subset of functionalities and build your own web application.
To extend/modify the default web application, extend `SpringConfiguration` to inject your own framework beans.

See the documentation in the [README.md](https://github.com/OpenConextApps/apis/blob/master/README.md) in the root project for overall documentation.
