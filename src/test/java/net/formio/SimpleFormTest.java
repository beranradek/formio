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

import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.MarriedPerson;
import net.formio.domain.Nation;
import net.formio.domain.Person;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Simple form processing "top-level" test.
 * @author Radek Beran
 */
public class SimpleFormTest {
	private static final Logger LOG = Logger.getLogger(SimpleFormTest.class.getName());

	@Test
	public void testFormProcessing() {
		try {
			final Locale locale = new Locale("en");
			
			// Filling form with initial data
			FormData<Person> formData = new FormData<Person>(getInitData(), ValidationResult.empty);
			FormMapping<Person> personForm = TestForms.PERSON_FORM;
			FormMapping<Person> filledForm = personForm.fill(formData, locale);
			
			LOG.info("Filled form: \n" + filledForm);
			
			String filledBirthDate = filledForm.getFields().get("birthDate").getValue();
			assertEquals("20-2-1982 11-20", filledBirthDate);
			
			Person filledObject = filledForm.getFilledObject();
			Person initData = getInitData();
			assertInitDataAreFilled(initData, filledObject);
			
			// Preparing data (filled "by the user" into the form)
			MapParams reqParams = getRequestParams();
					
			// Binding data from request to model (Person)
			FormData<Person> boundFormData = personForm.bind(reqParams, locale);
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
	
	@Test
	public void testBindToProvidedInstance() {
		final Locale locale = new Locale("en");
		
		MarriedPerson personToFillFromForm = new MarriedPerson("Charlotte", "Stripes");
		personToFillFromForm.setMarriageDate(new Date());
		personToFillFromForm.setNation(Nation.SLOVAK);
		FormMapping<Person> personForm = TestForms.PERSON_FORM;
		FormData<Person> boundFormData = personForm.bind(getRequestParams(), locale, personToFillFromForm);
		MarriedPerson person = (MarriedPerson)boundFormData.getData();
		
		// Constructor-settable properties are not overriden
		assertEquals("Charlotte", person.getFirstName());
		assertEquals("Stripes", person.getLastName());
		assertEquals(40000, person.getSalary());
		assertEquals("728111222", person.getPhone());
		assertEquals(Boolean.FALSE, Boolean.valueOf(person.isMale()));
		
		// Other properties are overriden from request params
		assertEquals(Nation.JEDI_KNIGHT, person.getNation());
		
		assertNotNull("Marriage date should be filled", person.getMarriageDate());
		assertEquals(personToFillFromForm.getMarriageDate(), person.getMarriageDate());
		assertEquals(personToFillFromForm, person);
	}

	private MapParams getRequestParams() {
		MapParams reqParams = new MapParams();
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
