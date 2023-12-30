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
package net.formio.domain;

import java.io.Serializable;

import jakarta.validation.constraints.Pattern;

import net.formio.binding.ArgumentName;

public final class Address implements Serializable {
	private static final long serialVersionUID = 2293884142466950281L;
	private String street;
	private String city;
	
	@Pattern(message="{constraints.psc.invalid}", regexp="(|\\d{5})") // empty or 5 digits
	private String zipCode;
	
	public static Address getInstance(
		@ArgumentName("street") String street, 
		@ArgumentName("city") String city, 
		@ArgumentName("zipCode") String zipCode) {
		return new Address(street, city, zipCode);
	}
		
	private Address(String street, String city, String zipCode) {
		this.street = street;
		this.city = city;
		this.zipCode = zipCode;
	}
	
	public boolean isEmpty() {
		return isEmptyString(street);
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	private boolean isEmptyString(String str) {
		return str == null || str.isEmpty();
	}

}
