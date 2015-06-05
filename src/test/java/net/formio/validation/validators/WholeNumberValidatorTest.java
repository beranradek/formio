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
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class WholeNumberValidatorTest extends ValidatorTest {

	@Test
	public void testValidateRange() {
		WholeNumberValidator<Integer> v = WholeNumberValidator.<Integer>range(2, 6);
		assertValid(v.validate(value(Integer.valueOf(4))));
		assertValid(v.validate(value(Integer.valueOf(2))));
		assertValid(v.validate(value(Integer.valueOf(6))));
		
		InterpolatedMessage msg = assertInvalid(v.validate(value(Integer.valueOf(12))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(WholeNumberValidator.RANGE_MSG, msg.getMessageKey());
		assertEquals(2, msg.getMessageParameters().size());
		assertEquals(Long.valueOf(2), msg.getMessageParameters().get(WholeNumberValidator.MIN_ARG));
		assertEquals(Long.valueOf(6), msg.getMessageParameters().get(WholeNumberValidator.MAX_ARG));
	}
	
	@Test
	public void testValidateMin() {
		WholeNumberValidator<Long> v = WholeNumberValidator.<Long>min(1);
		assertValid(v.validate(value(Long.valueOf(3))));
		assertValid(v.validate(value(Long.valueOf(1))));
		
		InterpolatedMessage msg = assertInvalid(v.validate(value(Long.valueOf(0))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(WholeNumberValidator.MIN_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(Long.valueOf(1), msg.getMessageParameters().get(WholeNumberValidator.VALUE_ARG));
	}
	
	@Test
	public void testValidateMax() {
		WholeNumberValidator<Short> v = WholeNumberValidator.<Short>max(10);
		assertValid(v.validate(value(Short.valueOf("" + 3))));
		assertValid(v.validate(value(Short.valueOf("" + 10))));
		
		InterpolatedMessage msg = assertInvalid(v.validate(value(Short.valueOf("20"))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(WholeNumberValidator.MAX_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(Long.valueOf(10), msg.getMessageParameters().get(WholeNumberValidator.VALUE_ARG));
	}

}
