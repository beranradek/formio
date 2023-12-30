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
package net.formio.validation.constraints;

import static org.junit.Assert.*;

import jakarta.validation.constraints.Pattern;

import org.junit.Test;

public class RegexValidationTest {

	@Test
	public void testIsValid() {
		String email = "JOHN.smith@email.com";
		String myEmailPattern = "[a-z]+\\.[a-z]+@[a-z]+\\.[a-z]+";
		assertTrue("Value is not valid", RegexValidation.isValid(email, myEmailPattern, Pattern.Flag.CASE_INSENSITIVE));
		assertFalse("Value is valid", RegexValidation.isValid(null, myEmailPattern));
		assertFalse("Value is valid", RegexValidation.isValid(email, myEmailPattern));
		assertFalse("Value is valid", RegexValidation.isValid("some@mail", myEmailPattern));
	}

}
