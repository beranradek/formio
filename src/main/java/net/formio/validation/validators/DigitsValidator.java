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

import javax.validation.constraints.Digits;

import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;
import net.formio.validation.constraints.DigitsValidation;

/**
 * Validates the number of integer and fraction digits of given <code>Number</code>.
 * @author Radek Beran
 */
public class DigitsValidator<T extends Number> extends AbstractValidator<T> {
	
	protected static final String INTEGER_ARG = "integer";
	protected static final String FRACTION_ARG = "fraction";
	
	private final int maxIntegerLength;
	private final int maxFractionLength;
	
	public static <T extends Number> DigitsValidator<T> getInstance(int maxIntegerLength, int maxFractionLength) {
		return new DigitsValidator<T>(maxIntegerLength, maxFractionLength);
	}
	
	private DigitsValidator(int maxIntegerLength, int maxFractionLength) {
		this.maxIntegerLength = maxIntegerLength;
		this.maxFractionLength = maxFractionLength;
	}

	@Override
	public List<InterpolatedMessage> validate(ValidationContext<T> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			if (!DigitsValidation.isValid(ctx.getValidatedValue(), maxIntegerLength, maxFractionLength)) {
				msgs.add(error(ctx.getElementName(), "{" + Digits.class.getName() + ".message}",
					new Arg(VALUE_ARG, ctx.getValidatedValue()), 
					new Arg(INTEGER_ARG, Integer.valueOf(maxIntegerLength)), 
					new Arg(FRACTION_ARG, Integer.valueOf(maxFractionLength))));
			}
		}
		return msgs;
	}
	
	public int getMaxIntegerLength() {
		return maxIntegerLength;
	}
	
	public int getMaxFractionLength() {
		return maxFractionLength;
	}
}
