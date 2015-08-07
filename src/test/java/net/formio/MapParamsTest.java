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
package net.formio;

import static org.junit.Assert.*;
import net.formio.ajax.AjaxParams;
import net.formio.inmemory.MapParams;

import org.junit.Test;

public class MapParamsTest {

	@Test
	public void testToString() {
		MapParams params = new MapParams();
		params.put("a", "1");
		params.put("b", "2");
		params.put("some_parameter", "Some very long value for some parameter that should be truncated in toString");
		String str = params.toString();
		assertTrue(str.contains("a=1"));
		assertTrue(str.contains("b=2"));
		assertTrue(str.contains("Some very long value"));
		assertFalse(str.contains("in toString"));
	}
	
	@Test
	public void testIsTdiAjaxRequest() {
		MapParams params = new MapParams();
		params.put(AjaxParams.INFUSE, "1");
		assertTrue("Should be TDI AJAX request", params.isTdiAjaxRequest());
	}

	@Test
	public void testIsNotTdiAjaxRequest() {
		MapParams params = new MapParams();
		assertFalse("Should NOT be TDI AJAX request", params.isTdiAjaxRequest());
	}
	
	@Test
	public void testGetTdiAjaxSrcElement() {
		MapParams params = new MapParams();
		String elementName = "age";
		params.put(AjaxParams.SRC_ELEMENT_NAME, elementName);
		assertEquals(elementName, params.getTdiAjaxSrcElementName());
	}
	
	@Test
	public void testMissingTdiAjaxSrcElement() {
		MapParams params = new MapParams();
		assertNull("Should be null", params.getTdiAjaxSrcElementName());
	}
}
