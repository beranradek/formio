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


/**
 * Validation of IPv4 address.
 * @author Radek Beran
 */
public class IPv4AddressValidation {
	
	/**
	 * Returns true if given string is valid IPv4 address, 
	 * false if it is not or {@code null} or empty string is given.
	 * @param input
	 * @return
	 */
	public static boolean isIPv4Address(String input) {
		if (input == null || input.isEmpty()) return false;
		try {
			String[] parts = input.split("[.]");
            if (parts.length == 4) {
	            for (int i = 0; i < parts.length; i++) {
	                int p = Integer.valueOf(parts[i]).intValue();
	                if (p < 0 || p > 255) {
	                    return false;
	                }
	            }
	            return true;
            }
        } catch (Exception e) {
            return false;
        }
		return false;
	}
	
	private IPv4AddressValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
