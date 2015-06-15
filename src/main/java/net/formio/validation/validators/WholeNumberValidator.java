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
 * Validates whether the whole number belongs to given range.
 * @author Radek Beran
 *
 * @param <T>
 */
public class WholeNumberValidator<T extends Number> extends AbstractNumberValidator<T> {
	private final long min;
	private final long max;
	
	static final long DEFAULT_MIN = Long.MIN_VALUE;
	static final long DEFAULT_MAX = Long.MAX_VALUE;
	
	public static <T extends Number> WholeNumberValidator<T> range(long min, long max) {
		return new WholeNumberValidator<T>(min, max);
	}
	
	public static <T extends Number> WholeNumberValidator<T> min(long min) {
		return new WholeNumberValidator<T>(min, DEFAULT_MAX);
	}
	
	public static <T extends Number> WholeNumberValidator<T> max(long max) {
		return new WholeNumberValidator<T>(DEFAULT_MIN, max);
	}
	
	private WholeNumberValidator(long min, long max) {
		this.min = min;
		this.max = max;
	}
	
	public long getMin() {
		return min;
	}
	
	public long getMax() {
		return max;
	}

	@Override
	public <U extends T> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			double d = ctx.getValidatedValue().doubleValue();
			if (d < DEFAULT_MIN) {
				msgs.add(error(ctx.getElementName(), MIN_MSG, new Arg(VALUE_ARG, Long.valueOf(DEFAULT_MIN))));
			}
			if (d > DEFAULT_MAX) {
				msgs.add(error(ctx.getElementName(), MAX_MSG, new Arg(VALUE_ARG, Long.valueOf(DEFAULT_MAX))));
			}
			long v = ctx.getValidatedValue().longValue();
			if (min > DEFAULT_MIN && max < DEFAULT_MAX) {
				if (v < min || v > max) {
					msgs.add(error(ctx.getElementName(), RANGE_MSG, new Arg(MIN_ARG, Long.valueOf(min)), new Arg(MAX_ARG, Long.valueOf(max))));
				}
			} else { 
				if (v < min) {
					msgs.add(error(ctx.getElementName(), MIN_MSG, new Arg(VALUE_ARG, Long.valueOf(min))));
				}
				if (v > max) {
					msgs.add(error(ctx.getElementName(), MAX_MSG, new Arg(VALUE_ARG, Long.valueOf(max))));
				}
			}
		}
		return msgs;
	}
}
