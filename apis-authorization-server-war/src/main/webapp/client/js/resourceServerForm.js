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
    show: function(mode, resourceServer) {

      var model = resourceServer || {}; // empty in case of new one

      model.formTitle = (mode == "add")?"Add resource server" : "Edit resource server";
      $(containerSelector).append(Template.get(templateId)(model));
      $(containerSelector).css("height", ""); // clear the fixed height


      /*
       Scopes
       */
      // Remove attribute on click of delete-button (click on holder-div, delegated to button)
      $("tbody#currentScopes").on("click", "a.removeScope", function() {
        $(this).closest("tr").remove();
        return false;
      });
      // On click of the + button
      $("button#addScope").on("click", function() {
        // Save the state to the list of 'current' scopes
        $("tbody#currentScopes").append(Template.get("tplResourceServerScope")({
          scope: $("#newScopeField").val()
        }));

        // reset field for new value and focus.
        $("#newScopeField").val("").focus();
      });


      $("#editResourceServerForm button.cancel").click(function() {
        resourceServerFormController.onCancel();
      });

      $("#editResourceServerForm").submit(function() {
        resourceServerFormController.onSubmit(this);
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
      $("form#editResourceServerForm").prepend(html);
    }
  }
})();

var resourceServerFormController = (function() {

  var view = resourceServerFormView;

  /**
   * Accept the scopes from the form, transform them to a comma separated string, filtering out duplicates and empty elements.
   *
   * @param arrayOfScopes
   * @return {String}
   */
  var scopesToString = function(arrayOfScopes) {

    var scopes = [];

    if ($.isArray(arrayOfScopes)) {
      arrayOfScopes = $.unique(arrayOfScopes); // mind you, we rely on a modified version of $.unique, working for non-dom-elements
      for (var i=0; i < arrayOfScopes.length; i++) {
        var oneScope = arrayOfScopes[i];
        if (oneScope) { // skip empty items
          scopes.push(arrayOfScopes[i]);
        }
      }
    } else if (arrayOfScopes.length) {
      scopes.push(arrayOfScopes);
    } else {
      // not even one scope
    }

    return scopes.join(",");
  };

  return {
    onSubmit: function(form) {
      var formAsObject = $(form).serializeObject();

      var scopes = scopesToString(formAsObject['scopes']);

      var resourceServer = {
        id: (formAsObject['id'] > 0) ? formAsObject['id'] : null,
        name: formAsObject['name'],
        description: formAsObject['description'],
        scopes: scopes,
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

    show: function(mode, id) {
      if (mode == "edit") {
        data.getResourceServer(id, function(resourceServer){
          resourceServer.scopes = resourceServer.scopes ? resourceServer.scopes.split(",") : [];
          view.show(mode, resourceServer);
        });
      } else {
        view.show(mode);
      }
    },
    onCancel: function() {
      view.hide();
      windowController.onCloseEditResourceServer();
    }
  }
})();