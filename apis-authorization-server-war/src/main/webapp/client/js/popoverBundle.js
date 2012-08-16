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


var popoverBundle = (function() {
  var bundle = {
    "resource-server-name"			    :	["Resource server name","The unique name of the Resource Server"],	  
    "resource-server-description"	  :	["Resource server description","The long description of the Resource Server"],
    "resource-server-key"           : ["Resource server key","The key is needed together with the secret to authenticate with the Authorization Server"],
    "resource-server-secret"        : ["Resource server secret","The secret is needed together with the key to authenticate with the Authorization Server"],
    "client-key"                    : ["Client key","The key is needed together with the secret to authenticate with the Authorization Server"],
    "client-secret"                 : ["Client secret","The secret is needed together with the key to authenticate with the Authorization Server"],
    "resource-server-scope"         : ["Resource server scopes","Scopes represent the different functionality your API offers to Client apps (e.g. read, update). Resource owners will have to grant access to the scopes of a client app which usually substitutes of a sub-set of the scopes of a resource server"],
    "resource-server-thumbNailUrl"  : ["Resource server thumbnail url","The thumbnail url is displayed on the consent screen and it can be used - as this client does - to aid in the visual representation of a resource server"],
    "resource-server-contactName"   : ["Resource server contact name","The name of the person who can be contacted in case of information and/ or trouble shooting"],
    "resource-server-contactEmail"  : ["Resource server contact email","The email address of the person who can be contacted in case of information and/ or trouble shooting"],
  }

  return {
    getTitle: function(name) {
      return bundle[name][0];
    },
    getContent: function(name) {
      return bundle[name][1];
    }
    

  }
})();