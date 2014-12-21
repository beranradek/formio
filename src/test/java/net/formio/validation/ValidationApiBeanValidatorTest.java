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
package net.formio.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.formio.binding.ParseError;
import net.formio.upload.RequestProcessingError;
import net.formio.validation.constraints.NotEmpty;

import org.junit.Test;

/**
 * Tests for {@link ValidationApiBeanValidator}.
 * 
 * @author Radek Beran
 */
public class ValidationApiBeanValidatorTest {

	@Test
	public void testIsRequired() {
		try {
			ValidationApiBeanValidator v = new ValidationApiBeanValidator(
				Validation.buildDefaultValidatorFactory(), "whatever");
			assertTrue("name is required", v.isRequired(Contact.class, "name"));
			assertTrue("email is required", v.isRequired(Contact.class, "email"));
			assertTrue("age is required", v.isRequired(Contact.class, "age"));
			assertFalse("phone is not required", v.isRequired(Contact.class, "phone"));
			assertTrue("sizes are required", v.isRequired(Contact.class, "sizes"));
			assertFalse("sizes2 are not required", v.isRequired(Contact.class, "sizes2"));
			assertTrue("interests are required", v.isRequired(Contact.class, "interests"));
			assertFalse("interestsByCodes are not required", v.isRequired(Contact.class, "interestsByCodes"));

			Contact c = new Contact();
			c.setEmail("e");
			c.setName("n");
			c.setPhone("p");
			c.setNewContact(false);
			c.setAge(Integer.valueOf(0));
			c.setSizes(new int[] {90, 60, 90});
			c.setSizes2(new int[] {});
			c.setInterests(new String[] {"a", "b", "c"});
			ValidationResult r = v.validate(c, "",
				Collections.<RequestProcessingError> emptyList(),
				Collections.<ParseError> emptyList(),
				new Locale("cs", "CZ"));
			assertTrue("Validation should be successfull", r.isSuccess());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

	static class Contact {

		private boolean newContact;
		
		@NotNull
		private Integer age;

		@Size(min = 1)
		private String name;

		@NotEmpty
		private String email;

		private String phone;
		
		@NotEmpty
		private int[] sizes;
		
		private int[] sizes2;
		
		@NotEmpty
		private String[] interests;
		
		private Map<String, String> interestsByCodes;

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public boolean isNewContact() {
			return newContact;
		}

		public void setNewContact(boolean newContact) {
			this.newContact = newContact;
		}

		public int[] getSizes() {
			return sizes;
		}

		public void setSizes(int[] sizes) {
			this.sizes = sizes;
		}

		public int[] getSizes2() {
			return sizes2;
		}

		public void setSizes2(int[] sizes2) {
			this.sizes2 = sizes2;
		}

		public String[] getInterests() {
			return interests;
		}

		public void setInterests(String[] interests) {
			this.interests = interests;
		}

		public Map<String, String> getInterestsByCodes() {
			return interestsByCodes;
		}

		public void setInterestsByCodes(Map<String, String> interestsByCodes) {
			this.interestsByCodes = interestsByCodes;
		}

	}

}
