/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 10.10.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package net.formio.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import net.formio.domain.Address;
import net.formio.domain.Registration;

public class RegistrationValidator implements ConstraintValidator<RegistrationValid, Registration> {

	public static final String TPL_PREFIX = "constraints.registration";
	
	@Override
	public void initialize(RegistrationValid ann) {
		// no initialization required
	}

	@Override
	public boolean isValid(Registration reg, ConstraintValidatorContext ctx) {
		if (reg == null) return true;
		
		// we will specify own message name, without using default message constraints.registrationValid
		ctx.disableDefaultConstraintViolation();
		boolean valid = true;
		// contact address or e-mail required
		if (!contactAddressFilled(reg.getContactAddress()) && !emailFilled(reg.getEmail())) {
			valid = false;
			ctx.buildConstraintViolationWithTemplate(getTplName("addrOrEmailRequired")).addConstraintViolation();
		}
		return valid;
	}
	
	private String getTplName(String violationType) {
		return "{" + TPL_PREFIX + "." + violationType + "}";
	}
	
	private boolean contactAddressFilled(Address addr) {
		return addr != null && !addr.isEmpty();
	}
	
	private boolean emailFilled(String email) {
		return email != null && !email.isEmpty();
	}
}
