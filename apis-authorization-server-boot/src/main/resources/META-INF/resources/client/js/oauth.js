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

/**
 * OAuth2 Implicit Grant client.
 *
 * Create with these options:
 * var auth = new OAuth({
 *   context: window,
 *   clientId: "myClientId",
 *   redirectUri: "http://myredirecturi", // (optional)
 *   authorizationEndpoint: "http://localhost/oauth2/authorize"
 * });
 *
 * if (auth.isTokenPresent()) {
 *   accessToken = auth.extractTokenInfo();
 * } else {
 *   auth.authorize();
 * }
 *
 * @param opt
 * @return {Object}
 * @constructor
 */
var OAuth = function(opt) {
  var
    options = opt || {},
    context = options.context,
    oauthTokenInfo = {};

  function buildAuthorizationUrl() {
    return options.authorizationEndpoint
      + "?"
      + "response_type=token"
      + "&client_id=" + options.clientId
      + "&scope=" + options.scope
        // TODO: add scope
      + "&redirect_uri=" + options.redirectUri || context.location
  }



  return {
    authorize: function() {
      // redirect to authorization endpoint
      context.location = buildAuthorizationUrl();
    },

    isTokenPresent: function() {
      return /access_token=/.test(context.location.hash);
    },

    extractTokenInfo: function() {
      var hash = context.location.hash.substring(1);
      var split = hash.split('&');

      var obj = {};
      for(var i = 0; i < split.length; i++){
        var kv = split[i].split('=');
        obj[kv[0]] = decodeURIComponent(kv[1] ? kv[1].replace(/\+/g, ' ') : kv[1]);
      }
      oauthTokenInfo = {
        accessToken: obj["access_token"],
        expires: obj["expires_in"],
        scope: obj["scope"],
        principal: obj["principal"]
      };
      context.location.hash = "";
      return oauthTokenInfo.accessToken;
    },

    principalName: function() {
      return oauthTokenInfo.principal;
    }
  }
};

