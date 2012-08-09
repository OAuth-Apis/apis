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


var data = (function() {
  var accessToken = null;


  var oauthAjax = function(options) {
    if (options.beforeSend) {
      var originalBeforeSend = options.beforeSend;
    }
    options.beforeSend = function(xhr, settings) {
      originalBeforeSend && originalBeforeSend(xhr, settings);
      xhr.setRequestHeader('Authorization', "bearer " + accessToken);
    };
    options.contentType = "application/json"; // we send json
    options.dataType = "json"; // we expect json back.

    return $.ajax(options);
  };

  return {
    setAccessToken: function(newAccessToken) {
      accessToken = newAccessToken;
    },

    saveResourceServer: function(resourceServer, success, failure) {
      // TODO: distinct between create and update
      oauthAjax({
        url:"/admin/resourceServer",
        data: JSON.stringify(resourceServer),
        type: "PUT",
        success: success,
        error: function(xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    },

    getResourceServer: function(id, resultHandler) {
      oauthAjax({
        url:"/admin/resourceServer/" + id,
        success: resultHandler,
        error: function() { // On failure, call result handler anyway, with empty result.
          resultHandler({});
        }
      });
    },

    getResourceServers:function (resultHandler) {
      oauthAjax({
        url:"/admin/resourceServer",
        success: resultHandler,
        error: function() { // On failure, call result handler anyway, with empty result.
          resultHandler([]);
        }
      });
    }
  }
})();