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



var resourceServerGridView = (function() {

  var templateId = "tplResourceServerGrid";
  var containerSelector = "#contentView";
  var handleSelector = "#resourceServerGrid";

  return {

    refresh: function(resourceServers) {
      this.hide();
      this.show(resourceServers);
    },

    show: function(resourceServers) {
      $(containerSelector).append(Template.get(templateId)({resourceServers: resourceServers}));
      $("#addServerButton,#noServersAddOne").click(function() {
        windowController.onAddResourceServer();
      });
    },

    hide: function() {
      $(handleSelector).remove();
    }
  }
})();

var resourceServerGridController = (function() {

  var view = resourceServerGridView;

  return {
    show: function() {
      // get list of resource servers. With this data, show the grid view.
      data.getResourceServers(function(data) {
        view.show(data);
      });
    },
    hide: function() {
      view.hide();
    }
  }
})();



var editResourceServerView = (function() {

  var templateId = "tplEditResourceServer";
  var containerSelector = "#contentView";
  var handleSelector = "#editResourceServer";

  return {
    show: function(mode) {
      $(containerSelector).append(Template.get(templateId)({
        formTitle: mode == "add"?"Add resource server" : "Edit resource server"
      }));

      $("#editResourceServerForm button.cancel").click(function() {
        resourceServerFormController.onCancel();
      });

      $("#editResourceServerForm").submit(function() {
        resourceServerFormController.onSubmit(this);
        return false; // prevent default submit
      });
    },
    hide: function() {
      $(handleSelector).remove();
    },

    showMessage: function(type, text) {
      if (type == "error")
      var html = Template.get("tplAlert")({
        title: type == "error" ? "Error" : "Notice",
        text: text
      });
      $("form#editResourceServerForm").prepend(html);
    }
  }
})();

var resourceServerFormController = (function() {

  var view = editResourceServerView;

  return {
    show: function(mode) {
      view.show(mode);
    },

    onSubmit: function(form) {
      var formAsObject = $(form).serializeObject();

      var resourceServer = {
        id: null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        scopes: formAsObject['scopes'],
        contactName: formAsObject['contactName'],
        contactEmail: formAsObject['contactEmail']
      };

      data.saveResourceServer(resourceServer, function(data) {
        console.log("resource server has been saved. Result from server: " + JSON.stringify(data));
        view.hide();
        windowController.onCloseEditResourceServer();
      }, function (errorMessage) {
        console.log("error while saving data: " + errorMessage);
        view.showMessage("error", errorMessage);
      });
    },
    onCancel: function() {
      view.hide();
      windowController.onCloseEditResourceServer();
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
  },

  refresh: function() {
    resourceServerGridController.show();
  },

  onCloseEditResourceServer: function() {
    resourceServerGridController.show();
  },

  onAddResourceServer: function() {
    resourceServerGridController.hide();
    resourceServerFormController.show("add");
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
  $(".alert").alert();

  // Initialisation of window controller.
  windowController.onPageLoad();
});