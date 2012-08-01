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
