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

import java.math.BigDecimal;

import org.junit.Test;

public class DigitsValidationTest {

	@Test
	public void testIsValid() {
		assertTrue(DigitsValidation.isValid(null, 2, 3));
		assertTrue(DigitsValidation.isValid(BigDecimal.valueOf(12345, 3), 2, 3));
		assertTrue(DigitsValidation.isValid(BigDecimal.valueOf(12345, 2), 3, 3));
		assertTrue(DigitsValidation.isValid(Double.valueOf(25.345), 2, 3));
		assertTrue(DigitsValidation.isValid(Long.valueOf(34), 2, 3));
		
		assertFalse(DigitsValidation.isValid(BigDecimal.valueOf(12345, 2), 2, 3));
		assertFalse(DigitsValidation.isValid(BigDecimal.valueOf(12345, 4), 2, 3));
		assertFalse(DigitsValidation.isValid(Double.valueOf(25.3456), 2, 3));
		assertFalse(DigitsValidation.isValid(Long.valueOf(348), 2, 3));
	}

}
