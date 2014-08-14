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


var data = (function () {
  var accessToken = null;


  var oauthAjax = function (options) {
    if (options.beforeSend) {
      var originalBeforeSend = options.beforeSend;
    }
    options.beforeSend = function (xhr, settings) {
      originalBeforeSend && originalBeforeSend(xhr, settings);
      xhr.setRequestHeader('Authorization', "bearer " + accessToken);
    };
    options.contentType = "application/json"; // we send json
    options.dataType = "json"; // we expect json back.
    if (options.error) {
      var originalError = options.error;
    }
    options.error = function (xhr, textStatus, errorThrown) {
      //token expired or invalid
      if (xhr.status == 403) {
        windowController.login();
      } else if (xhr.status == 0) {
        bootbox.alert("Currently there is no communication possible with the Authorization Server",
          "Close",
          function (result) {
            bootbox.hideAll();
          });
      }
      if (originalError != undefined) {
        originalError(xhr, textStatus, errorThrown);
      }
    };

    return $.ajax(options);
  };

  return {
    setAccessToken: function (newAccessToken) {
      accessToken = newAccessToken;
    },

    saveResourceServer: function (resourceServer, success, failure) {

      var httpMethod, url;
      if (resourceServer.id) {
        httpMethod = "POST";
        url = "../admin/resourceServer/" + resourceServer.id;
      } else {
        httpMethod = "PUT";
        url = "../admin/resourceServer";

      }

      oauthAjax({
        url: url,
        data: JSON.stringify(resourceServer),
        type: httpMethod,
        success: success,
        error: function (xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    },

    getResourceServer: function (id, resultHandler) {
      oauthAjax({
        url: "../admin/resourceServer/" + id,
        success: resultHandler,
        error: function (xhr, textStatus, errorThrown) { // On failure, call result handler anyway, with empty result.
          resultHandler({});
        }
      });
    },
    getResourceServers: function (resultHandler) {
      oauthAjax({
        url: "../admin/resourceServer",
        success: resultHandler,
        error: function (xhr, textStatus, errorThrown) { // On failure, call result handler anyway, with empty result.
          resultHandler({});
        }
      });
    },
    getStatistics: function (resultHandler) {
      oauthAjax({
        url: "../admin/resourceServer/stats",
        success: resultHandler,
        error: function () { // On failure, call result handler anyway, with empty result.
          resultHandler([]);
        }
      });
    },

    deleteResourceServer: function (resourceServerId, success, failure) {
      var httpMethod, url;
      httpMethod = "DELETE";
      url = "../admin/resourceServer/" + resourceServerId;
      oauthAjax({
        url: url,
        type: httpMethod,
        success: success,
        error: function (xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    },

    /**
     * Get all clients for all given resource servers.
     * Due to the way the REST service is setup, we cannot query for all clients overall but only for a specific resource server.
     * Here we query each resource servers' clients synchronously and concatenate the results.
     */
    getClientsForResourceServers: function (resourceServerIds, resultHandler) {
      var resultData = [];
      var receivedResponses = 0;
      for (var i = 0; i < resourceServerIds.length; i++) {
        var resourceServerId = resourceServerIds[i];
        oauthAjax({
          url: "../admin/resourceServer/" + resourceServerId + "/client",
          async: false,
          success: function (data) {
            // set the resourceServerId on the client
            for (var i = 0; i < data.length; i++) {
              data[i].resourceServerId = resourceServerId;
            }
            resultData = resultData.concat(data);
          },
          error: function (jqXHR, textStatus, errorThrown) {
            console.log("error: " + textStatus);
          },
          complete: function () {
            receivedResponses++;
            if (receivedResponses == resourceServerIds.length) {
              resultHandler(resultData);
            }
          }
        });
      }
    },
    getClients: function (resourceServerId, resultHandler) {
      oauthAjax({
        url: "../admin/resourceServer/" + resourceServerId + "/client",
        success: function (data) {
          for (var i = 0; i < data.length; i++) {
            // Put the resourceServerId in the client, we do not get it back from the request.
            data[i].resourceServerId = resourceServerId;
          }
          resultHandler(data);
        },
        error: function () { // On failure, call result handler anyway, with empty result.
          resultHandler([]);
        }
      });
    },
    getClient: function (resourceServerId, clientId, resultHandler) {
      this.getResourceServer(resourceServerId, function (resourceServer) {
        oauthAjax({
          url: "../admin/resourceServer/" + resourceServerId + "/client/" + clientId,
          success: function (client) {
            // Put the resourceServer in the client, we do not get it back from the request.
            client.resourceServer = resourceServer;
            resultHandler(client);
          },
          error: function () { // On failure, call result handler anyway, with empty result.
            resultHandler({});
          }
        });
      });
    },

    saveClient: function (resourceServerId, client, success, failure) {

      var httpMethod, url;
      if (client.id) {
        httpMethod = "POST";
        url = "../admin/resourceServer/" + resourceServerId + "/client/" + client.id;
      } else {
        httpMethod = "PUT";
        url = "../admin/resourceServer/" + resourceServerId + "/client";
      }
      oauthAjax({
        url: url,
        data: JSON.stringify(client),
        type: httpMethod,
        success: success,
        error: function (xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    },
    deleteClient: function (resourceServerId, clientId, success, failure) {

      var httpMethod, url;
      httpMethod = "DELETE";
      url = "../admin/resourceServer/" + resourceServerId + "/client/" + clientId;
      oauthAjax({
        url: url,
        type: httpMethod,
        success: success,
        error: function (xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    },

    /**
     * Access token REST
     */
    getAccessToken: function (id, resultHandler) {
      oauthAjax({
        url: "../admin/accessToken/" + id,
        success: resultHandler,
        error: function () { // On failure, call result handler anyway, with empty result.
          resultHandler({});
        }
      });
    },
    getAccessTokens: function (resultHandler) {
      oauthAjax({
        url: "../admin/accessToken",
        success: resultHandler,
        error: function () { // On failure, call result handler anyway, with empty result.
          resultHandler([]);
        }
      });
    },
    deleteAccessToken: function (accessTokenId, success, failure) {

      var httpMethod, url;
      httpMethod = "DELETE";
      url = "../admin/accessToken/" + accessTokenId;
      oauthAjax({
        url: url,
        type: httpMethod,
        success: success,
        error: function (xhr, textStatus, errorThrown) {
          failure(xhr.responseText);
        }
      });
    }

  }
})();