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
		List<FormElementProperty<Object>> props = FormElementProperty.getValues();
		assertTrue("field props should contain visible property", props.contains(FormElementProperty.VISIBLE));
		assertTrue("field props should contain enabled property", props.contains(FormElementProperty.ENABLED));
		assertTrue("field props should contain readonly property", props.contains(FormElementProperty.READ_ONLY));
	}
	
	@Test
	public void testEquality() {
		assertEquals(FormElementProperty.ENABLED, FormElementProperty.ENABLED);
		Property<Boolean> prop = FormElementProperty.ENABLED; 
		assertFalse(FormElementProperty.READ_ONLY == prop);
		assertFalse(FormElementProperty.VISIBLE == prop);
		Property<Boolean> prop2 = prop;
		assertTrue(FormElementProperty.ENABLED == prop2);
	}
	
	@Test
	public void testFromName() {
		assertEquals(FormElementProperty.ENABLED, FormElementProperty.fromName("enabled"));
		assertEquals(null, FormElementProperty.fromName("unknown"));
	}
	
	@Test
	public void testDefaultProperties() {
		assertTrue(FormElementProperty.VISIBLE.getDefaultValue().booleanValue());
		assertTrue(FormElementProperty.ENABLED.getDefaultValue().booleanValue());
		assertFalse(FormElementProperty.READ_ONLY.getDefaultValue().booleanValue());
		assertEquals("", FormElementProperty.HELP.getDefaultValue());
		assertEquals("", FormElementProperty.DATA_AJAX_URL.getDefaultValue());
	}

}
