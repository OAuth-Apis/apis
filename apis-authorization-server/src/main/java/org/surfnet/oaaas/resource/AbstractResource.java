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

package org.surfnet.oaaas.resource;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.surfnet.oaaas.auth.AuthorizationServerFilter;
import org.surfnet.oaaas.auth.OAuth2Validator;
import org.surfnet.oaaas.auth.ValidationResponseException;
import org.surfnet.oaaas.model.ErrorResponse;
import org.surfnet.oaaas.model.ValidationErrorResponse;
import org.surfnet.oaaas.model.VerifyTokenResponse;
import org.surfnet.oaaas.repository.ExceptionTranslator;

/**
 * Abstract resource that defines common functionality.
 */
public class AbstractResource {

  public static final String SCOPE_READ = "read";
  public static final String SCOPE_WRITE = "write";

  private static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);

  @Inject
  private ExceptionTranslator exceptionTranslator;

  @Inject
  protected Validator validator;

  public Response buildErrorResponse(RuntimeException e) {
    final Throwable jpaException = exceptionTranslator.translate(e);
    Response.Status s;
    String reason;
    if (jpaException instanceof EntityExistsException) {
      s = Response.Status.BAD_REQUEST;
      reason = "Violating unique constraints";
    } else if (jpaException instanceof ConstraintViolationException) {
      s = Response.Status.BAD_REQUEST;
      reason = "Constraint violation " + jpaException.getMessage();
    } else {
      s = Response.Status.INTERNAL_SERVER_ERROR;
      reason = "Internal server error";
    }
    LOG.info("Responding with error '" + s + "', '" + reason + "'. Cause attached.", e);
    return Response
        .status(s)
        .entity(reason)
        .build();
  }


  protected Response buildViolationErrorResponse(Set<ConstraintViolation> violations) {
    ValidationErrorResponse responseBody = new ValidationErrorResponse(violations);
    return Response
        .status(Response.Status.BAD_REQUEST)
        .entity(responseBody)
        .build();
  }


  protected String getUserId(HttpServletRequest request) {
    return ((VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE)).getPrincipal().getName();
  }

  public String generateRandom() {
    return UUID.randomUUID().toString();
  }

  public Response validateScope(HttpServletRequest request, List<String> requiredScopes) {
    VerifyTokenResponse verifyTokenResponse = (VerifyTokenResponse) request.getAttribute(AuthorizationServerFilter.VERIFY_TOKEN_RESPONSE);
    List<String> grantedScopes = verifyTokenResponse.getScopes();
    for (String requiredScope : requiredScopes) {
      if (!grantedScopes.contains(requiredScope)) {
        LOG.debug("Resource required scopes ({}) which the client has not been granted ({})", requiredScopes, grantedScopes);
        return Response
            .status(HttpServletResponse.SC_BAD_REQUEST)
            .entity(new ErrorResponse(OAuth2Validator.ValidationResponse.SCOPE_NOT_VALID.getValue(),
                OAuth2Validator.ValidationResponse.SCOPE_NOT_VALID.getDescription()))
                .build();
      }
    }
    return null;
  }
}
