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
import net.formio.upload.RequestUploadedFile;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;
import net.formio.validation.constraints.MaxFileSize;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class MaxFileSizeValidatorTest extends ValidatorTest {

	@Test
	public void testValid() {
		MaxFileSizeValidator validator = MaxFileSizeValidator.getInstance("50MB");
		assertValid(validator.validate(value(new RequestUploadedFile("document.xml", "application/xml", 52428800, null))));
		assertValid(validator.validate(value(new RequestUploadedFile("document.json", "application/json", 45, null))));
	}
	
	@Test
	public void testInvalid() {
		MaxFileSizeValidator validator = MaxFileSizeValidator.getInstance("50MB");
		assertInvalid(validator.validate(value(new RequestUploadedFile("document.xml", "application/xml", 52428801, null))));
		InterpolatedMessage msg = assertInvalid(validator.validate(value(new RequestUploadedFile("document.json", "application/json", 62428802, null))));
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals(MaxFileSize.MESSAGE, msg.getMessageKey());
		assertEquals("50MB", msg.getMessageParameters().get(MaxFileSizeValidator.MAX_ARG));
	}

}
