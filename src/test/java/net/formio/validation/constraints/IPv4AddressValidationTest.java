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

import org.junit.Test;

public class IPv4AddressValidationTest {

	@Test
	public void testIsIPv4Address() {
		assertFalse("should not be valid address", IPv4AddressValidation.isIPv4Address(null));
		assertFalse("should not be valid address", IPv4AddressValidation.isIPv4Address(""));
		assertFalse("should not be valid address", IPv4AddressValidation.isIPv4Address("1a.2b.3c.4d"));
		
		assertTrue("should be valid address", IPv4AddressValidation.isIPv4Address("127.0.0.1"));
		assertTrue("should be valid address", IPv4AddressValidation.isIPv4Address("255.255.255.255"));
	}

}
