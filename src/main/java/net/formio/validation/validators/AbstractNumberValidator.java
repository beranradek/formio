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

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Validates whether the number belongs to given range.
 * @author Radek Beran
 * @param <T> type of validated value
 */
public abstract class AbstractNumberValidator<T extends Number> extends AbstractValidator<T> {
	protected static final String MIN_MSG = "{" + Min.class.getName() + ".message}";
	protected static final String MAX_MSG = "{" + Max.class.getName() + ".message}";
	protected static final String RANGE_MSG = "{" + Size.class.getName() + ".message}";
	protected static final String DECIMAL_MIN_MSG = "{" + DecimalMin.class.getName() + ".message}";
	protected static final String DECIMAL_MAX_MSG = "{" + DecimalMax.class.getName() + ".message}";
	protected static final String MIN_ARG = "min";
	protected static final String MAX_ARG = "max";
}
