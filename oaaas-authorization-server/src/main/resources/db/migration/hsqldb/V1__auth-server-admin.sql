INSERT INTO resourceserver (id, contactEmail,  contactName, name, scopes, secret )
VALUES
	(99999, 'foo@university.org','foo.bar','university-foo','read','58b749f7-acb3-44b7-a38c-53d5ad740cf6');

INSERT INTO client (id, clientId, contactEmail, contactName, description, expireDuration, 
					name, redirectUris, scopes, secret, skipConsent, thumbNailUrl, 
					useRefreshTokens, resourceserver_id)
VALUES
    (99999, 'cool_app_id', 'client@coolapp.com', 'john.doe', 'Cool app for doing awesome things', 0,
    'cool-app', 'http://localhost:8080/redirect', 'read', 'secret', 0, 'http://www.surfnet.nl/SURFnet%20imagebank/Logos/SURFconext_klein.gif', 
    0, 99999);

INSERT INTO accesstoken (id, expires, principal, scopes, token, client_id)
VALUES
    (99999, 0, 'emma.blunt','read','74eccf5f-0995-4e1c-b08c-d05dd5a0f89b',99999);


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

INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99997, 'it-test@example.com', 'john.doe', 'it test client',
    'it test client', 'read,write',
    'thumbnailurl', 99997,
    'it-test-client', 'somesecret');

INSERT INTO accesstoken (id, expires, principal, scopes, token, client_id)
VALUES
    (99997, 0, 'it-test-enduser','read,write','00-11-22-33',99997);
