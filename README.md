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
  * In-memory JSON-based backend with limited functionality other then to demo the OAuth Authorization Server

- An implementation add-on for connecting to a compliant SAML IdP for authentication