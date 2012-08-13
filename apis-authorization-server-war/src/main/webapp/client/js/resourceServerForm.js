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


var resourceServerFormView = (function() {

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

  var view = resourceServerFormView;

  return {
    show: function(mode, id) {
      if (mode == "edit") {
        data.getResourceServer(id, function(resourceServer){
          view.show(mode, resourceServer);
        });
      }
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
        contactEmail: formAsObject['contactEmail'],
        thumbNailUrl: formAsObject['thumbNailUrl']
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