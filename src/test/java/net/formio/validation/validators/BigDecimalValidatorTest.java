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

import java.math.BigDecimal;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class BigDecimalValidatorTest extends ValidatorTest {

	@Test
	public void testValidateRange() {
		BigDecimal min = BigDecimal.valueOf(250, 2); /** 2.50 */
		BigDecimal max = BigDecimal.valueOf(120867, 3); /** 120.867 */
		BigDecimalValidator v = BigDecimalValidator.range(min, max);
		assertValid(v.validate(value((BigDecimal)null)));
		assertValid(v.validate(value(BigDecimal.valueOf(120867, 3))));
		assertValid(v.validate(value(BigDecimal.valueOf(1208660, 4))));
		assertValid(v.validate(value(BigDecimal.valueOf(25000, 4))));
		assertValid(v.validate(value(BigDecimal.valueOf(47))));
		assertValid(v.validate(value(BigDecimal.valueOf(4756, 2))));
		
		assertInvalid(v.validate(value(BigDecimal.valueOf(24999, 4))));
		InterpolatedMessage msg = assertInvalid(v.validate(value(BigDecimal.valueOf(120867001, 6))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.RANGE_MSG, msg.getMessageKey());
		assertEquals(2, msg.getMessageParameters().size());
		assertEquals(min, msg.getMessageParameters().get(AbstractNumberValidator.MIN_ARG));
		assertEquals(max, msg.getMessageParameters().get(AbstractNumberValidator.MAX_ARG));
		assertEquals(min, v.getMin());
		assertEquals(max, v.getMax());
	}
	
	@Test
	public void testValidateMin() {
		BigDecimal min = BigDecimal.valueOf(549, 2); /** 5.49 */
		BigDecimalValidator v = BigDecimalValidator.min(min);
		assertValid(v.validate(value((BigDecimal)null)));
		assertValid(v.validate(value(BigDecimal.valueOf(54901, 4))));
		assertValid(v.validate(value(BigDecimal.valueOf(8.5))));
		
		assertInvalid(v.validate(value(BigDecimal.valueOf(54899, 4))));
		InterpolatedMessage msg = assertInvalid(v.validate(value(BigDecimal.valueOf(520, 2))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.MIN_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(min, msg.getMessageParameters().get(AbstractNumberValidator.VALUE_ARG));
	}
	
	@Test
	public void testValidateMax() {
		BigDecimal max = BigDecimal.valueOf(549, 2); /** 5.49 */
		BigDecimalValidator v = BigDecimalValidator.max(max);
		assertValid(v.validate(value((BigDecimal)null)));
		assertValid(v.validate(value(BigDecimal.valueOf(54899, 4))));
		assertValid(v.validate(value(BigDecimal.valueOf(450, 2))));
		assertValid(v.validate(value(BigDecimal.ZERO)));
		assertValid(v.validate(value(BigDecimal.ONE)));
		
		assertInvalid(v.validate(value(BigDecimal.valueOf(549010, 5))));
		InterpolatedMessage msg = assertInvalid(v.validate(value(BigDecimal.valueOf(50.34))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(getDefaultElementName(), msg.getElementName());
		assertEquals(AbstractNumberValidator.MAX_MSG, msg.getMessageKey());
		assertEquals(1, msg.getMessageParameters().size());
		assertEquals(max, msg.getMessageParameters().get(AbstractNumberValidator.VALUE_ARG));
	}

}
