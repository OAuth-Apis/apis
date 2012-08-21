/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.surfnet.oaaas.model.AbstractEntity;

/**
 * {@link ConstraintValidator} that validates {@link org.surfnet.oaaas.model.AbstractEntity} by calling
 * the {@link org.surfnet.oaaas.model.AbstractEntity#validate(javax.validation.ConstraintValidatorContext)}
 *
 */
public class AbstractEntityValidator implements ConstraintValidator<AbstractEntityValid, AbstractEntity> {

  @Override
  public void initialize(AbstractEntityValid constraintAnnotation) {
  }

  @Override
  public boolean isValid(AbstractEntity entity, ConstraintValidatorContext context) {
    return entity.validate(context);
  }
}
