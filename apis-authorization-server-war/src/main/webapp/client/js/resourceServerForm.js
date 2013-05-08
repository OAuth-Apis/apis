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
 * Accept values from a serialized form, transform them to a array, filtering out duplicates and empty elements.
 * It handles the special case of 1 item. Because in that case, the array isn't an array actually, but simple the item itself.
 *
 * @param arrayOfStrings
 * @return {String}
 */
var cleanFormArray = function(arrayOfStrings) {

  var cleanArray = [];

  if ($.isArray(arrayOfStrings)) {
    arrayOfStrings = $.unique(arrayOfStrings); // mind you, we rely on a modified version of $.unique, working for non-dom-elements
    for (var i=0; i < arrayOfStrings.length; i++) {
      var item = arrayOfStrings[i].trim();
      if (item) { // skip empty items
        cleanArray.push(arrayOfStrings[i]);
      }
    }
  } else if (arrayOfStrings && arrayOfStrings.length) { // Only one item
    cleanArray.push(arrayOfStrings);
  } else {
    // not even one item
  }

  return cleanArray;
};

var resourceServerFormView = (function() {

  var templateId = "tplEditResourceServer";
  var containerSelector = "#contentView";
  var handleSelector = "#editResourceServer";

  return {
    show: function(mode, resourceServer) {

      var model = resourceServer || {}; // empty in case of new one

      model.formTitle = (mode == "add")?"Add resource server" : "Edit resource server";
      Template.get(templateId, function(template) {
        $(containerSelector).append(template(model));
        $(containerSelector).css("height", ""); // clear the fixed height

        /*
         Scopes
         */
        // Remove attribute on click of delete-button (click on holder-div, delegated to button)
        $("div#currentScopes").on("click", "button.removeScope", function() {
          $(this).closest("div").remove();
          return false;
        });
        // On click of the + button
        $("button#addScope").on("click", function() {
          // Save the state to the list of 'current' scopes
          Template.get("tplResourceServerScope", function(template) {
            $("div#newScope").before(template({
              scope: $("#newScopeField").val()
            }));
            // reset field for new value and focus.
            $("#newScopeField").val("").focus();
          });
        });


        $("button.removeScope").one("click", function() { 
          if (model.id) {
            Template.get("tplDeleteScopeWarning", function(template) {
              $("#currentScopes").before(template());
            });
            return false; 
          }
          return true;
        });

        $("#editResourceServerForm button.cancel").click(function() {
          resourceServerFormController.onCancel();
        });

        $("#editResourceServerForm").submit(function() {
          resourceServerFormController.onSubmit(this);
          return false; // prevent default submit
        });
      });

    },
    hide: function() {
      $(containerSelector).css("height", $(containerSelector).height()); // set a fixed height to prevent wild swapping of the footer
      $(handleSelector).remove();
    }
  }
})();

var resourceServerFormController = (function() {

  var view = resourceServerFormView;

  return {
    onSubmit: function(form) {
      var formAsObject = $(form).serializeObject();

      var resourceServer = {
        id: (formAsObject['id'] > 0) ? formAsObject['id'] : null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        scopes: cleanFormArray(formAsObject['scopes']),
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
        popoverBundle.showMessage("error", errorMessage, $("form#editResourceServerForm"));
      });
    },

    show: function(mode, id) {
      if (mode == "edit") {
        data.getResourceServer(id, function(resourceServer){
          view.show(mode, resourceServer);
        });
      } else {
        view.show(mode);
      }
    },
    onCancel: function() {
      view.hide();
      windowController.onCloseEditResourceServer();
    },

    hide: view.hide
  }
})();