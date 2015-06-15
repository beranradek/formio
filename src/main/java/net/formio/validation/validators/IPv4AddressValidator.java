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
import net.formio.validation.constraints.IPv4Address;
import net.formio.validation.constraints.IPv4AddressValidation;

/**
 * IPv4 address validator.
 * @author Radek Beran
 */
public class IPv4AddressValidator extends AbstractValidator<String> {
	
	private static final IPv4AddressValidator INSTANCE = new IPv4AddressValidator(); 
	
	public static IPv4AddressValidator getInstance() {
		return INSTANCE;
	}

	@Override
	public <U extends String> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null && !ctx.getValidatedValue().isEmpty()) {
			if (!IPv4AddressValidation.isIPv4Address(ctx.getValidatedValue())) {
				msgs.add(error(ctx.getElementName(), IPv4Address.MESSAGE));
			}
		}
		return msgs;
	}
}
