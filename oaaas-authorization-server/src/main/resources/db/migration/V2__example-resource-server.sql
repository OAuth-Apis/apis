INSERT INTO resourceserver (id, contactEmail,  contactName, name, scopes, secret )
VALUES
	(99999, 'foo@university.org','foo.bar','university-foo','read','58b749f7-acb3-44b7-a38c-53d5ad740cf6');

INSERT INTO client (id, contactEmail, contactName, description, name, scopes, thumbNailUrl, resourceserver_id)
VALUES
    (99999, 'client@coolapp.com', 'john.doe', 'Cool app for doing awesome things', 'cool-app', 'read', 'http://www.surfnet.nl/SURFnet%20imagebank/Logos/SURFconext_klein.gif', 99999);

INSERT INTO accesstoken (id, expires, principal, scopes, token, client_id)
VALUES
    (99999, 0, 'emma.blunt','read','74eccf5f-0995-4e1c-b08c-d05dd5a0f89b',99999);


/*
Local administration application
 */
INSERT INTO resourceserver (id, contactEmail,  contactName, name, scopes, secret )
VALUES
	(99998, 'localadmin@example.com','local admin','authorization-server-admin','read','cafebabe-cafe-babe-cafe-babecafebabe');

