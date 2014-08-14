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
      Template.get(templateId, function(template) {
        $(containerSelector).append(template(model));
        $(containerSelector).css("height", ""); // clear the fixed height

        $("select#clientResourceServer").on("change", function() {
          clientFormController.onChangeResourceServer($("select#clientResourceServer option:selected").val());
        });

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
          Template.get("tplClientAttribute", function(template) {
            $("div#newAttribute").before(template({
              attributeName: $("#newAttributeName").val(),
              attributeValue: $("#newAttributeValue").val()
            }));
            // reset fields for new values and focus
            $("#newAttributeName").val("").focus();
            $("#newAttributeValue").val("");
          });
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
          Template.get("tplClientRedirectUri", function(template) {
            $("div#newRedirectUri").before(template({
              uri: $("#newRedirectUriField").val()
            }));
            // reset field for new value and focus.
            $("#newRedirectUriField").val("").focus();
          });
        });

        $("input[name='allowedImplicitGrant']").change(function(){
          $("#implicit_grant_warning").fadeToggle($(this).is(':checked'));
        });

        $("input[name='allowedClientCredentials']").change(function(){
          $("#client_credentials_warning").fadeToggle($(this).is(':checked'));
        });

        if (mode == "add") {
          // Trigger the onchange beforehand for new clients, to populate the scopes list for the first time.
          clientFormController.onChangeResourceServer($("select#clientResourceServer option:selected").val());
        }

        $("#editClientForm button.cancel").click(function() {
          clientFormController.onCancel();
        });

        $("#editClientForm").submit(function() {
          clientFormController.onSubmit(this);
          return false; // prevent default submit
        });

      });
    },

    updateAvailableScopes: function(scopes) {
      // Remove current scopes
      $("div#clientScopesHolder label").remove();

      // TODO: move to template?
      // Add new ones
      $.each(scopes, function(index, scope) {
        $("<label />")
            .addClass("checkbox")
            .append($('<input type="checkbox" name="scopes"/>')
            .val(scope))
            .append(scope)
            .appendTo("#clientScopesHolder");
      });
    },

    hide: function() {
      $(containerSelector).css("height", $(containerSelector).height()); // set a fixed height to prevent wild swapping of the footer
      $(handleSelector).remove();
    }
  }
})();

var clientFormController = (function() {

  var view = clientFormView;

  var formArrayAttributesToHash = function (names, values) {
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
    if ($.isArray(names)) {
      for (var i = 0; i < names.length; i++) {
        if (names[i]) { // skip empty names
          attributes[names[i]] = values[i];
        }
      }
    } else if (names) {
      /*
       Of special interest is the case where only 1 name and 1 value are given.
       Then names is not an array but a string. (same goes for 'values')
       */
      attributes[names] = values;
    } else {
      // No attribute at all
    }
    return attributes;
  };

  return {
    show: function(mode, resourceServerId, clientId) {


      if (mode == "edit") {
        // retrieve current data for this client.
        data.getClient(resourceServerId, clientId, function(client){

          client.availableScopes = [];
          for (var i=0; i< client.resourceServer.scopes.length; i++) {
            var scope = client.resourceServer.scopes[i];
            client.availableScopes.push({
              scopeName: scope,
              currentlySelected: ($.inArray(scope, client.scopes)>-1) ? 1 : 0
            });
          }


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

    onChangeResourceServer: function(resourceServerId) {
      data.getResourceServer(resourceServerId, function(resourceServer) {
        view.updateAvailableScopes(resourceServer.scopes);
      });
    },

    onSubmit: function(form) {
      var formAsObject = $(form).serializeObject();

      var attributes = formArrayAttributesToHash(formAsObject['attributeName'], formAsObject['attributeValue']);

      // If one redirecturi is submitted, the field is a simple string instead of an array.
      var redirectUris = $.isArray(formAsObject['redirectUris']) ? formAsObject['redirectUris'] : [formAsObject['redirectUris']];
      // Trim whitespace, remove empty strings
      redirectUris = $.map(redirectUris, function (item, index) {
        item = item.trim();
        if (item.length == 0) {
          return null;
        } else {
          return item;
        }
      });

      var client = {
        id: (formAsObject['id'] > 0) ? formAsObject['id'] : null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        clientId: formAsObject['clientId'],
        scopes: cleanFormArray(formAsObject['scopes']),
        contactName: formAsObject['contactName'],
        contactEmail: formAsObject['contactEmail'],
        thumbNailUrl: formAsObject['thumbNailUrl'],
        useRefreshTokens: formAsObject['useRefreshTokens'],
        allowedImplicitGrant: formAsObject['allowedImplicitGrant'],
        allowedClientCredentials: formAsObject['allowedClientCredentials'],
        expireDuration: formAsObject['expireDuration'],
        attributes: attributes,
        redirectUris: cleanFormArray(formAsObject['redirectUris'])
      };

      data.saveClient(formAsObject['resourceServerId'], client, function(data) {
        console.log("client has been saved. Result from server: " + JSON.stringify(data));
        view.hide();
        windowController.onCloseEditClient();
      }, function (errorMessage) {
        console.log("error while saving data: " + errorMessage);
        popoverBundle.showMessage("error", errorMessage, $("form#editClientForm"));
      });
    },
    onCancel: function() {
      view.hide();
      windowController.onCloseEditClient();
    },
    
    hide: view.hide
  }
})();