var data = (function() {
  var accessToken = null;


  var oauthAjax = function(options) {
    if (options.beforeSend) {
      var originalBeforeSend = options.beforeSend;
    }
    options.beforeSend = function(xhr, settings) {
      originalBeforeSend && originalBeforeSend(xhr, settings);
      xhr.setRequestHeader('Accept', "application/json");
      xhr.setRequestHeader('Authorization', "bearer " + accessToken);
    };
    options.dataType = "json";

    return $.ajax(options);
  };

  return {
    setAccessToken: function(newAccessToken) {
      accessToken = newAccessToken;
    },

    saveResourceServer: function(resourceServer, resultHandler) {
      // TODO: distinct between create and update
      oauthAjax({
        url:"/admin/resourceServer",
        data: resourceServer,
        success: resultHandler,
        error: function() {
          resultHandler(); // failure: result handler with empty result. TODO: log?
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