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

import javax.validation.constraints.Pattern;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

public class RegexValidatorTest extends ValidatorTest {

	@Test
	public void testValidate() {
		String myEmailPattern = "[a-z]+\\.[a-z]+@[a-z]+\\.[a-z]+";
		RegexValidator validator = RegexValidator.getInstance(myEmailPattern, Pattern.Flag.CASE_INSENSITIVE);  
		assertValid(validator.validate(value("john.SMITH@email.cz")));
		assertValid(validator.validate(value((String)null)));
		
		String invalidValue = "john@email";
		InterpolatedMessage msg = assertInvalid(validator.validate(value(invalidValue)));
		assertEquals(value((String)null).getElementName(), msg.getElementName());
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(3, msg.getMessageParameters().size());
		assertEquals(invalidValue, msg.getMessageParameters().get(AbstractValidator.CURRENT_VALUE_ARG));
		assertEquals(myEmailPattern, msg.getMessageParameters().get(RegexValidator.REGEXP_ARG));
		assertEquals(Pattern.Flag.CASE_INSENSITIVE, ((Pattern.Flag[])msg.getMessageParameters().get(RegexValidator.FLAGS_ARG))[0]);
		assertEquals("{" + Pattern.class.getName() + ".message}", msg.getMessageKey());
		assertEquals(myEmailPattern, validator.getRegexp());
		assertEquals(1, validator.getPatternFlags().length);
		assertEquals(Pattern.Flag.CASE_INSENSITIVE, validator.getPatternFlags()[0]);
	}

}
