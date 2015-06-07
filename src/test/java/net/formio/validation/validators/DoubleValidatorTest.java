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

import static org.junit.Assert.*;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class DoubleValidatorTest extends ValidatorTest {

	@Test
	public void testValidateRange() {
		DoubleValidator v = DoubleValidator.range(2.5, 120.8);
		assertValid(v.validate(value((Double)null)));
		assertValid(v.validate(value(Double.valueOf(2.6))));
		assertValid(v.validate(value(Double.valueOf(120.7))));
		assertValid(v.validate(value(Double.valueOf(2.5))));
		assertValid(v.validate(value(Double.valueOf(120.8))));
		
		InterpolatedMessage msg = assertInvalid(v.validate(value(Double.valueOf(2.0))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.RANGE_MSG, msg.getMessageKey());
		assertEquals(2, msg.getMessageParameters().size());
		assertEquals(Double.valueOf(2.5), msg.getMessageParameters().get(AbstractNumberValidator.MIN_ARG));
		assertEquals(Double.valueOf(120.8), msg.getMessageParameters().get(AbstractNumberValidator.MAX_ARG));
		assertEquals(2.5, v.getMin(), 0.001);
		assertEquals(120.8, v.getMax(), 0.001);
	}
	
	@Test
	public void testValidateMin() {
		DoubleValidator v = DoubleValidator.min(2.5);
		assertValid(v.validate(value((Double)null)));
		assertValid(v.validate(value(Double.valueOf(3))));
		assertValid(v.validate(value(Double.valueOf(2.5))));
		
		assertInvalid(v.validate(value(Double.valueOf(2.499))));
		InterpolatedMessage msg = assertInvalid(v.validate(value(Double.valueOf(2.4))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.DECIMAL_MIN_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(Double.valueOf(2.5), msg.getMessageParameters().get(AbstractNumberValidator.VALUE_ARG));
	}
	
	@Test
	public void testValidateMax() {
		DoubleValidator v = DoubleValidator.max(50.33);
		assertValid(v.validate(value((Double)null)));
		assertValid(v.validate(value(Double.valueOf(3.4))));
		assertValid(v.validate(value(Double.valueOf(10.1))));
		assertValid(v.validate(value(Double.valueOf(50.32))));
		assertValid(v.validate(value(Double.valueOf(50.33))));
		
		assertInvalid(v.validate(value(Double.valueOf(50.333))));
		InterpolatedMessage msg = assertInvalid(v.validate(value(Double.valueOf(50.34))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.DECIMAL_MAX_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(Double.valueOf(50.33), msg.getMessageParameters().get(AbstractNumberValidator.VALUE_ARG));
	}

}
