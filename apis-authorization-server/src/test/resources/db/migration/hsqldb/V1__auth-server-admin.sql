INSERT INTO resourceserver (id, contactEmail,  contactName, resourceServerName, resourceServerKey,secret )
VALUES
	(99999, 'foo@university.org','foo.bar','university-foo','university-foo',
	'58b749f7-acb3-44b7-a38c-53d5ad740cf6');
INSERT INTO Resourceserver_scopes values (99999, 'read');

INSERT INTO client (id, clientId, contactEmail, contactName, description, expireDuration, 
					clientName, secret, skipConsent, thumbNailUrl,
					useRefreshTokens, resourceserver_id)
VALUES
    (99999, 'cool_app_id', 'client@coolapp.com', 'john.doe', 'Cool app for doing awesome things', 0,
    'cool-app', 'secret', 0, 'http://www.surfnet.nl/SURFnet%20imagebank/Logos/SURFconext_klein.gif',
    0, 99999);
INSERT INTO Client_scopes values (99999, 'read');
INSERT INTO Client_redirectUris values (99999, 'http://localhost:8084/redirect');


/*
emma.blunt
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, token, client_id, resourceOwnerId)
VALUES
    (99999, 0, '{\"name\":\"emma.blunt\",\"roles\":[\"user\",\"admin\"],\"attributes\":{}}',
	'74eccf5f-0995-4e1c-b08c-d05dd5a0f89b',99999, 'emma.blunt');
INSERT INTO Accesstoken_scopes values (99999, 'read');

INSERT INTO client_attributes(client_id , attribute_name, attribute_value) values
	(99999, 'university','foo-university');

/*
Local administration application
 */
INSERT INTO resourceserver (id, contactEmail,  contactName, resourceServerName, resourceServerKey, secret, owner, thumbNailUrl)
VALUES
	(99998, 'localadmin@example.com','local admin','Authorization Server Apis',
	'authorization-server-admin', 'cafebabe-cafe-babe-cafe-babecafebabe', null, 'https://raw.github.com/OpenConextApps/apis/master/apis-images/surf-oauth.png');
INSERT INTO ResourceServer_scopes values (99998, 'read'),(99998, 'write') ;

INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99998, 'client@coolapp.com', 'john.doe', 'Javascript application for authorization server administration',
    'Authorization Server Admin Client',
    'https://raw.github.com/OpenConextApps/apis/master/apis-images/surf-oauth-client.png', 99998,
    'authorization-server-admin-js-client', '');
INSERT INTO Client_scopes values (99998, 'read'), (99998, 'write');

/*
For integration tests
*/
INSERT INTO resourceserver (id, contactEmail,  contactName, resourceServerName, resourceServerKey, secret, owner)
VALUES
	(99997, 'it-test@example.com','it test','it-test-resource-server',
	'it-test-resource-server', 'somesecret', 'it-test-enduser');
INSERT INTO Resourceserver_scopes values (99997, 'read');

/*
Client not getting refresh tokens
*/
INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99997, 'it-test@example.com', 'john.doe', 'it test client',
    'it test client',
    'thumbnailurl', 99997,
    'it-test-client', 'somesecret');
INSERT INTO Client_scopes values (99997, 'read'), (99997, 'write');

/*
Client getting refresh tokens (and skips consent)
*/
INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret, skipConsent, expireDuration, useRefreshTokens, notAllowedImplicitGrant)
VALUES
    (99996, 'it-test@example.com', 'john.doe', 'it test client no consent use refresh',
    'it test client 2',
    'thumbnailurl', 99997,
    'it-test-client-no-consent-refresh', 'somesecret2', 1, 3600, 1, 1);
INSERT INTO Client_scopes values (99996, 'read'), (99996, 'write');

/*
Client for implicit grant
*/
INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99995, 'it-test-grant@example.com', 'john.grant', 'it test client grant',
    'it test client grant',
    'thumbnailurl', 99997,
    'it-test-client-grant', 'somesecret-grant');
INSERT INTO Client_scopes values (99995, 'read'), (99995, 'write');

/*
admin-enduser
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, token, client_id, resourceOwnerId)
VALUES (99998, 0, '{\"name\":\"admin-enduser\",\"roles\":[\"user\",\"admin\"],\"attributes\":{}}',
'dad30fb8-ad90-4f24-af99-798bb71d27c8',99998, 'admin-enduser');
INSERT INTO Accesstoken_scopes values (99998, 'read'), (99998, 'write');
/*
it-test-enduser 
 */
INSERT INTO accesstoken (id, expires, encodedPrincipal, token, client_id, resourceOwnerId)
VALUES
    (99997, 0, '{"name":"it-test-enduser","roles":["user","admin"],"attributes":{}}',
    '00-11-22-33',99997, 'it-test-enduser ');
INSERT INTO Accesstoken_scopes values (99997, 'read'), (99997, 'write');
