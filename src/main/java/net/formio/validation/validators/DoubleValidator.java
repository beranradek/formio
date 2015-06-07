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

import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;

/**
 * Validates whether the double value belongs to given range.
 * @author Radek Beran
 */
public class DoubleValidator extends AbstractNumberValidator<Double> {
	private final double min;
	private final double max;
	
	static final double DEFAULT_MIN = Double.MIN_VALUE;
	static final double DEFAULT_MAX = Double.MAX_VALUE;
	
	public static DoubleValidator range(double min, double max) {
		return new DoubleValidator(min, max);
	}
	
	public static DoubleValidator min(double min) {
		return new DoubleValidator(min, DEFAULT_MAX);
	}
	
	public static DoubleValidator max(double max) {
		return new DoubleValidator(DEFAULT_MIN, max);
	}
	
	private DoubleValidator(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}

	@Override
	public List<InterpolatedMessage> validate(ValidationContext<Double> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			double v = ctx.getValidatedValue().doubleValue();
			if (min > DEFAULT_MIN && max < DEFAULT_MAX) {
				if (v < min || v > max) {
					msgs.add(error(ctx.getElementName(), RANGE_MSG, new Arg(MIN_ARG, Double.valueOf(min)), new Arg(MAX_ARG, Double.valueOf(max))));
				}
			} else { 
				if (v < min) {
					msgs.add(error(ctx.getElementName(), MIN_MSG, new Arg(VALUE_ARG, Double.valueOf(min))));
				}
				if (v > max) {
					msgs.add(error(ctx.getElementName(), MAX_MSG, new Arg(VALUE_ARG, Double.valueOf(max))));
				}
			}
		}
		return msgs;
	}
}
