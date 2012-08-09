/**
 * A caching abstraction for Handlebars templates.
 *
 * Use as:
 *
 * var model = {"id":"123"};
 * Template.get("idOfTemplate")(model);
 *
 */
define(['lib/handlebars', 'lib/jquery'], function(handlebars, $) {

  var tplCache = [];

  return {
    get: function(name) {
      if (!tplCache[name]) {
        tplCache[name] = handlebars.compile($("#" + name).html());
      }
      return tplCache[name];
    }
  }
});
