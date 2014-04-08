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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.MapParamsProvider;
import net.formio.MappingType;
import net.formio.domain.Address;
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
	
	// immutable definition of the form, can be freely shared/cached
	private static final FormMapping<RegDate> REG_DATE_MAPPING = Forms.basic(RegDate.class, "regDate").fields("month", "year").build();
	private static final FormMapping<Registration> BASIC_REG_FORM = 
		Forms.basic(Registration.class, "registration")
		  // whitelist of properties to bind
		  .fields("attendanceReasons", "cv", "certificates", "interests", "email")
		  .nested(Forms.basic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance"))
			.fields("street", "city", "zipCode").build())
		  .nested(Forms.basic(Collegue.class, "collegues", null, MappingType.LIST)
		    .fields("name", "email")
		    .nested(REG_DATE_MAPPING)
		    .build())
		  .nested(Forms.basic(NewCollegue.class, "newCollegue")
		    .fields("name", "email")
		    .nested(REG_DATE_MAPPING)
		    .build())
		  .build();
	
	// equivalent definition of the form, can be freely shared/cached
	private static final FormMapping<Registration> REGISTRATION_FORM = 
		Forms.automatic(Registration.class, "registration")
			.nested(Forms.automatic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance")).build())
			.build();
	
	@Test
	public void testBasicFormProcessing() {
		testFormProcessingWithDef(BASIC_REG_FORM);
	}
	
	@Test
	public void testAutomaticFormProcessing() {
		testFormProcessingWithDef(REGISTRATION_FORM);
	}
	
	public void testFormProcessingWithDef(FormMapping<Registration> form) {
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
		
		
		// Filled form
		FormData<Registration> formData = new FormData<Registration>(getInitData(), null);
		FormMapping<Registration> filledForm = form.fill(formData);
		LOG.info("Filled form: \n" + filledForm);
		
		assertEquals("filled root mapping should have correct name", "registration", filledForm.getName());
		assertEquals("filled root mapping should have correct data class", Registration.class, filledForm.getDataClass());
		assertEquals("filled root mapping should have correct label key", "registration", filledForm.getLabelKey());
		assertNotNull("filled object should not be null", filledForm.getFilledObject());
		assertTrue("filled object should be of appropriate class", filledForm.getFilledObject() instanceof Registration);
		
		Registration filledObject = (Registration)filledForm.getFilledObject();
		Registration initData = getInitData();
		assertEquals(initData.getAttendanceReasons(), filledObject.getAttendanceReasons());
		assertEquals(initData.getInterests().length, filledObject.getInterests().length);
		assertEquals(2, filledObject.getInterests().length);
		assertEquals(initData.getInterests()[0], filledObject.getInterests()[0]);
		assertEquals(initData.getInterests()[1], filledObject.getInterests()[1]);
		// TODO: Another asserts
		
		
		// Binding data from the form to a model
		// Preparing data (filled "by the user" into the form)
		final String sep = Forms.PATH_SEP;
		final MapParamsProvider reqParams = new MapParamsProvider();
		reqParams.put("registration" + sep + "email", "invalidemail.com");
		reqParams.put("registration" + sep + "attendanceReasons", 
			new String[] { AttendanceReason.COMPANY_INTEREST.name(), AttendanceReason.CERTIFICATION.name() });
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "year", "2014");
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "month", "11");
		
		// Binding form data to model (Registration)
		FormData<Registration> boundFormData = form.bind(reqParams);
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
		Set<ConstraintViolationMessage> msgSet = report.getFieldMessages().get("registration" + sep + "email");
		assertEquals(1, msgSet.size());
		final ConstraintViolationMessage msg = msgSet.iterator().next();
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals("Please enter valid e-mail.", msg.getText());
	}
	
	private Registration getInitData() {
		final Set<AttendanceReason> attendanceReasons = new HashSet<AttendanceReason>();
		attendanceReasons.add(AttendanceReason.COMPANY_INTEREST);
		Registration reg = new Registration(attendanceReasons);
		reg.setInterests(new int[] {Registration.DATA_STRUCTURES.getInterestId(), Registration.WEB_FRAMEWORKS.getInterestId()});
		reg.setContactAddress(Address.getInstance("Milady Horakove 22", "Praha", "16000"));
		
		List<Collegue> collegues = new ArrayList<Collegue>();
		final Collegue michael = new Collegue();
		michael.setName("Michael");
		michael.setEmail("michael@email.com");
		collegues.add(michael);
		
		Collegue jane = new Collegue();
		jane.setName("Jane");
		jane.setEmail("jane@email.com");
		collegues.add(jane);
		
		reg.setCollegues(collegues);
		
		reg.setNewCollegue(new NewCollegue());
		return reg;
	}

}
