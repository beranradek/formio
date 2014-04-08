/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.formio.binding.PrimitiveType;

import org.junit.Test;

public class PrimitiveTypeTest {

	@Test
	public void testIsPrimitiveType() {
		assertEquals(Boolean.TRUE, Boolean.valueOf(PrimitiveType.isPrimitiveType(int.class)));
		assertEquals(Boolean.FALSE, Boolean.valueOf(PrimitiveType.isPrimitiveType(Integer.class)));
	}

	@Test
	public void testByPrimitiveClass() {
		PrimitiveType ptype = PrimitiveType.byPrimitiveClass(int.class);
		assertNotNull("Primitive type null", ptype);
		assertEquals(int.class, ptype.getPrimitiveClass());
		assertEquals(Integer.class, ptype.getWrapperClass());
		assertEquals(Integer.valueOf(0), ptype.getInitialValue());
	}
	
	@Test
	public void testByClasses() {
		assertEquals(null, PrimitiveType.byClasses(int.class, Boolean.class));
		assertNotNull("Returned type is null", PrimitiveType.byClasses(int.class, Integer.class));
	}

}
