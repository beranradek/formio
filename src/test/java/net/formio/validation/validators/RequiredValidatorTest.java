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

import java.util.List;

import jakarta.validation.constraints.NotNull;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class RequiredValidatorTest extends ValidatorTest {
	
	private static final RequiredValidator<String> validator = RequiredValidator.getInstance();

	@Test
	public void testValid() {
		assertValid(validator.validate(value("Peugeot")));
	}
	
	@Test
	public void testInvalid() {
		assertInvalid(validator.validate(value("")));
		assertInvalid(validator.validate(value(" ")));
		
		List<InterpolatedMessage> msgs = validator.validate(value((String)null));
		InterpolatedMessage msg = assertInvalid(msgs);
		assertEquals(value((String)null).getElementName(), msg.getElementName());
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertTrue("Message arguments should be empty", msg.getMessageParameters().isEmpty());
		assertEquals("{" + NotNull.class.getName() + ".message}", msg.getMessageKey());
	}

}
