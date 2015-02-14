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

import java.util.List;

import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.Address;
import net.formio.domain.Collegue;
import net.formio.domain.Engine;
import net.formio.domain.Person;
import net.formio.domain.Registration;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link BasicFormMapping}.
 * @author Radek Beran
 */
public class BasicFormMappingTest {
	
	@Test
	public void testIsRootMapping() {
		assertTrue(((BasicFormMapping<Person>)TestForms.PERSON_FORM).isRootMapping());
		assertFalse(((BasicFormMapping<Engine>)TestForms.CAR_FORM.getMapping(Engine.class, "engine")).isRootMapping());
	}
	
	@Test
	public void testGetRootMappingPath() {
		assertEquals("carForm", ((BasicFormMapping<Engine>)TestForms.CAR_FORM.getMapping(Engine.class, "engine")).getRootMappingPath());
	}
	
	@Test
	public void testGetName() {
		assertEquals("person", TestForms.PERSON_FORM.getName());
		assertEquals("carForm" + Forms.PATH_SEP + "engine", TestForms.CAR_FORM.getMapping(Engine.class, "engine").getName());
	}

	@Test
	public void testGetDataClass() {
		assertEquals(Person.class, TestForms.PERSON_FORM.getDataClass());
		assertEquals(Engine.class, TestForms.CAR_FORM.getMapping(Engine.class, "engine").getDataClass());
	}
	
	@Test
	public void testWithOrder() {
		FormMapping<Person> person = TestForms.PERSON_FORM;
		FormMapping<Person> student = person.withOrder(3);
		assertEquals(3, student.getOrder());
		assertEquals("Original mapping name should remain unchanged", "person", person.getName());
		assertEquals("Original order should remain unchanged", 0, person.getOrder());
	}
	
	@Test
	public void testGetElements() {
		FormMapping<Registration> form = TestForms.BASIC_REG_FORM;
		testBasicRegFormElements(form);
		
		FormMapping<Registration> filledForm = TestForms.BASIC_REG_FORM.fill(
			new FormData<Registration>(TestData.newRegistration(), ValidationResult.empty));
		testBasicRegFormElements(filledForm);
	}
	
	private void testBasicRegFormElements(FormMapping<Registration> mapping) {
		String pathSep = Forms.PATH_SEP;
		String rootMappingName = "registration";
		List<FormElement> elements = mapping.getElements();
		assertEquals(8, elements.size());
		assertEquals(rootMappingName + pathSep + "attendanceReasons", elements.get(0).getName());
		assertEquals(rootMappingName + pathSep + "cv", elements.get(1).getName());
		assertEquals(rootMappingName + pathSep + "certificates", elements.get(2).getName());
		assertEquals(rootMappingName + pathSep + "interests", elements.get(3).getName());
		assertEquals(rootMappingName + pathSep + "email", elements.get(4).getName());
		assertEquals(rootMappingName + pathSep + "contactAddress", elements.get(5).getName());
		assertEquals(rootMappingName + pathSep + "collegues", elements.get(6).getName());
		assertEquals(rootMappingName + pathSep + "newCollegue", elements.get(7).getName());
		int index = 0;
		for (FormElement el : elements) {
			assertEquals(index, el.getOrder());
			index++;
		}
		
		FormMapping<Address> contactAddress = mapping.getMapping(Address.class, "contactAddress");
		List<FormElement> addressElements = contactAddress.getElements();
		assertEquals(rootMappingName + pathSep + "contactAddress" + pathSep + "street", addressElements.get(0).getName());
		assertEquals(rootMappingName + pathSep + "contactAddress" + pathSep + "city", addressElements.get(1).getName());
		assertEquals(rootMappingName + pathSep + "contactAddress" + pathSep + "zipCode", addressElements.get(2).getName());
		
		FormMapping<Collegue> collegues = mapping.getMapping(Collegue.class, "collegues");
		index = 0;
		for (FormMapping<Collegue> indexedCollegues : collegues.getList()) {
			assertEquals(index, indexedCollegues.getOrder());
			List<FormElement> elems = indexedCollegues.getElements();
			assertEquals(rootMappingName + pathSep + "collegues[" + index + "]" + pathSep + "name", elems.get(0).getName());
			assertEquals(rootMappingName + pathSep + "collegues[" + index + "]" + pathSep + "email", elems.get(1).getName());
			assertEquals(rootMappingName + pathSep + "collegues[" + index + "]" + pathSep + "regDate", elems.get(2).getName());
			index++;
		}
	}
}
