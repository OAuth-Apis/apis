/*
Local administration application
 */
INSERT INTO resourceserver (id, contactEmail,  contactName, resourceServerName, resourceServerKey, secret, owner, thumbNailUrl)
VALUES
	(99998, 'localadmin@example.com','local admin','Authorization Server Apis',
	'authorization-server-admin', 'cafebabe-cafe-babe-cafe-babecafebabe', null, 'https://raw.github.com/OpenConextApps/apis/master/apis-images/surf-oauth.png');
INSERT INTO ResourceServer_scopes values (99998, 'read'),(99998, 'write') ;

INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, includePrincipal, allowedImplicitGrant)
VALUES
    (99998, 'client@coolapp.com', 'john.doe', 'Javascript application for authorization server administration',
    'Authorization Server Admin Client',
    'https://raw.github.com/OpenConextApps/apis/master/apis-images/surf-oauth-client.png', 99998,
    'authorization-server-admin-js-client', 1, 1);
INSERT INTO Client_scopes values (99998, 'read'), (99998, 'write');