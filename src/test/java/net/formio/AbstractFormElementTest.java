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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import jakarta.validation.Validation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import net.formio.binding.DefaultBeanExtractor;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.DefaultBeanValidator;
import net.formio.validation.ValidationResult;
import net.formio.validation.constraints.NotEmpty;

import org.junit.Test;

public class AbstractFormElementTest {

	@Test
	public void testIsRequired() {
		try {
			assertFalse("name is not required, only size of not-null value is validated", ((AbstractFormElement<?>)Forms.field("name").build()).isRequired(Contact.class));
			assertTrue("email is required", ((AbstractFormElement<?>)Forms.field("email").build()).isRequired(Contact.class));
			assertTrue("age is required", ((AbstractFormElement<?>)Forms.field("age").build()).isRequired(Contact.class));
			assertFalse("phone is not required", ((AbstractFormElement<?>)Forms.field("phone").build()).isRequired(Contact.class));
			assertTrue("sizes are required", ((AbstractFormElement<?>)Forms.field("sizes").build()).isRequired(Contact.class));
			assertFalse("sizes2 are not required", ((AbstractFormElement<?>)Forms.field("sizes2").build()).isRequired(Contact.class));
			assertTrue("interests are required", ((AbstractFormElement<?>)Forms.field("interests").build()).isRequired(Contact.class));
			assertFalse("interestsByCodes are not required", ((AbstractFormElement<?>)Forms.field("interestsByCodes").build()).isRequired(Contact.class));
			assertTrue("interestsByCodes are required when the required property is set", ((AbstractFormElement<?>)Forms.field("interestsByCodes").required(true).build()).isRequired(Contact.class));

			Contact c = new Contact();
			c.setEmail("e");
			c.setName("n");
			c.setPhone("p");
			c.setNewContact(false);
			c.setAge(Integer.valueOf(0));
			c.setSizes(new int[] {90, 60, 90});
			c.setSizes2(new int[] {});
			c.setInterests(new String[] {"a", "b", "c"});
			DefaultBeanValidator v = new DefaultBeanValidator(Validation.buildDefaultValidatorFactory(), new DefaultBeanExtractor());
			ValidationResult r = v.validate(c, "",
				Collections.<InterpolatedMessage> emptyList(),
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
