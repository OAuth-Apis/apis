OpenConext authentication module for API Secure
================================

## Introduction
This module is an implementation of the authentication plugin architecture of API Secure.
Although it has been tested on SURFConext only, in theory it should work for any SAML 2.0 IdP.

## Configuration
Enable this plugin by configuring the correct authentication class in `apis.application.properties`

    authenticatorClass=org.surfnet.oaaas.conext.SAMLAuthenticator

Adapt the SAML properties in `surfconext.authn.properties` to your environment and put it on the classpath. An example of this file resides in apis-authorization-server-war/src/test/resources.:

    entityId=http://oaaas-dev
    assertionConsumerURI=http://localhost:8080/oauth2/authorize
    ssoUrl=https://engine.dev.surfconext.nl/authentication/idp/single-sign-on
    wayfCertificate=MIID/jCCAuYCCQCs7BsDR2N8tjANBgkqhkiG9w0BAQUFADCBwDELMAkGA1UEBhMCTkwxEDAOBgNVBAgTB1V0cmVjaHQxEDAOBgNVBAcTB1V0cmVjaHQxJTAjBgNVBAoTHFNVUkZuZXQgQlYgLSBUaGUgTmV0aGVybGFuZHMxHzAdBgNVBAsTFkNvbGxhYm9yYXRpb24gU2VydmljZXMxGDAWBgNVBAMTD1NVUkZjb25leHQtdGVzdDErMCkGCSqGSIb3DQEJARYcc3VyZmNvbmV4dC1iZWhlZXJAc3VyZm5ldC5ubDAeFw0xMTA2MjcxNTM0NDFaFw0yMTA2MjQxNTM0NDFaMIHAMQswCQYDVQQGEwJOTDEQMA4GA1UECBMHVXRyZWNodDEQMA4GA1UEBxMHVXRyZWNodDElMCMGA1UEChMcU1VSRm5ldCBCViAtIFRoZSBOZXRoZXJsYW5kczEfMB0GA1UECxMWQ29sbGFib3JhdGlvbiBTZXJ2aWNlczEYMBYGA1UEAxMPU1VSRmNvbmV4dC10ZXN0MSswKQYJKoZIhvcNAQkBFhxzdXJmY29uZXh0LWJlaGVlckBzdXJmbmV0Lm5sMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA27T+ALNpoU9PAvRYj2orOXaEVcy1fHSU/rZEakpgNzOFIEL9UB6BvtWdRbO5FT84yN+x2Qzpu6WLpwU7JFb36aRwPmBmabxUx95DhNQNFGA3ZkHu6DOA81GiG0Ll9p9S/EV2fmHGJdJjh5BP1v0/y7bJ/2JmvdzH+cFhEhFk0Ex9HNbC0Hmy9Sn8EXbg3RQO5/2e51wSv4uGALkyGM6lrOG/R1GenoAI8Ys7LNxj3NGkhKRUtpwHoIViWU5cOy26kG9VOG9bAVk51l0LNayMqyieX9UrCp1akQuP3Ir/ogtbKo2zg63Ho1cc43tHHdLZTHp2TWRRGEgnskvGZLddzwIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQB8eUq/0ArBGnPZHNn2o2ip+3i0U4r0swKXjuYL/o2VXqo3eI9j/bCvWw5NOLQVbk/Whc6dSeYIt1oVW/ND4iQZ7LHZr9814IOE3KLGIMeZgjPsXH15o9W4aLC0npPYilw96dfIAq49tOn44jhsRHrdR8z/NFPXE09ehAEb7fOIrxdlf7YDGYx+gXLEnsJo75E6LwCr/y/MBd13DDJNc1HUViiEz+Mkfo4FEKe/5HgEvDy2XjuE1juDCqJ/07pBPHBd0DtM7uaxGw+Zt/Fc4uE0NvtlCZqShFUvMmqHL5oENOlfTmBSWJMbBAGs2O/JQirG2aYcULqXYoCGIPUF49Z6
    wayfUrlMetadata=https://engine.dev.surfconext.nl/authentication/idp/metadata

## Modifying behaviour
To modify behaviour of this plugin, extend (one of) the following classes and wire them accordingly.

### SAMLAuthenticator
This class is the main entry point and the easiest to extend.
Refer to your subclass in apis.application.properties with the property `authenticatorClass`
### SAMLProvisioner
This class is instantiated by `SAMLAuthenticator.createProvisioner()`. So extend SAMLAuthenticator (see above) and override this method.
### OpenSAMLContext
This class is instantiated by `SAMLAuthenticator.createOpenSAMLContext(Properties, SAMLProvisioner)`. So extend SAMLAuthenticator (see above) and override this method.
