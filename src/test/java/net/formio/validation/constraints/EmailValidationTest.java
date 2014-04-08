/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.validation.constraints;

import static org.junit.Assert.*;
import net.formio.validation.constraints.EmailValidation;

import org.junit.Test;

public class EmailValidationTest {

	@Test
	public void testIsEmail() {
		assertFalse("should not be valid email", EmailValidation.isEmail(null));
		assertFalse("should not be valid email", EmailValidation.isEmail(""));
		assertFalse("should not be valid email", EmailValidation.isEmail("somemail"));
		
		assertFalse("should not be valid email", EmailValidation.isEmail("#@%^%#$@#$@#.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("Joe Smith <email@example.com>"));
		assertFalse("should not be valid email", EmailValidation.isEmail("email.example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("email@example@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail(".email@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("email@example.com (Joe Smith)"));
		assertFalse("should not be valid email", EmailValidation.isEmail("email@example..com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("just\"not\"right@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("this is\"not\\allowed@example.com"));
		assertFalse("should not be valid email", EmailValidation.isEmail("this is\\\"not\\\\allowed@example.com"));
		
		assertTrue("should be valid email", EmailValidation.isEmail("some@mail"));
		assertTrue("should be valid email", EmailValidation.isEmail("some@mail.en"));
	}

}
