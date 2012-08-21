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

package org.surfnet.oaaas.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.internal.constraintvalidators.PatternValidator;

/**
 * Validator that applies the {@link Pattern @Pattern} validation annotation to its items.
 * It delegates the item validation to an instance of {@link PatternValidator}
 */
public class PatternIterableValidator implements ConstraintValidator<Pattern, Iterable<String>> {

  private PatternValidator patternValidator;

  @Override
  public void initialize(Pattern pattern) {
    patternValidator = new PatternValidator();
    patternValidator.initialize(pattern);
  }

  @Override
  public boolean isValid(Iterable<String> iterable, ConstraintValidatorContext context) {
    boolean isValid = true;
    for (String item : iterable) {
      isValid = patternValidator.isValid(item, context);
      if (!isValid)
        break;
    }
    return isValid;
  }
}