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

import java.util.ArrayList;
import java.util.List;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;
import net.formio.validation.constraints.NotEmpty;
import net.formio.validation.constraints.NotEmptyValidation;

/**
 * Validates if string, collection, map or array is NOT {@code null} and NOT empty.
 * @author Radek Beran
 */
public class NotEmptyValidator extends AbstractValidator<Object> {
	
	private static final NotEmptyValidator INSTANCE = new NotEmptyValidator();
	
	public static NotEmptyValidator getInstance() {
		return INSTANCE;
	}

	@Override
	public <U extends Object> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (!NotEmptyValidation.isNotEmpty(ctx.getValidatedValue())) {
			msgs.add(error(ctx.getElementName(), NotEmpty.MESSAGE));
		}
		return msgs;
	}
}
