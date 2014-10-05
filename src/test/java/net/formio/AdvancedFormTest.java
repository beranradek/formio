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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.data.TestParams;
import net.formio.domain.AttendanceReason;
import net.formio.domain.Collegue;
import net.formio.domain.NewCollegue;
import net.formio.domain.RegDate;
import net.formio.domain.Registration;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Advanced form processing "top-level" test.
 * @author Radek Beran
 */
public class AdvancedFormTest {
	private static final Logger LOG = Logger.getLogger(AdvancedFormTest.class.getName());
	
	@Test
	public void testBasicFormProcessing() {
		testFormProcessingInternal(TestForms.BASIC_REG_FORM);
	}
	
	@Test
	public void testAutomaticFormProcessing() {
		testFormProcessingInternal(TestForms.REG_FORM);
	}
	
	@Test
	public void testBindToProvidedInstance() {
		Registration regToFillFromForm = new Registration(Collections.<AttendanceReason>emptySet());
		NewCollegue col = new NewCollegue();
		col.setEmail("collegue@email.en");
		regToFillFromForm.setNewCollegue(col);
		
		FormMapping<Registration> regForm = TestForms.REG_FORM;
		FormData<Registration> boundFormData = regForm.bind(TestParams.newRegistrationParams(), new Locale("en"), regToFillFromForm);
		Registration reg = boundFormData.getData();
		
		assertEquals(regToFillFromForm.getNewCollegue(), reg.getNewCollegue());
		assertEquals("Joshua", reg.getNewCollegue().getName());
		assertEquals(null, reg.getNewCollegue().getEmail()); // filled by empty req. param
		assertEquals("invalidemail.com", reg.getEmail());
	}
	
	@Test
	public void testBindToListElementOfProvidedInstance() {
		Registration regToFillFromForm = new Registration(Collections.<AttendanceReason>emptySet());
		List<Collegue> collegues = new ArrayList<Collegue>();
		collegues.add(new Collegue());
		Collegue c = new Collegue();
		c.setName("George");
		c.setRegDate(new RegDate(8, 2015));
		collegues.add(c);
		collegues.add(new Collegue());
		regToFillFromForm.setCollegues(collegues);
		
		FormMapping<Registration> regForm = TestForms.REG_FORM;
		FormData<Registration> boundFormData = regForm.bind(TestParams.newRegistrationParams(), new Locale("en"), regToFillFromForm);
		Registration reg = boundFormData.getData();
		
		assertEquals("Michael", reg.getCollegues().get(0).getName());
		assertEquals(collegues.get(1), reg.getCollegues().get(1));
		assertEquals("Natalie", reg.getCollegues().get(1).getName());
		assertEquals(2015, reg.getCollegues().get(1).getRegDate().getYear());
		assertEquals(8, reg.getCollegues().get(1).getRegDate().getMonth());
	}
	
	private void testFormProcessingInternal(FormMapping<Registration> form) {
		// Initial form definition
		assertNotNull("nested mappings of root mapping should not be null", form.getNested());
		assertEquals("root mapping should have 3 nested mappings", 3, form.getNested().size());
		// TODO: Asserts on nested mappings and their fields
		
		assertNotNull("fields of root mapping should not be null", form.getFields());
		assertEquals("root mapping should have 5 fields", 5, form.getFields().size());
		// TODO: Asserts on fields
		
		assertNull("filled object of root mapping should be null when the form is not filled yet", form.getFilledObject());
		assertTrue("root mapping should have empty list mappings", form.getList().isEmpty());
		
		assertEquals("root mapping should have correct name", "registration", form.getName());
		assertEquals("root mapping should have correct data class", Registration.class, form.getDataClass());
		assertEquals("root mapping should have correct label key", "registration", form.getLabelKey());
		assertNull("root mapping has no validation report yet before filling", form.getValidationResult());
		
		final Locale locale = new Locale("en"); 
		
		// Filled form
		FormData<Registration> formData = new FormData<Registration>(TestData.newRegistration(), ValidationResult.empty);
		FormMapping<Registration> filledForm = form.fill(formData, locale);
		LOG.info("Filled form: \n" + filledForm);
		
		assertEquals("filled root mapping should have correct name", "registration", filledForm.getName());
		assertEquals("filled root mapping should have correct data class", Registration.class, filledForm.getDataClass());
		assertEquals("filled root mapping should have correct label key", "registration", filledForm.getLabelKey());
		assertNotNull("filled object should not be null", filledForm.getFilledObject());
		assertTrue("filled object should be of appropriate class", filledForm.getFilledObject() instanceof Registration);
		
		Registration filledObject = (Registration)filledForm.getFilledObject();
		Registration initData = TestData.newRegistration();
		assertEquals(initData.getAttendanceReasons(), filledObject.getAttendanceReasons());
		assertEquals(initData.getInterests().length, filledObject.getInterests().length);
		assertEquals(2, filledObject.getInterests().length);
		assertEquals(initData.getInterests()[0], filledObject.getInterests()[0]);
		assertEquals(initData.getInterests()[1], filledObject.getInterests()[1]);
		// TODO: Another asserts
		
		
		// Binding data from the form to a model
		final String sep = Forms.PATH_SEP;
		
		// Binding form data to model (Registration)
		FormData<Registration> boundFormData = form.bind(TestParams.newRegistrationParams(), locale);
		final Registration boundReg = boundFormData.getData();
		
		assertNotNull("bound object should not be null", boundReg);
		assertEquals(2, boundReg.getAttendanceReasons().size());
		assertTrue(boundReg.getAttendanceReasons().contains(AttendanceReason.COMPANY_INTEREST));
		assertTrue(boundReg.getAttendanceReasons().contains(AttendanceReason.CERTIFICATION));
		assertEquals("invalidemail.com", boundReg.getEmail());
		assertEquals(2014, boundReg.getNewCollegue().getRegDate().getYear());
		assertEquals(11, boundReg.getNewCollegue().getRegDate().getMonth());
		// TODO: Another asserts
		
		
		// Validation report
		assertTrue(!boundFormData.isValid());
		ValidationResult report = boundFormData.getValidationResult();
		assertTrue(report.getFieldMessages().containsKey("registration" + sep + "email"));
		List<ConstraintViolationMessage> msgSet = report.getFieldMessages().get("registration" + sep + "email");
		assertEquals(1, msgSet.size());
		final ConstraintViolationMessage msg = msgSet.iterator().next();
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals("Please enter valid e-mail.", msg.getText());
	}

}
