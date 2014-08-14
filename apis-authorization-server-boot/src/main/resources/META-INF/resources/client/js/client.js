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
 * Template.get(templateId, function(template) {
 *       $(containerSelector).append(template(model));
 *
 * });
 *
 */
var Template = (function() {
  var tplCache = [];

  return {
    /**
     * We support both inline templates as external templates 
     */
    get: function(templateName, callback) {
      if (!tplCache[templateName]) {
        var template = $("#" + templateName);
        if (template.size() == 0) {
          $.get("templates/" + templateName + ".html", function(data) {
            tplCache[templateName] = Handlebars.compile(data);
            callback(tplCache[templateName]);
          });
        } else {
          tplCache[templateName] = Handlebars.compile(template.html());
          callback(tplCache[templateName]);
        }
      } else {
        callback(tplCache[templateName]);
      }
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
      Template.get(templateId, function(template) {
        $(containerSelector).append(template());
        $("a#loginbutton").click(function(){
          windowController.login();
          return false;
        });
      });
    }
  }
})();


var windowController = {

  oauth: new OAuth({
    context:window,
    redirectUri: window.location, // Current location as redirect URI: after authorization we get back control.
    authorizationEndpoint:"../oauth2/authorize",
    clientId:"authorization-server-admin-js-client",
    scope:"read,write"
  }),

  login: function() {
    // Start the authorization. Effectively we lose control, the browser will change location and come back later.
    this.oauth.authorize();
  },

  onLanding: function() {
    landingView.show();
  },
  onLoggedIn: function() {
    // Refresh the data grid.
    this.refresh();

    $('.user-info').html(this.oauth.principalName());

    // On click of nav link, remove 'current' from all, add to actual current.
    $("div.side-nav a").click(function() {
      $("div.side-nav a").removeClass("cur");
      $(this).addClass("cur");
    });

    $("#nav-resource-servers").click(function() {
      windowController.refresh();
    });

    $("#nav-clients-apps").click(function() {
      /*
       * TODO scroll/animate to the start of the client section, but to be done after ajax calls
       */
      windowController.refresh();
    });

    $("#nav-access-tokens").click(function() {
      windowController.clearContentView();
      accessTokenGridController.show();
    });

    $("#nav-statistics").click(function() {
      windowController.clearContentView();
      statisticsGridController.show();
    });
  },

  clearContentView: function() {
    resourceServerFormController.hide();
    resourceServerGridController.hide();

    clientFormController.hide();
    clientGridController.hide();

    accessTokenGridController.hide();
    statisticsGridController.hide();
  },

  refresh: function() {
    this.clearContentView();
    data.getResourceServers(function(resourceServers) {
      resourceServerGridController.show(resourceServers);
      clientGridController.show(resourceServers);
    });
  },

  /**
   * Resource server events.
   */
  onCloseEditResourceServer: function() {
    this.refresh();
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

  onDeleteResourceServer: function() {
    this.refresh();
  },

  onDeleteAccessToken: function() {
    this.clearContentView();
    accessTokenGridController.show();
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
     this.refresh();
  },

  onDeleteClient: function() {
    this.refresh();
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
    selector: '[rel=popover]',
    //See popoverBundle.js
    title: function() {
      return popoverBundle.getTitle(this.attributes['name'].nodeValue);
    },
    content: function() {
      return popoverBundle.getContent(this.attributes['name'].nodeValue);
    }
  });

  // Initialization of window controller.
  windowController.onPageLoad();
});