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


var clientFormView = (function() {

  var templateId = "tplEditClient";
  var containerSelector = "#contentView";
  var handleSelector = "#editClient";

  return {
    show: function(mode, client) {

      var model = client || {}; // empty in case of new one

      model.formTitle = (mode == "add")?"Add client app" : "Edit client app";
      $(containerSelector).append(Template.get(templateId)(model));
      $(containerSelector).css("height", ""); // clear the fixed height

      $("#editClientForm button.cancel").click(function() {
        clientFormController.onCancel();
      });

      $("#editClientForm").submit(function() {
        clientFormController.onSubmit(this);
        return false; // prevent default submit
      });
    },
    hide: function() {
      $(containerSelector).css("height", $(containerSelector).height()); // set a fixed height to prevent wild swapping of the footer
      $(handleSelector).remove();
    },

    showMessage: function(type, text) {
      if (type == "error")
        var html = Template.get("tplAlert")({
          title: type == "error" ? "Error" : "Notice",
          text: text
        });
      $("form#editClientForm").prepend(html);
    }
  }
})();

var clientFormController = (function() {

  var view = clientFormView;

  return {
    show: function(mode, id) {

      if (mode == "edit") {
        // retrieve current data for this client.
        data.getClient(id, function(client){
          view.show(mode, client);
        });
      } else {
        // retrieve possible resource servers to put this new client under.
        data.getResourceServers(function(resourceServers) {
          var model = {};
          model.availableResourceServers = resourceServers;
          view.show(mode, model);
        });
      }
    },

    onSubmit: function(form) {
      var formAsObject = $(form).serializeObject();

      var client = {
        id: (formAsObject['id'] > 0) ? formAsObject['id'] : null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        clientId: formAsObject['clientId'],
        scopes: formAsObject['scopes'],
        contactName: formAsObject['contactName'],
        contactEmail: formAsObject['contactEmail'],
        thumbNailUrl: formAsObject['thumbNailUrl']
      };

      data.saveClient(formAsObject['resourceServerId'], client, function(data) {
        console.log("client has been saved. Result from server: " + JSON.stringify(data));
        view.hide();
        windowController.onCloseEditClient();
      }, function (errorMessage) {
        console.log("error while saving data: " + errorMessage);
        view.showMessage("error", errorMessage);
      });
    },
    onCancel: function() {
      view.hide();
      windowController.onCloseEditClient();
    }
  }
})();