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



var resourceServersView = (function() {

  var templateId = "tplResourceServerTable";
  var domLocationSelector = "#placeholderResourceServerTable";

  return {

    refresh: function(resourceServers) {
      $(domLocationSelector).replaceWith(Template.get(templateId)({resourceServers: resourceServers}));

      $("#addServerButton").click(function() {
        windowController.onAddResourceServer();
      })
    }
  }
})();

var editResourceServerView = (function() {

  var templateId = "tplEditResourceServer";
  var domLocationSelector = "#placeholderEditResourceServer";

  return {
    show: function(mode) {
      $(domLocationSelector).replaceWith(Template.get(templateId)({
        formTitle: mode == "add"?"Add resource server" : "Edit resource server"
      }));

      $("div#editResourceServerView").show();
      $("form#editResourceServerForm").submit(windowController.onResourceServerSave);
    }
  }
})();


var windowController = {

  oauth: new OAuth({
    context:window,
    redirectUri: window.location, // Current location as redirect URI: after authorization we get back control.
    authorizationEndpoint:"/oauth2/authorize", // TODO: configurable?
    clientId:"authorization-server-admin-js-client"
  }),

  login: function() {
    // Start the authorization. Effectively we lose control, the browser will change location and come back later.
    this.oauth.authorize();
  },

  onLogin: function() {
    // Refresh the data grid.
    this.refresh();
  },

  refresh: function() {
    // get list of resource servers. With this data, refresh the grid view.
    data.getResourceServers(function(data) {
      resourceServersView.refresh(data);
    });
  },

  onAddResourceServer: function() {
    $("div#gridView").hide(); // TODO: delegate to view?
    editResourceServerView.show("add");
  },

  onResourceServerSave: function(form) {
    data.saveResourceServer($.serialize(form), function(data) {
      console.log("resource server has been saved. Result from server: " + data);
    });
    return false; // prevent default submit
  },

  onPageLoad: function() {
    if (this.oauth.isTokenPresent()) { // This will be true upon return from authentication step-out.

      // The URL-hash will contain the access token
      data.setAccessToken(this.oauth.extractTokenInfo());

      // Effectively we're logged in. We can do API calls now.
      this.onLogin();
    }

    $("a#loginbutton").click(function(){
      windowController.login();
      return false;
    });
  }
};

// On DOM ready
$(function() {
  // Initialisation of window controller.
  windowController.onPageLoad();
});