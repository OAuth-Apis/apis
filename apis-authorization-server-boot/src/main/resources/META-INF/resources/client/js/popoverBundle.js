/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var popoverBundle = (function () {
  var bundle = {
    "resource-server-name": ["Resource server name", "The unique name of the Resource Server"],
    "resource-server-description": ["Resource server description", "The long description of the Resource Server"],
    "resource-server-key": ["Resource server key", "The key is needed together with the secret to authenticate with the Authorization Server"],
    "resource-server-secret": ["Resource server secret", "The secret is needed together with the key to authenticate with the Authorization Server"],
    "client-key": ["Client key", "The key is needed together with the secret to authenticate with the Authorization Server"],
    "client-secret": ["Client secret", "The secret is needed together with the key to authenticate with the Authorization Server"],
    "resource-server-scope": ["Resource server scopes", "Scopes represent the different functionality your API offers to Client apps (e.g. read, update). Resource owners will have to grant access to the scopes of a client app which usually substitutes of a sub-set of the scopes of a resource server"],
    "resource-server-thumbNailUrl": ["Resource server thumbnail url", "The thumbnail url is displayed on the consent screen and it can be used - as this client does - to aid in the visual representation of a resource server"],
    "resource-server-contactName": ["Resource server contact name", "The name of the person who can be contacted in case of information and/ or trouble shooting"],
    "resource-server-contactEmail": ["Resource server contact email", "The email address of the person who can be contacted in case of information and/ or trouble shooting"],

    "client-name": ["Client app name", "The unique name of the Client application"],
    "client-description": ["Client app description", "The long description of the Client application"],
    "client-resource-server": ["Client app - Resource server", "Every client app belongs to one (and only one) resource server"],
    "client-scope": ["Client app scopes", "Scopes represent the different functionality the API of the resource server offers to Client apps (e.g. read, update). Resource owners will have to grant access to the scopes of a client app which usually substitutes of a sub-set (or all) of the scopes of a resource server"],
    "client-thumbNailUrl": ["Client app thumbnail url", "The thumbnail url is displayed on the consent screen and it can be used - as this client does - to aid in the visual representation of client applications"],
    "client-contactName": ["Client app contact name", "The name of the person who can be contacted in case of information and/ or trouble shooting"],
    "client-contactEmail": ["Client app contact email", "The email address of the person who can be contacted in case of information and/ or trouble shooting"],
    "client-expireDuration": ["Token expiration time", "The time (in seconds) an access token will be valid after being issued by the authorization server. Leave 0 for infinite validity"],
    "client-allowedImplicitGrant": ["Client allowed implicit grant", "If a Client is allowed implicit grant - e.g. is a JavaScript client - then it can leverage the implicit grant flow where no secret is used."],
    "client-allowedClientCredentials": ["Client allowed client credentials", "If a Client is allowed the credit credentials grant - e.g. is a highly trusted client - it will authenticate only with the key/secret and not with user authentication."],
    "client-useRefreshTokens": ["Client uses refresh tokens", "The client is issued (typically short-lived) a refresh token which is included when issuing an access token. Note that unlike access tokens, refresh tokens are intended for use only with authorization servers and are never sent to resource servers"],
    "client-redirectUri": ["Client app redirect uri's", "A client app has to provide a redirect uri at runtime when obtaining an access token. The provided redirect uri at runtime is checked against the configured redirect uri here. Although this is not a required field, we strongly advice to configure the redirect uri to prevent possible client frauds to tamper with the authorization server"],
    "client-attributes": ["Client app attributes", "A client may have additional attributes (key -value pairs) to configure extra info for the client app. The additional data can be used to add extra (OAuth) validation checks on the authorization server prior to granting a client app an access token and/ or enrichen the user consent form"]

  }

  return {
    getTitle: function (name) {
      return bundle[name][0];
    },
    getContent: function (name) {
      return bundle[name][1];
    },
    showMessage: function (type, text, $parent) {
      if (type == "error") {
        var violations;
        try {
          violations = JSON.parse(text).violations;
        }
        catch (e) {
          violations = [text];
        }
        $.each(violations, function (index, value) {
          Template.get("tplAlert", function (template) {
            $parent.prepend(template({
              title: type == "error" ? "Error" : "Notice",
              text: value
            }));
          });
        });
      }
    }
  }
})();



