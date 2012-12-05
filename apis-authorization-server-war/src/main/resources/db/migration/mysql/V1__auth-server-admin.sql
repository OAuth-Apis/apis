/*
Local administration application
 */
INSERT INTO resourceserver (id, contactEmail,  contactName, resourceServerName, resourceServerKey, secret, owner, thumbNailUrl)
VALUES
	(99998, 'localadmin@example.com','local admin','Authorization Server Apis',
	'authorization-server-admin', 'cafebabe-cafe-babe-cafe-babecafebabe', null, 'http://www.aldokkan.com/religion/apis.jpg');
INSERT INTO ResourceServer_scopes values (99998, 'read'),(99998, 'write') ;

INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret)
VALUES
    (99998, 'client@coolapp.com', 'john.doe', 'Javascript application for authorization server administration',
    'Authorization Server Admin Client',
    'http://www.artyfactory.com/egyptian_art/egyptian_gods/images/apis.jpg', 99998,
    'authorization-server-admin-js-client', '');
INSERT INTO Client_scopes values (99998, 'read'), (99998, 'write');
