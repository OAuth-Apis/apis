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
    'jquery-extensions': {
      deps: ['lib/jquery']
    },
    'client': {
      deps: ['oauth', 'lib/jquery','jquery-extensions', 'lib/bootstrap.min', 'lib/handlebars', 'data']
    }
  }
});
require([
  "lib/jquery",
  "jquery-extensions",
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