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

import org.junit.Test;

public class TypedKeyTest {
	
	private static final TypedKey<String, Integer> intKey1 = DefaultTypedKey.valueOf("keyName", Integer.class);
	private static final TypedKey<String, Integer> intKey2 = DefaultTypedKey.valueOf("keyName", Integer.class);
	private static final TypedKey<String, Integer> intKey3 = DefaultTypedKey.valueOf("anotherKeyName", Integer.class);

	@Test
	public void testInstanceCaching() {
		assertTrue("instances should be the same", intKey1 == intKey2);
		assertTrue("instances should not be the same, names of keys are different", intKey3 != intKey2);
	}
	
	@Test
	public void testGetName() {
		assertEquals("keyName", intKey1.getKey());
	}
	
	@Test
	public void testGetValueClass() {
		assertEquals(Integer.class, intKey1.getValueClass());
	}
	
	@Test
	public void testEquals() {
		assertTrue("Keys should be equal", intKey1.equals(intKey2));
		assertFalse("Keys should not be equal", intKey1.equals(intKey3));
	}
}