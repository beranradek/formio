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

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;
import net.formio.validation.constraints.Phone;

import org.junit.Test;

public class PhoneValidatorTest extends ValidatorTest {
	
	private static final PhoneValidator validator = PhoneValidator.getInstance();
	
	@Test
	public void testValid() {
		assertValid(validator.validate(value((String)null)));
		assertValid(validator.validate(value("")));
		assertValid(validator.validate(value("(305) 613 09 58 ext 101")));
		assertValid(validator.validate(value("+420 728 90 80 70")));
	}
	
	@Test
	public void testInvalid() {
		String invalidValue = "12,34,56789";
		List<InterpolatedMessage> msgs = validator.validate(value(invalidValue));
		InterpolatedMessage msg = assertInvalid(msgs);
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(Phone.MESSAGE, msg.getMessageKey());
		assertEquals(invalidValue, msg.getMessageParameters().get(AbstractValidator.CURRENT_VALUE_ARG));
	}

}
