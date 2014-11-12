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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Map;

import net.formio.props.FieldProperty;

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
		assertEquals(3, config.size());
		Integer i1 = config.getTyped(INT_KEY);
		String str = config.getTyped(HELLO_KEY);
		Integer i2 = config.getTyped(ANOTHER_INT_KEY);
		assertEquals(15, i1.intValue());
		assertEquals("Hello", str);
		assertEquals(10, i2.intValue());
	}
	
	@Test
	public void testPutAllValues() {
		final HeterogMap<String> properties = HeterogCollections.<String>newLinkedMap();
		properties.putTyped(FieldProperty.VISIBLE, Boolean.FALSE);
		properties.putTyped(FieldProperty.ENABLED, Boolean.TRUE);
		properties.putTyped(FieldProperty.REQUIRED, Boolean.FALSE);
		properties.putTyped(FieldProperty.READ_ONLY, Boolean.FALSE);
		final HeterogMap<String> copy = HeterogCollections.<String>newLinkedMap();
		copy.putAllFromSource(properties);
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.VISIBLE));
		assertEquals(Boolean.TRUE, copy.getTyped(FieldProperty.ENABLED));
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.REQUIRED));
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.READ_ONLY));
	}
	
	@Test
	public void testCopyConstructor() {
		final DefaultHeterogMap<String> properties = new DefaultHeterogMap<String>();
		properties.putTyped(FieldProperty.VISIBLE, Boolean.FALSE);
		properties.putTyped(FieldProperty.ENABLED, Boolean.TRUE);
		properties.putTyped(FieldProperty.REQUIRED, Boolean.FALSE);
		properties.putTyped(FieldProperty.READ_ONLY, Boolean.FALSE);
		final DefaultHeterogMap<String> copy = new DefaultHeterogMap<String>(properties);
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.VISIBLE));
		assertEquals(Boolean.TRUE, copy.getTyped(FieldProperty.ENABLED));
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.REQUIRED));
		assertEquals(Boolean.FALSE, copy.getTyped(FieldProperty.READ_ONLY));
	}
	
	@Test
	public void testContainsKey() {
		assertFalse("Container should not contain key", config.containsKey(INT_KEY));
		assertFalse("Container should not contain key", config.containsKey(MISSING_KEY));
		config.putTyped(INT_KEY, new Integer(15));
		assertTrue("Container should contain key", config.containsKey(INT_KEY));
		assertFalse("Container should not contain key", config.containsKey(MISSING_KEY));
	}
	
	@Test
	public void testToString() {
		config.putTyped(INT_KEY, new Integer(15));
		config.putTyped(HELLO_KEY, "Hello");
		config.putTyped(ANOTHER_INT_KEY, new Integer(10));
		String s = config.toString();
		assertTrue("toString result should contain expected text", s.contains("java.lang.Integer"));
		assertTrue("toString result should contain expected text", s.contains("15"));
		assertTrue("toString result should contain expected text", config.toString().contains("Hello"));
	}
	
	@Test(expected = ClassCastException.class)
	public void testPutInvalidValueType() {
		// config.putTyped(INT_KEY, "Hello"); // correctly not compilable
		Map<TypedKey<String, ?>, Object> map = (Map<TypedKey<String, ?>, Object>)config;
		map.put(INT_KEY, "String");
	}

}
