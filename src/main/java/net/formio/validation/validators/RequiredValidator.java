/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.validation.validators;

import javax.validation.constraints.NotNull;

import net.formio.validation.Validated;
import net.formio.validation.ValidationContext;
import net.formio.validation.Validator;

/**
 * Required (not null) validator.
 * @author Radek Beran
 */
public class RequiredValidator<T> implements Validator<T> {

	@Override
	public Validated<T> validate(ValidationContext<T> ctx) {
		boolean valid = ctx.getValidatedValue() != null;
		return new Validated<T>(ctx, valid, "{" + NotNull.class.getName() + ".message}");
	}

}
