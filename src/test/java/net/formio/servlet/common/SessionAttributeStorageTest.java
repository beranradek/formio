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
package net.formio.servlet.common;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

public class SessionAttributeStorageTest {

	@Test
	public void testStoreData() {
		SessionAttributeStorage<String> storage = new SessionAttributeStorage<String>("my_attribute");
		MockHttpSession session = new MockHttpSession();
		assertNull(storage.findData(session));
		
		storage.storeData(session, "Hello");
		
		assertEquals("Hello", storage.findData(session));
		
		assertTrue(storage.isStored(session));
		
		storage.removeData(session);
		
		assertNull(storage.findData(session));
		assertFalse(storage.isStored(session));
	}

}
