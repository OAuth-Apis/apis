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
 * A caching abstraction for Handlebars templates.
 *
 * Use as:
 *
 * var model = {"id":"123"};
 * Template.get("idOfTemplate")(model);
 *
 */
var Template = (function() {
  var tplCache = [];

  return {

  get: function(name) {
    if (!tplCache[name]) {
      tplCache[name] = Handlebars.compile($("#" + name).html());
    }
    return tplCache[name];
  }
  }
})();


/**
 * OAuth2 Implicit Grant client.
 *
 * Create with these options:
 * var auth = new OAuth({
 *   context: window,
 *   clientId: "myClientId",
 *   authorizationEndpoint: "http://localhost/oauth2/authorize"
 * });
 *
 * var accessToken = auth.authorize();
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
        + "&redirect_uri=" + context.location
  }

  function extractTokenInfo(hash) {
    var split = hash.split('&');

    var obj = {};
    for(var i = 0; i < split.length; i++){
      var kv = split[i].split('=');
      obj[kv[0]] = decodeURIComponent(kv[1] ? kv[1].replace(/\+/g, ' ') : kv[1]);
    }
    oauthTokenInfo = {
      accessToken: obj["access_token"],
      expires: obj["expires_in"],
      scope: obj["scope"]
    };
  }


  return {
    authorize: function() {
      if (/access_token=/.test(context.location.hash)) {
        extractTokenInfo(context.location.hash.substring(1));
        context.location.hash = "";
        return oauthTokenInfo.accessToken;
      } else {
        // redirect to authorization endpoint
        context.location = buildAuthorizationUrl();
      }
    }
  }
};

var accessToken = new OAuth({
  context:window,
  authorizationEndpoint:"http://localhost:8080/oauth2/authorize",
  clientId:"authorization-server-admin-js-client"
})
    .authorize();


var clients = $.ajax({
  url: "/admin/resourceServer",
  dataType: "json",
  beforeSend: function(xhr, settings) {
    xhr.setRequestHeader('Authorization', "bearer " + accessToken);
    xhr.setRequestHeader('Accept', "application/json");
  },
  success: function(data) {
    $("#clientsListTable").html(Template.get("clientTableTpl")({clients: data}));
}
});
