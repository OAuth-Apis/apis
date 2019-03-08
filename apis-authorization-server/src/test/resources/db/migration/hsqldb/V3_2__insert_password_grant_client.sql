/*
Client for password grant
*/
INSERT INTO client (id, contactEmail, contactName, description, clientName, thumbNailUrl, resourceserver_id,
clientId, secret, allowedPasswordGrant)
VALUES
    (99991, 'it-test-password@example.com', 'john.password.grant', 'it test password grant',
    'it test password grant',
    'thumbnailurl', 99997,
    'it-test-password-grant', 'some-secret-client-password', 1);
