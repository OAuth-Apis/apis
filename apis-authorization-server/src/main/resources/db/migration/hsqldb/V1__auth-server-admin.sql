INSERT INTO resourceserver (id, contactEmail,  contactName, name, key, scopes, secret )
VALUES
	(99999, 'foo@university.org','foo.bar','university-foo','university-foo','read','58b749f7-acb3-44b7-a38c-53d5ad740cf6');

INSERT INTO client (id, clientId, contactEmail, contactName, description, expireDuration, 
					name, redirectUris, scopes, secret, skipConsent, thumbNailUrl, 
					useRefreshTokens, resourceserver_id)
VALUES
    (99999, 'cool_app_id', 'client@coolapp.com', 'john.doe', 'Cool app for doing awesome things', 0,
    'cool-app', 'http://localhost:8084/redirect', 'read', 'secret', 0, 'http://www.surfnet.nl/SURFnet%20imagebank/Logos/SURFconext_klein.gif', 
    0, 99999);
/*
emma.blunt
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, scopes, token, client_id, resourceOwnerId)
VALUES
    (99999, 0, 'rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AAplbW1hLmJsdW50c3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAnQABHVzZXJ0AAVhZG1pbg==',
	'read','74eccf5f-0995-4e1c-b08c-d05dd5a0f89b',99999, 'emma.blunt');

INSERT INTO client_attributes(client_id , attribute_name, attribute_value) values
	(99999, 'university','foo-university');

/*
Local administration application
 */
INSERT INTO resourceserver (id, contactEmail,  contactName, name, key, secret, scopes, owner)
VALUES
	(99998, 'localadmin@example.com','local admin','authorization-server-admin',
	'authorization-server-admin', 'cafebabe-cafe-babe-cafe-babecafebabe', 'read', null);

INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99998, 'client@coolapp.com', 'john.doe', 'Javascript application for authorization server administration',
    'authorization server admin js client', 'read,write',
    'http://www.surfnet.nl/SURFnet%20imagebank/Logos/SURFconext_klein.gif', 99998,
    'authorization-server-admin-js-client', '');

/*
For integration tests
*/
INSERT INTO resourceserver (id, contactEmail,  contactName, name, key, secret, scopes, owner)
VALUES
	(99997, 'it-test@example.com','it test','it-test-resource-server',
	'it-test-resource-server', 'somesecret', 'read', 'it-test-enduser');

/*
Client not getting refresh tokens
*/
INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99997, 'it-test@example.com', 'john.doe', 'it test client',
    'it test client', 'read,write',
    'thumbnailurl', 99997,
    'it-test-client', 'somesecret');


/*
Client getting refresh tokens (and skips consent)
*/
INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id,
clientId, secret, skipConsent, expireDuration, useRefreshTokens, notAllowedImplicitGrant)
VALUES
    (99996, 'it-test@example.com', 'john.doe', 'it test client no consent use refresh',
    'it test client 2', 'read,write',
    'thumbnailurl', 99997,
    'it-test-client-no-consent-refresh', 'somesecret2', 1, 3600, 1, 1);

/*
Client for implicit grant
*/
INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99995, 'it-test-grant@example.com', 'john.grant', 'it test client grant',
    'it test client grant', 'read,write',
    'thumbnailurl', 99997,
    'it-test-client-grant', 'somesecret-grant');
    
/*
admin-enduser
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, scopes, token, client_id, resourceOwnerId)
VALUES (99998, 0, 'rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AA1hZG1pbi1lbmR1c2Vyc3IAGmphdmEudXRpbC5BcnJheXMkQXJyYXlMaXN02aQ8vs2IBtICAAFbAAFhdAATW0xqYXZhL2xhbmcvT2JqZWN0O3hwdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAnQABHVzZXJ0AAVhZG1pbg==',
'read,write','dad30fb8-ad90-4f24-af99-798bb71d27c8',99998, 'admin-enduser');
/*
it-test-enduser 
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, scopes, token, client_id, resourceOwnerId)
VALUES
    (99997, 0, 'rO0ABXNyADdvcmcuc3VyZm5ldC5vYWFhcy5hdXRoLnByaW5jaXBhbC5BdXRoZW50aWNhdGVkUHJpbmNpcGFsAAAAAAAAAAECAANMAAphdHRyaWJ1dGVzdAAPTGphdmEvdXRpbC9NYXA7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABXJvbGVzdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAHmphdmEudXRpbC5Db2xsZWN0aW9ucyRFbXB0eU1hcFk2FIVa3OfQAgAAeHB0AA9pdC10ZXN0LWVuZHVzZXJzcgAaamF2YS51dGlsLkFycmF5cyRBcnJheUxpc3TZpDy+zYgG0gIAAVsAAWF0ABNbTGphdmEvbGFuZy9PYmplY3Q7eHB1cgATW0xqYXZhLmxhbmcuU3RyaW5nO63SVufpHXtHAgAAeHAAAAACdAAEdXNlcnQABWFkbWlu',
'read,write','00-11-22-33',99997, 'it-test-enduser ');
