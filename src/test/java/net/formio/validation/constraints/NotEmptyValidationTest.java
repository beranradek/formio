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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for {@link NotEmptyValidation}.
 * @author Radek Beran
 */
public class NotEmptyValidationTest {

	@Test
	public void testIsNotEmpty() {
		assertTrue(NotEmptyValidation.isNotEmpty("string"));
		assertTrue(NotEmptyValidation.isNotEmpty(Arrays.asList(Integer.valueOf(0), Integer.valueOf(1))));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "b");
		assertTrue(NotEmptyValidation.isNotEmpty(map));
		assertTrue(NotEmptyValidation.isNotEmpty(new int[] {1, 2, 3}));
		assertTrue(NotEmptyValidation.isNotEmpty(new String[] {"a", "b", "c"}));
	}
	
	@Test
	public void testEmptyCases() {
		assertFalse(NotEmptyValidation.isNotEmpty(null));
		assertFalse(NotEmptyValidation.isNotEmpty(""));
		assertFalse(NotEmptyValidation.isNotEmpty(new ArrayList<Integer>()));
		assertFalse(NotEmptyValidation.isNotEmpty(new HashMap<String, Object>()));
		assertFalse(NotEmptyValidation.isNotEmpty(new int[] {}));
		assertFalse(NotEmptyValidation.isNotEmpty(new String[] {}));
	}

}
