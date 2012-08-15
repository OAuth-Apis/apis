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


      /*
       Attributes
      */
      // Remove attribute on click of delete-button (click on holder-div, delegated to button)
      $("div#attributesHolder").on("click", "button.removeAttribute", function() {
        $(this).closest("div").remove();
      });

      // On click of the + button
      $("button.addAttribute").on("click", function() {

        // Save the state to the list of 'current' attributes
        $("div#newAttribute").before(Template.get("tplClientAttribute")({
          attributeName: $("#newAttributeName").val(),
          attributeValue: $("#newAttributeValue").val()
        }));

        // reset fields for new values and focus
        $("#newAttributeName").val("").focus();
        $("#newAttributeValue").val("");
      });

      /*
       Redirect URIs
     */
      // Remove attribute on click of delete-button (click on holder-div, delegated to button)
      $("div#redirectUrisHolder").on("click", "button.removeRedirectUri", function() {
        $(this).closest("div").remove();
      });
      // On click of the + button
      $("button.addRedirectUri").on("click", function() {

        // Save the state to the list of 'current' attributes
        $("div#newRedirectUri").before(Template.get("tplClientRedirectUri")({
          uri: $("#newRedirectUriField").val()
        }));

        // reset field for new value and focus.
        $("#newRedirectUriField").val("").focus();
      });


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
    show: function(mode, resourceServerId, clientId) {

      if (mode == "edit") {
        // retrieve current data for this client.
        data.getClient(resourceServerId, clientId, function(client){

          // See the comment in the onSubmit about transformation of attributeName/value and back again.
          var rewrittenAttributes = [];
          for (var attributeName in client.attributes) {
            if (client.attributes.hasOwnProperty(attributeName)) {
              rewrittenAttributes.push({
                "attributeName": attributeName,
                "attributeValue": client.attributes[attributeName]
              });
            }
          }
          client.attributes = rewrittenAttributes;

          var redirecturisAsList = (client.redirectUris) ? client.redirectUris.split("\n") : [];
          client.redirectUris = [];
          $.each(redirecturisAsList, function(index, value) {
            client.redirectUris.push({"uri": value});
          });
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

      /*
      Attributes are submitted in the form:
      {
        "attributeName": ['name1', 'name2', 'name3'],
        "attributeValue": ['val1', 'val2', 'val3']
      }
      But we want to post them in the form:
      attributes {"name1": "val1", "name2": "val2"}
       */
      var attributes = {};
      for (var i = 0; i < formAsObject['attributeName'].length; i++) {
        if (formAsObject['attributeName'][i]) { // skip empty names
          attributes[formAsObject['attributeName'][i]] = formAsObject['attributeValue'][i];
        }
      }

      var client = {
        id: (formAsObject['id'] > 0) ? formAsObject['id'] : null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        clientId: formAsObject['clientId'],
        scopes: formAsObject['scopes'],
        contactName: formAsObject['contactName'],
        contactEmail: formAsObject['contactEmail'],
        thumbNailUrl: formAsObject['thumbNailUrl'],
        attributes: attributes,
        redirectUris:$.isArray(formAsObject['redirectUri']) ? formAsObject['redirectUri'].join("\n") : formAsObject['redirectUri']
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