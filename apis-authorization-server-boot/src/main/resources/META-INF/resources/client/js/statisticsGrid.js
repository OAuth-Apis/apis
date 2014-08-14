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

var statisticsGridView = (function() {

  var templateId = "tplStatisticsGrid";
  var containerSelector = "#contentView";
  var handleSelector = "#statisticsGrid";

  return {

    refresh: function(statistics) {
      this.hide();
      this.show(statistics);
    },

    show: function(statistics) {
      Template.get(templateId, function(template) {
        $(containerSelector).append(template(statistics));
        $(containerSelector).css("height", ""); // clear the fixed height
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

var statisticsGridController = (function() {

  var view = statisticsGridView;

  return {
    show: function() {
      // first hide to view to prevent multiple views displayed
      view.hide();
      data.getStatistics(function(statistics) {
        view.show(statistics);
      });
    },
    hide: view.hide,
    focus: view.focus,
    isVisible: view.isVisible
  }
})();

