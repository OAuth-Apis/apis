requirejs.config({
  //By default load any module IDs from js/lib
  baseUrl: 'js',

  paths: {
    lib: "lib"
  },

  shim: {
    'lib/bootstrap.min': {
      //These script dependencies should be loaded before loading
      //backbone.js
      deps: ['lib/jquery']
      //Once loaded, use the global 'Bootstrap' as the
      //module value.
    },
    'data': {
      deps: ['lib/jquery']
    },
    'client': {
      deps: ['oauth', 'lib/jquery','lib/bootstrap.min', 'lib/handlebars', 'data']
    }
  }
});
require([
  "lib/jquery",
  "lib/handlebars",
  "lib/bootstrap.min",
  "oauth",
  "data",
  "client"
  ],
  function($){
    return $;
  }
);