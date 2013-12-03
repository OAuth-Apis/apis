package org.surfnet.oaaas.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.repository.AccessTokenRepository;

import javax.inject.Inject;
import java.util.Date;

public class Cleaner {
  private static final Logger LOG = LoggerFactory.getLogger(Cleaner.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  private static final int EXPIRE = 1000 * 10;

  @Scheduled(fixedDelay = EXPIRE)
  public void cleanupExpiredAccessTokens() {
    LOG.debug("Cleaning up expired access tokens");
    for (AccessToken at : accessTokenRepository.findByMaxExpires(System.currentTimeMillis() - 1000)) {
      LOG.debug("Deleting expired access token {} (created: {}, expired: {})", at.getToken(), at.getCreationDate(), new Date(at.getExpires()));
      accessTokenRepository.delete(at);
    }
  }
}
