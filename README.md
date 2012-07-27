<pre>

   ____                           _____ 
  / __ \   /\                    / ____|
 | |  | | /  \ ______ __ _  __ _| (___  
 | |  | |/ /\ \______/ _` |/ _` |\___ \ 
 | |__| / ____ \    | (_| | (_| |____) |
  \____/_/    \_\    \__,_|\__,_|_____/ 

</pre>
OA-aaS
======
<em>(Work in Progress, development starts in august sponsored by SURFnet)</em>

OAuth Authorization as a Service (OA-aaS) is a generic OAuth2 provider that can be used to kickstart your API authentication. 

Features
--------

- An OAuth2 Provider compliant with [the draft v2-30 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2-30)
  * Pluggable authentication and configurable persistence
  * Support for authorization code and implicit grant
  * GUI for the registration of Resource Servers and Client apps

- An OAuth2 demo Resource Server
  * In-memory JSON-based backend with limited functionality only to demo the OAuth Authorization Server

- An implementation add-on for connecting to a compliant SAML IdP for authentication

## Build / run Authorization Server
    mvn clean install
    java -cp oaaas-authorization-server/target/oaaas-*.jar org.surfnet.oaaas.boot.Application
Configuration resides in oaaas.yml.


## Resource Servers and Client apps registration
### GUI
The GUI for Resource Servers and Client apps registration can be found at:
[http://localhost:8080/adminClient/](http://localhost:8080/adminClient/)
### REST api
The following URLs are available for the registration interface:

    GET     /admin/resourceServer
    GET     /admin/resourceServer/{resourceServerId}.json
    PUT     /admin/resourceServer
    POST    /admin/resourceServer/{resourceServerId}.json
    DELETE  /admin/resourceServer/{resourceServerId}.json

    GET     /admin/client
    GET     /admin/client/{clientId}.json
    PUT     /admin/client
    POST    /admin/client/{clientId}.json
    DELETE  /admin/client/{clientId}.json
