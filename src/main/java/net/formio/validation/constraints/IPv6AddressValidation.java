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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Validation of IPv6 address.
 * @author Radek Beran
 */
public class IPv6AddressValidation {
	
	/**
	 * Returns true if given string is valid IPv6 address, 
	 * false if it is not or {@code null} or empty string is given.
	 * @param input
	 * @return
	 */
	public static boolean isIPv6Address(String input) {
		if (input == null || input.isEmpty()) return false;
		try {
            InetAddress addr = InetAddress.getByName(input);
            return addr instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
	}
	
	private IPv6AddressValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
