/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.surfnet.oaaas.repository;

import javax.persistence.PersistenceException;

import org.apache.openjpa.lib.jdbc.ReportingSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenJPAExceptionTranslator implements ExceptionTranslator {

  private static final Logger LOG = LoggerFactory.getLogger(OpenJPAExceptionTranslator.class);

  @Override
  public Exception translate(Throwable e) {
    if (e.getCause() != null && isRelevantCause(e.getCause())) {
      return translate(e.getCause());
    }
    Class<? extends Throwable> c = e.getClass();
    if (c.equals(org.apache.openjpa.persistence.EntityExistsException.class)) {
      return new javax.persistence.EntityExistsException(e.getMessage(), e);
    } else if (c.equals(javax.validation.ConstraintViolationException.class)) {
      return (Exception) e;
    }
    LOG.info("Cannot translate '{}' to specific subtype, will return generic PersistenceException",
        e.getClass().getName());
    return new PersistenceException(e);
  }

  /**
   * OpenJPA starts with an irrelevant ReportingSQLException....
   */
  private boolean isRelevantCause(Throwable cause) {
    return ! (cause instanceof ReportingSQLException);
  }
}
