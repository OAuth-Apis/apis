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

var clientGridView = (function() {

  var templateId = "tplClientGrid";
  var containerSelector = "#contentView";
  var handleSelector = "#clientGrid";




  return {

    refresh: function(clients) {
      this.hide();
      this.show(clients);
    },

    show: function(clients) {

      $(containerSelector).append(Template.get(templateId)({clients: clients}));
      $(containerSelector).css("height", ""); // clear the fixed height

      $("#addClientButton,#noClientsAddOne").click(function() {
        windowController.onAddClient();
      });

      $("a.editClient").click(function(e) {
        var clientId = $(e.target).closest("tr").attr("data-clientId");
        windowController.onEditClient(clientId);
      });

    },

    isVisible: function() {
      return $(handleSelector).is(':visible');
    },
    hide: function() {
      $(containerSelector).css("height", $(containerSelector).height()); // set a fixed height to prevent wild swapping of the footer
      $(handleSelector).remove();
    },
    focus: function() {
      $(handleSelector).focus();
    }
  }
})();

var clientGridController = (function() {

  var view = clientGridView;

  return {
    show: function() {

      // get list of resource servers. With this data, query each of them for all their clients.
      data.getResourceServers(function(resourceServers) {

        var resourceServerIds = [];
        for (var i=0; i<resourceServers.length; i++) {
          resourceServerIds.push(resourceServers[i].id);
        }

        data.getClientsForResourceServers(resourceServerIds, function(data) {
          view.show(data);
        });
      });
    },

    hide: view.hide,
    focus: view.focus,
    isVisible: view.isVisible
  }
})();

