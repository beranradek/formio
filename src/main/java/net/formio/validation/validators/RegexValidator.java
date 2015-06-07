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

import javax.validation.constraints.Pattern;

import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;
import net.formio.validation.constraints.RegexValidation;

/**
 * Validates if given value matches the regular expresssion pattern.
 * @author Radek Beran
 */
public class RegexValidator extends AbstractValidator<String> {
	
	protected static final String REGEXP_ARG = "regexp";
	
	private final String regexp;
	private final Pattern.Flag[] patternFlags;
	
	public static RegexValidator getInstance(String regexp, Pattern.Flag ... patternFlags) {
		return new RegexValidator(regexp, patternFlags);
	}
	
	private RegexValidator(String regexp, Pattern.Flag ... patternFlags) {
		this.regexp = regexp;
		this.patternFlags = patternFlags;
	}

	@Override
	public List<InterpolatedMessage> validate(ValidationContext<String> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			if (!RegexValidation.isValid(ctx.getValidatedValue(), regexp, patternFlags)) {
				msgs.add(error(ctx.getElementName(), "{" + Pattern.class.getName() + ".message}",
					new Arg(VALUE_ARG, ctx.getValidatedValue()), 
					new Arg(REGEXP_ARG, regexp)));
			}
		}
		return msgs;
	}
	
	public String getRegexp() {
		return regexp;
	}
	
	public Pattern.Flag[] getPatternFlags() {
		return patternFlags;
	}
}
