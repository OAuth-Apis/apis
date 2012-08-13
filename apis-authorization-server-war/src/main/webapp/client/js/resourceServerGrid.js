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

  $("#addServerButton,#noServersAddOne").click(function() {
    windowController.onAddResourceServer();
  });
  $("a.editResourceServer").click(function(e) {
    var resourceServerId = $(e.target).closest("tr").attr("data-resourceServerId");
    windowController.onEditResourceServer(resourceServerId);
  });

  return {

    refresh: function(resourceServers) {
      this.hide();
      this.show(resourceServers);
    },

    show: function(resourceServers) {
      $(containerSelector).append(Template.get(templateId)({resourceServers: resourceServers}));
    },

    hide: function() {
      $(handleSelector).remove();
    }
  }
})();

var resourceServerGridController = (function() {

  var view = resourceServerGridView;

  return {
    show: function() {
      // get list of resource servers. With this data, show the grid view.
      data.getResourceServers(function(data) {
        view.show(data);
      });
    },
    hide: function() {
      view.hide();
    }
  }
})();

