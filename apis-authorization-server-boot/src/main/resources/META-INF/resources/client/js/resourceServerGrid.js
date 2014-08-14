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
      Template.get(templateId, function(template) {
        $(containerSelector).append(template({resourceServers: resourceServers}));
        $(containerSelector).css("height", ""); // clear the fixed height

        $("#addServerButton,#noServersAddOne").click(function() {
          windowController.onAddResourceServer();
        });

        $("a.editResourceServer").click(function(e) {
          var resourceServerId = $(e.target).closest("tr").attr("data-resourceServerId");
          windowController.onEditResourceServer(resourceServerId);
        });

        $("a.deleteServerButton").click(function(e) {
          var resourceServerId = $(e.target).closest("tr").attr("data-resourceServerId");
          bootbox.confirm("Are you sure you want to delete this Resource Server?", function (result) {
            if (result) {
              resourceServerGridController.onDelete(resourceServerId);
            }
          });
        });

        $('#resourceServerGrid input.copy-clipboard').tooltip({
          trigger: 'click',
          title: 'Press Ctrl/Cmd-C to copy'
        });
        $('#resourceServerGrid input.copy-clipboard').on('click', function() {
          $(this).select();
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

var resourceServerGridController = (function() {

  var view = resourceServerGridView;

  return {
    show: function(resourceServers) {
      // first hide to view to prevent multiple views displayed
      view.hide();
      view.show(resourceServers);
    },

    onDelete: function(resourceServerId) {
      data.deleteResourceServer(resourceServerId, function(data) {
        console.log("resource server has been deleted.");
        windowController.onDeleteResourceServer();
      }, function (errorMessage) {
        console.log("error while saving data: " + errorMessage);
        popoverBundle.showMessage("error", errorMessage, $("resourceServerGrid"));
      });
    },

    hide: view.hide,
    focus: view.focus,
    isVisible: view.isVisible
  }
})();