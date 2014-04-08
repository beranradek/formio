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
package net.formio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import net.formio.domain.Nation;
import net.formio.domain.Person;
import net.formio.format.Formatter;
import net.formio.format.StringParseException;

import org.junit.Test;

/**
 * Simple form processing "top-level" test.
 * @author Radek Beran
 */
public class SimpleFormTest {
	private static final Logger LOG = Logger.getLogger(SimpleFormTest.class.getName());
	
	private static final Formatter<Date> CUSTOM_DATE_FORMATTER = new Formatter<Date>() {
		private static final String FIXED_FORMAT = "d-M-yyyy HH-mm";
		
		@Override
		public Date parseFromString(String str, Class<Date> destClass,
				String formatPattern, Locale locale) {
			try {
				return new SimpleDateFormat(FIXED_FORMAT).parse(str);
			} catch (Exception ex) {
				throw new StringParseException(Date.class, str, ex);
			}
		}
		
		@Override
		public String makeString(Date value, String formatPattern, Locale locale) {
			return new SimpleDateFormat(FIXED_FORMAT).format(value);
		}
	};
	
	// immutable definition of the form, can be freely shared/cached
	private static final FormMapping<Person> PERSON_FORM = Forms.basic(Person.class, "person") // NOPMD by Radek on 2.3.14 19:29
		// whitelist of properties to bind
		.fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation")
		.field("birthDate", CUSTOM_DATE_FORMATTER)
		.build();

	@Test
	public void testFormProcessing() {
		try {
			// Filling form with initial data
			FormData<Person> formData = new FormData<Person>(getInitData(), null);
			FormMapping<Person> filledForm = PERSON_FORM.fill(formData);
			
			LOG.info("Filled form: \n" + filledForm);
			
			String filledBirthDate = filledForm.getFields().get("birthDate").getValue();
			assertEquals("20-2-1982 11-20", filledBirthDate);
			
			Person filledObject = (Person)filledForm.getFilledObject();
			Person initData = getInitData();
			assertInitDataAreFilled(initData, filledObject);
			
			// Preparing data (filled "by the user" into the form)
			MapParamsProvider reqParams = getRequestParams();
					
			// Binding data from request to model (Person)
			FormData<Person> boundFormData = PERSON_FORM.bind(reqParams);
			Person person = boundFormData.getData();
			
			assertEquals(1, person.getPersonId());
			assertEquals("Michel", person.getFirstName());
			assertEquals("Rider", person.getLastName());
			assertEquals(40000, person.getSalary());
			assertEquals("728111222", person.getPhone());
			assertEquals(Boolean.FALSE, Boolean.valueOf(person.isMale()));
			assertNotNull("bound birth date should not be null", person.getBirthDate());
			assertEquals(new SimpleDateFormat("d.M.yyyy HH:mm").parse("23.4.1985 10:30").getTime(), person.getBirthDate().getTime());
			assertEquals(Nation.JEDI_KNIGHT, person.getNation());
			
			assertTrue(boundFormData.getValidationResult().isSuccess());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Test failed: " + ex.getMessage());
		}
	}

	private MapParamsProvider getRequestParams() {
		MapParamsProvider reqParams = new MapParamsProvider();
		String sep = Forms.PATH_SEP;
		reqParams.put("person" + sep + "personId", "1");
		reqParams.put("person" + sep + "firstName", "Michel");
		reqParams.put("person" + sep + "lastName", "Rider");
		reqParams.put("person" + sep + "salary", "40000");
		reqParams.put("person" + sep + "phone", "728111222");
		reqParams.put("person" + sep + "male", "false");
		reqParams.put("person" + sep + "birthDate", "23-4-1985 10-30");
		reqParams.put("person" + sep + "nation", "JEDI_KNIGHT");
		return reqParams;
	}

	private void assertInitDataAreFilled(Person initData, Person filledObject) {
		assertEquals(initData.getPersonId(), filledObject.getPersonId());
		assertEquals(initData.getFirstName(), filledObject.getFirstName());
		assertEquals(initData.getLastName(), filledObject.getLastName());
		assertEquals(initData.getSalary(), filledObject.getSalary());
		assertEquals(initData.getPhone(), filledObject.getPhone());
		assertEquals(Boolean.valueOf(initData.isMale()), Boolean.valueOf(filledObject.isMale()));
		assertEquals(initData.getBirthDate().getTime(), filledObject.getBirthDate().getTime());
		assertEquals(initData.getNation(), filledObject.getNation());
	}
	
	private Person getInitData() {
		return TestData.newPerson();
	}

}
