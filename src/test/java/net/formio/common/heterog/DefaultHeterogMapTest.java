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
package net.formio.common.heterog;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultHeterogMapTest {
	private static final TypedKey<String, Integer> INT_KEY = DefaultTypedKey.valueOf("int", Integer.class);
	private static final TypedKey<String, String> HELLO_KEY = DefaultTypedKey.valueOf("hello", String.class);
	private static final TypedKey<String, Integer> ANOTHER_INT_KEY = DefaultTypedKey.valueOf("anotherInt", Integer.class);
	private static final TypedKey<String, BigDecimal> MISSING_KEY = DefaultTypedKey.valueOf("missing", BigDecimal.class);
	
	private HeterogMap<String> config;
	
	@Before
	public void init() {
		config = HeterogCollections.newMap();
	}
	
	@Test
	public void testPutAndGetValue() {
		config.putTyped(INT_KEY, new Integer(15));
		config.putTyped(HELLO_KEY, "Hello");
		config.putTyped(ANOTHER_INT_KEY, new Integer(10));
		Assert.assertEquals(3, config.size());
		Integer i1 = config.getTyped(INT_KEY);
		String str = config.getTyped(HELLO_KEY);
		Integer i2 = config.getTyped(ANOTHER_INT_KEY);
		Assert.assertEquals(15, i1.intValue());
		Assert.assertEquals("Hello", str);
		Assert.assertEquals(10, i2.intValue());
	}
	
	@Test
	public void testContainsKey() {
		Assert.assertFalse("Container should not contain key", config.containsKey(INT_KEY));
		Assert.assertFalse("Container should not contain key", config.containsKey(MISSING_KEY));
		config.putTyped(INT_KEY, new Integer(15));
		Assert.assertTrue("Container should contain key", config.containsKey(INT_KEY));
		Assert.assertFalse("Container should not contain key", config.containsKey(MISSING_KEY));
	}
	
	@Test
	public void testToString() {
		config.putTyped(INT_KEY, new Integer(15));
		config.putTyped(HELLO_KEY, "Hello");
		config.putTyped(ANOTHER_INT_KEY, new Integer(10));
		String s = config.toString();
		Assert.assertTrue("toString result should contain expected text", s.contains("java.lang.Integer"));
		Assert.assertTrue("toString result should contain expected text", s.contains("15"));
		Assert.assertTrue("toString result should contain expected text", config.toString().contains("Hello"));
	}
	
	@Test(expected = ClassCastException.class)
	public void testPutInvalidValueType() {
		// config.putTyped(INT_KEY, "Hello"); // correctly not compilable
		Map<TypedKey<String, ?>, Object> map = (Map<TypedKey<String, ?>, Object>)config;
		map.put(INT_KEY, "String");
	}

}
