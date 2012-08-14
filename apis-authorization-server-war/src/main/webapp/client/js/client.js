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

var landingView = (function() {

  var templateId = "tplLanding";
  var handleSelector = "#landing";
  var containerSelector = "#contentView";

  return {
    hide: function() {
      $(handleSelector).remove();
    },
    show: function() {
      $(containerSelector).append(Template.get(templateId)());
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

  onLanding: function() {
    landingView.show();
    $("a#loginbutton").click(function(){
      windowController.login();
      return false;
    });
  },
  onLoggedIn: function() {
    // Refresh the data grid.
    this.refresh();


    // On click of nav link, remove 'current' from all, add to actual current.
    $("div.side-nav a").click(function() {
      $("div.side-nav a").removeClass("cur");
      $(this).addClass("cur");
    })
  },

  refresh: function() {
    resourceServerGridController.show();
    clientGridController.show();
  },

  /**
   * Resource server events.
   */
  onCloseEditResourceServer: function() {
    resourceServerGridController.show();
    clientGridController.show();
  },
  onEditResourceServer: function(id) {
    resourceServerGridController.hide();
    clientGridController.hide();
    resourceServerFormController.show("edit", id);
  },
  onAddResourceServer: function() {
    resourceServerGridController.hide();
    clientGridController.hide();
    resourceServerFormController.show("add");
  },

  /**
   * Clients events.
   */
  onEditClient: function(resourceServerId, clientId) {
    resourceServerGridController.hide();
    clientGridController.hide();
    clientFormController.show("edit", resourceServerId, clientId);
  },
  onAddClient: function() {
    resourceServerGridController.hide();
    clientGridController.hide();
    clientFormController.show("add");
  },
  onCloseEditClient: function() {
    resourceServerGridController.show();
    clientGridController.show();
  },


  onPageLoad: function() {
    if (this.oauth.isTokenPresent()) { // This will be true upon return from authentication step-out.

      // The URL-hash will contain the access token
      data.setAccessToken(this.oauth.extractTokenInfo());

      // Effectively we're logged in. We can do API calls now.
      this.onLoggedIn();
    } else {
      this.onLanding();
    }
  }
};

// On DOM ready
$(function() {

  // Attach global listeners
  $(".alert").alert();

  $('body').tooltip({
    selector: '[rel=tooltip]'
  });
  $('body').popover({
    selector: '[rel=popover]'
  });

  // Initialisation of window controller.
  windowController.onPageLoad();
});