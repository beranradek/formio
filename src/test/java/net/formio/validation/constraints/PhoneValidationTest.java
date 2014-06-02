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

import org.junit.Test;

public class PhoneValidationTest {

	@Test
	public void testIsPhone() {
		assertFalse("should not be valid phone", PhoneValidation.isPhone(null));
		assertFalse("should not be valid phone", PhoneValidation.isPhone(""));
		assertFalse("should not be valid phone", PhoneValidation.isPhone("a123456789"));
		assertFalse("should not be valid phone", PhoneValidation.isPhone("12,34,56789"));
		
		// USA:
		assertTrue("should be valid phone", PhoneValidation.isPhone("(305) 613 09 58 ext 101"));
		// France:
		assertTrue("should be valid phone", PhoneValidation.isPhone("+33 1 47 37 62 24 x3"));
		// Germany:
		assertTrue("should be valid phone", PhoneValidation.isPhone("+49-4312 / 777 777"));
		// Czech:
		assertTrue("should be valid phone", PhoneValidation.isPhone("+420 728 90 80 70"));
		assertTrue("should be valid phone", PhoneValidation.isPhone("123456789"));
		// China:
		assertTrue("should be valid phone", PhoneValidation.isPhone("+86 (10)69445464"));
		// United Kingdom:
		assertTrue("should be valid phone", PhoneValidation.isPhone("(020) 1234 1234"));
	}

}
