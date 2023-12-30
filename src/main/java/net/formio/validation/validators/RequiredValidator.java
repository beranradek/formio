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

import jakarta.validation.constraints.NotNull;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;

/**
 * Required (not null) validator. If the validated value is a string,
 * string must be non empty.
 * @author Radek Beran
 */
public class RequiredValidator<T> extends AbstractValidator<T> {
	
	private static final RequiredValidator<?> INSTANCE = new RequiredValidator<Object>();
	
	public static <U> RequiredValidator<U> getInstance() {
		return (RequiredValidator<U>)INSTANCE;
	}

	@Override
	public <U extends T> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		boolean valid = ctx.getValidatedValue() != null;
		if (ctx.getValidatedValue() instanceof String) {
			valid = !((String)ctx.getValidatedValue()).trim().isEmpty();
		} else if (ctx.getValidatedValue() != null && ctx.getValidatedValue().getClass().getName().contains("scala.None")) {
			valid = false;
		}
		if (!valid) {
			msgs.add(error(ctx.getElementName(), "{" + NotNull.class.getName() + ".message}"));
		}
		return msgs;
	}
}
