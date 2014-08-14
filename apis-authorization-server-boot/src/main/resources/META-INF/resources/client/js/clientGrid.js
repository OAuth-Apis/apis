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
      Template.get(templateId, function(template) {
        $(containerSelector).append(template({clients: clients}));
        $(containerSelector).css("height", ""); // clear the fixed height

        $("#addClientButton,#noClientsAddOne").click(function() {
          windowController.onAddClient();
        });

        $("a.editClient").click(function(e) {
          var resourceServerId = $(e.target).closest("tr").attr("data-resourceServerId");
          var clientId = $(e.target).closest("tr").attr("data-clientId");
          windowController.onEditClient(resourceServerId, clientId);
        });
        
        $("a.deleteClientButton").click(function(e) {
          var resourceServerId = $(e.target).closest("tr").attr("data-resourceServerId");
          var clientId = $(e.target).closest("tr").attr("data-clientId");
          bootbox.confirm("Are you sure you want to delete this Client?", function (result) {
            if (result) {
              clientGridController.onDelete(resourceServerId, clientId);
            }
          });
        });

        $('#clientGrid input.copy-clipboard').tooltip({
          trigger: 'click',
          title: 'Press Ctrl/Cmd-C to copy'
        });
        $('#clientGrid input.copy-clipboard').on('click', function() {
          $(this).select();
        });

        /*
         * We need to populate the client column in the Resource Server grid
         */
        $(clients).each(function(i, client) {
          var $clientLink = $("<a href='#'>" + client.name + "<a><br>").click(function(e){
            windowController.onEditClient(client.resourceServer.id, client.id);
          })
          $('#resource_server_clients_' + client.resourceServer.id).prepend($clientLink) ;
        });

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
    show: function(resourceServers) {
      // first hide to view to prevent multiple views displayed
      view.hide();
      
      // with the resourceServers, query each of them for all their clients.

      var resourceServersByIds = {};
      $(resourceServers).each(function(i, resourceServer) {
        resourceServersByIds[resourceServer.id] = resourceServer;
      });

      data.getClientsForResourceServers(Object.keys(resourceServersByIds), function(data) {
        $(data).each(function(i, client) {
          client.resourceServer = resourceServersByIds[client.resourceServerId];
        });
        view.show(data);
      });
    },
    
    onDelete: function(resourceServerId, clientId) {
      data.deleteClient(resourceServerId, clientId, function(data) {
        console.log("client has been deleted.");
        windowController.onDeleteClient();
      }, function (errorMessage) {
        console.log("error while saving data: " + errorMessage);
        popoverBundle.showMessage("error", errorMessage, $("#clientGrid"));
      });
    },


    hide: view.hide,
    focus: view.focus,
    isVisible: view.isVisible
  }
})();

