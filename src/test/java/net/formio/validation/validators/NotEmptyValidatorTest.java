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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;
import net.formio.validation.constraints.NotEmpty;

import org.junit.Test;

public class NotEmptyValidatorTest extends ValidatorTest {

	private static final NotEmptyValidator validator = NotEmptyValidator.getInstance();
	
	@Test
	public void testValid() {
		assertValid(validator.validate(value("x")));
		assertValid(validator.validate(value(new Object[] { Integer.valueOf(1) })));
		List<String> list = new ArrayList<String>();
		list.add("item");
		assertValid(validator.validate(value(list)));
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		assertValid(validator.validate(value(map)));
	}
	
	@Test
	public void testInvalid() {
		String invalidValue = "";
		List<InterpolatedMessage> msgs = validator.validate(value(invalidValue));
		InterpolatedMessage msg = assertInvalid(msgs);
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(NotEmpty.MESSAGE, msg.getMessageKey());
	}
}
