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
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import net.formio.domain.Nation;
import net.formio.domain.Person;
import net.formio.format.BasicFormatters;

import org.junit.Test;

public class BindingTest {

	@Test
	public void testBindToNewEmptyPerson() {
		Map<String, BoundValuesInfo> values = new LinkedHashMap<String, BoundValuesInfo>();
		values.put("personId", BoundValuesInfo.getInstance(new Object[] { Long.valueOf(1) }));
		values.put("firstName", BoundValuesInfo.getInstance(new Object[] { "Michael" }));
		values.put("lastName", BoundValuesInfo.getInstance(new Object[] { "Cane" }));
		values.put("salary", BoundValuesInfo.getInstance(new Object[] { Integer.valueOf(40000) }));
		values.put("male", BoundValuesInfo.getInstance(new Object[] { Boolean.TRUE }));
		values.put("nation", BoundValuesInfo.getInstance(new Object[] { Nation.JEDI_KNIGHT }));
		
		BoundData<Person> filledPerson = createBinding().bindToNewInstance(Person.class, null, values);
		assertNotNull(filledPerson);
		assertNotNull(filledPerson.getData());
	}
	
	@Test
	public void testBindToNewPerson() throws ParseException {
		Map<String, BoundValuesInfo> values = new LinkedHashMap<String, BoundValuesInfo>();
		String firstName = "Michael";
		String lastName = "Cane";
		String birthDate = "28.1.2013 23:51:30";
		String birthDatePattern = "dd.MM.yyyy HH:mm:ss";
		String male = "true";
		String salary = "40000";
		String personId = "123";
		String nation = "CZECH";
		
		values.put("firstName", BoundValuesInfo.getInstance(new Object[] {firstName}));
		values.put("lastName", BoundValuesInfo.getInstance(new Object[] {lastName}));
		values.put("birthDate", BoundValuesInfo.getInstance(new Object[] {birthDate}, birthDatePattern));
		values.put("male", BoundValuesInfo.getInstance(new Object[] {male}));
		values.put("salary", BoundValuesInfo.getInstance(new Object[] {salary}));
		values.put("personId", BoundValuesInfo.getInstance(new Object[] {personId}));
		values.put("nation", BoundValuesInfo.getInstance(new Object[] {nation}));
		
		BoundData<Person> filledPerson = createBinding().bindToNewInstance(Person.class, null, values);
		assertTrue(filledPerson.isSuccessfullyBound());
		Person person = filledPerson.getData();
		assertNotNull(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(lastName, person.getLastName());
		assertEquals(new SimpleDateFormat(birthDatePattern).parse(birthDate), person.getBirthDate());
		assertEquals(Boolean.TRUE, Boolean.valueOf(person.isMale()));
		assertEquals(Integer.valueOf(salary), Integer.valueOf(person.getSalary()));
		assertEquals(Long.valueOf(personId), Long.valueOf(person.getPersonId()));
		assertEquals(Nation.valueOf(nation), person.getNation());
	}
	
	private DefaultBinder createBinding() { return new DefaultBinder(new BasicFormatters()); }

}
