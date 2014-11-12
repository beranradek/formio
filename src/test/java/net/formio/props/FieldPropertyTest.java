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
package net.formio.props;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class FieldPropertyTest {

	@Test
	public void testGetValues() {
		List<FieldProperty<Object>> props = FieldProperty.getValues();
		assertTrue("field props should contain visible property", props.contains(FieldProperty.VISIBLE));
		assertTrue("field props should contain enabled property", props.contains(FieldProperty.ENABLED));
		assertTrue("field props should contain readonly property", props.contains(FieldProperty.READ_ONLY));
	}
	
	@Test
	public void testEquality() {
		assertEquals(FieldProperty.ENABLED, FieldProperty.ENABLED);
		Property<Boolean> prop = FieldProperty.ENABLED;
		assertTrue(FieldProperty.ENABLED == prop); 
		assertFalse(FieldProperty.READ_ONLY == prop);
	}
	
	@Test
	public void testFromName() {
		assertEquals(FieldProperty.ENABLED, FieldProperty.fromName("enabled"));
		assertEquals(null, FieldProperty.fromName("unknown"));
	}
	
	@Test
	public void testDefaultProperties() {
		assertTrue(FieldProperty.VISIBLE.getDefaultValue().booleanValue());
		assertTrue(FieldProperty.ENABLED.getDefaultValue().booleanValue());
		assertFalse(FieldProperty.REQUIRED.getDefaultValue().booleanValue());
		assertFalse(FieldProperty.READ_ONLY.getDefaultValue().booleanValue());
		assertEquals("", FieldProperty.HELP.getDefaultValue());
	}

}
