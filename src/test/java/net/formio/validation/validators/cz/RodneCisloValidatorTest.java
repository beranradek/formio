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
package net.formio.validation.validators.cz;

import static org.junit.Assert.*;

import java.util.List;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;
import net.formio.validation.validators.ValidatorTest;

import org.junit.Test;

/** 
 * @author Radek Beran
 */
public class RodneCisloValidatorTest extends ValidatorTest {
	
	private static final RodneCisloValidator validator = RodneCisloValidator.getInstance();

	@Test
	public void testValid() {
		assertValid(validator.validate(value((String)null)));
		assertValid(validator.validate(value("")));
		assertValid(validator.validate(value("780123/3540"))); // valid even if not divisible by 11
		assertValid(validator.validate(value("0531135099")));
		assertValid(validator.validate(value("0681186066")));
	}
	
	@Test
	public void testInvalid() {
		List<InterpolatedMessage> msgs = validator.validate(value("4w4w4qw"));
		InterpolatedMessage msg = assertInvalid(msgs);
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals("{constraints.RodneCislo.message}", msg.getMessageKey());
	}

}
