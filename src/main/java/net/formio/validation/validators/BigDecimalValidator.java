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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;

/**
 * Validates whether the decimal value belongs to given range.
 * @author Radek Beran
 */
public class BigDecimalValidator extends AbstractNumberValidator<BigDecimal> {
	private final BigDecimal min;
	private final BigDecimal max;
	
	public static BigDecimalValidator range(BigDecimal min, BigDecimal max) {
		return new BigDecimalValidator(min, max);
	}
	
	public static BigDecimalValidator min(BigDecimal min) {
		return new BigDecimalValidator(min, null);
	}
	
	public static BigDecimalValidator max(BigDecimal max) {
		return new BigDecimalValidator(null, max);
	}
	
	private BigDecimalValidator(BigDecimal min, BigDecimal max) {
		this.min = min;
		this.max = max;
	}
	
	public BigDecimal getMin() {
		return min;
	}
	
	public BigDecimal getMax() {
		return max;
	}

	@Override
	public List<InterpolatedMessage> validate(ValidationContext<BigDecimal> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			BigDecimal v = ctx.getValidatedValue();
			if (min != null && max != null) {
				if (v.compareTo(min) < 0 || v.compareTo(max) > 0) {
					msgs.add(error(ctx.getElementName(), RANGE_MSG, new Arg(MIN_ARG, min), new Arg(MAX_ARG, max)));
				}
			} else { 
				if (min != null && v.compareTo(min) < 0) {
					msgs.add(error(ctx.getElementName(), MIN_MSG, new Arg(VALUE_ARG, min)));
				}
				if (max != null && v.compareTo(max) > 0) {
					msgs.add(error(ctx.getElementName(), MAX_MSG, new Arg(VALUE_ARG, max)));
				}
			}
		}
		return msgs;
	}
}
