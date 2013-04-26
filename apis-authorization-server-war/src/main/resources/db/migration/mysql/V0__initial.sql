CREATE TABLE `AbstractEntity` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `DTYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_BSTRTTY_DTYPE` (`DTYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AccessToken_scopes` (
  `ACCESSTOKEN_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_CCSSCPS_ACCESSTOKEN_ID` (`ACCESSTOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AuthorizationRequest_grantedScopes` (
  `AUTHORIZATIONREQUEST_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_THRZCPS_AUTHORIZATIONREQUEST_ID` (`AUTHORIZATIONREQUEST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AuthorizationRequest_requestedScopes` (
  `AUTHORIZATIONREQUEST_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_THRZCPS_AUTHORIZATIONREQUEST_ID1` (`AUTHORIZATIONREQUEST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Client_redirectUris` (
  `CLIENT_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_CLNTTRS_CLIENT_ID` (`CLIENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Client_scopes` (
  `CLIENT_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_CLNTCPS_CLIENT_ID` (`CLIENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `OPENJPA_SEQUENCE_TABLE` (
  `ID` tinyint(4) NOT NULL,
  `SEQUENCE_VALUE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ResourceServer_scopes` (
  `RESOURCESERVER_ID` bigint(20) DEFAULT NULL,
  `element` varchar(255) DEFAULT NULL,
  KEY `I_RSRCCPS_RESOURCESERVER_ID` (`RESOURCESERVER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `accesstoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accesstoken` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `encodedPrincipal` TEXT DEFAULT NULL,
  `expires` bigint(20) DEFAULT NULL,
  `refreshToken` varchar(255) DEFAULT NULL,
  `resourceOwnerId` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `client_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `U_CCSSTKN_REFRESHTOKEN` (`refreshToken`),
  UNIQUE KEY `U_CCSSTKN_TOKEN` (`token`),
  KEY `I_CCSSTKN_CLIENT` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `authorizationrequest` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `authState` varchar(255) DEFAULT NULL,
  `authorizationCode` varchar(255) DEFAULT NULL,
  `encodedPrincipal` TEXT DEFAULT NULL,
  `redirectUri` varchar(255) DEFAULT NULL,
  `responseType` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `client_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_THRZQST_CLIENT` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `client` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `contactEmail` varchar(255) DEFAULT NULL,
  `contactName` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `expireDuration` bigint(20) DEFAULT NULL,
  `clientName` varchar(255) DEFAULT NULL,
  `allowedImplicitGrant` bit(1) DEFAULT NULL,
  `allowedClientCredentials` bit(1) DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `skipConsent` bit(1) DEFAULT NULL,
  `includePrincipal` bit(1) DEFAULT NULL,
  `thumbNailUrl` varchar(255) DEFAULT NULL,
  `useRefreshTokens` bit(1) DEFAULT NULL,
  `resourceserver_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `U_CLIENT_CLIENTID` (`clientId`),
  KEY `I_CLIENT_RESOURCESERVER` (`resourceserver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `client_attributes` (
  `client_id` bigint(20) DEFAULT NULL,
  `attribute_name` varchar(255) NOT NULL,
  `attribute_value` varchar(255) DEFAULT NULL,
  KEY `I_CLNTBTS_CLIENT_ID` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resourceserver` (
  `id` bigint(20) NOT NULL,
  `creationDate` datetime DEFAULT NULL,
  `modificationDate` datetime DEFAULT NULL,
  `contactEmail` varchar(255) DEFAULT NULL,
  `contactName` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `resourceServerKey` varchar(255) DEFAULT NULL,
  `resourceServerName` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `secret` varchar(255) NOT NULL,
  `thumbNailUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `U_RSRCRVR_KEY` (`resourceServerKey`),
  UNIQUE KEY `U_RSRCRVR_OWNER` (`owner`,`resourceServerName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
