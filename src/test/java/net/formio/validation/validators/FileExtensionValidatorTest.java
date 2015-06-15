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
import net.formio.validation.constraints.FileExtension;

import org.junit.Test;

public class FileExtensionValidatorTest extends ValidatorTest {
	
	@Test
	public void testValid() {
		FileExtensionValidator validator = FileExtensionValidator.getInstance(new String[] {"doc", "xls"}, true);
		assertValid(validator.validate(value((String)null)));
		assertValid(validator.validate(value("")));
		assertValid(validator.validate(value("document.xls")));
	}
	
	@Test
	public void testInvalid() {
		FileExtensionValidator validator = FileExtensionValidator.getInstance(new String[] {"doc", "xls"}, true);
		String invalidValue = "image.png";
		List<InterpolatedMessage> msgs = validator.validate(value(invalidValue));
		InterpolatedMessage msg = assertInvalid(msgs);
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(FileExtension.MESSAGE, msg.getMessageKey());
		assertEquals(invalidValue, msg.getMessageParameters().get(AbstractValidator.CURRENT_VALUE_ARG));
		assertEquals("doc, xls", msg.getMessageParameters().get(FileExtensionValidator.ALLOWED_EXTENSIONS_ARG));
	}

}
	