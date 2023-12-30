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

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

public class DigitsValidatorTest extends ValidatorTest {

	@Test
	public void testValidate() {
		DigitsValidator<BigDecimal> validator = DigitsValidator.<BigDecimal>getInstance(3, 2);  
		assertValid(validator.validate(value((BigDecimal)null)));
		
		BigDecimal v = BigDecimal.valueOf(876345, 3);
		InterpolatedMessage msg = assertInvalid(validator.validate(value(v)));
		assertEquals(value((String)null).getElementName(), msg.getElementName());
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(3, msg.getMessageParameters().size());
		assertEquals(v, msg.getMessageParameters().get(AbstractValidator.CURRENT_VALUE_ARG));
		assertEquals(Integer.valueOf(3), msg.getMessageParameters().get(DigitsValidator.INTEGER_ARG));
		assertEquals(Integer.valueOf(2), msg.getMessageParameters().get(DigitsValidator.FRACTION_ARG));
		assertEquals("{" + Digits.class.getName() + ".message}", msg.getMessageKey());
	}

}
