package org.surfnet.oaaas.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.repository.AccessTokenRepository;

import javax.inject.Inject;
import java.util.Date;

/**
 * Helper class that contains scheduled tasks for database cleanup
 */
public class Cleaner {
  private static final Logger LOG = LoggerFactory.getLogger(Cleaner.class);

  @Inject
  private AccessTokenRepository accessTokenRepository;

  /**
   * Interval in ms between cleanup jobs
   */
  private static final long CLEANUP_INTERVAL = 1000 * 3600;

  /**
   * Throw away expired tokens after 30 days
   */
  private static final long EXPIRED_TOKEN_CLEANUP_AGE = 1000L * 3600 * 24 * 30;

  @Scheduled(fixedDelay = CLEANUP_INTERVAL)
  public void cleanupExpiredAccessTokens() {
    LOG.debug("Cleaning up expired access tokens");
    for (AccessToken at : accessTokenRepository.findByMaxExpires(System.currentTimeMillis() - EXPIRED_TOKEN_CLEANUP_AGE)) {
      LOG.debug("Deleting expired access token {} (created: {}, expired: {})", at.getToken(), at.getCreationDate(), new Date(at.getExpires()));
      accessTokenRepository.delete(at);
    }
  }
}
