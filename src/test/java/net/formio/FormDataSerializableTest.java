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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import net.formio.data.TestData;
import net.formio.domain.Person;
import net.formio.utils.SerializationUtils;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link FormData}.
 * @author Radek Beran
 */
public class FormDataSerializableTest {

	private static final Logger LOG = Logger.getLogger(FormDataSerializableTest.class.getName());
	
	@Test
	public void testFormDataIsSerializable() {
		try {
			Map<String, List<ConstraintViolationMessage>> fieldMsgs = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
			List<ConstraintViolationMessage> emailMsgs = new ArrayList<ConstraintViolationMessage>();
			Map<String, Serializable> emailArgs = new LinkedHashMap<String, Serializable>();
			emailArgs.put("value", "invalid-email.com");
			String emailMsgTxt = "E-mail is not valid";
			emailMsgs.add(new ConstraintViolationMessage(Severity.ERROR, emailMsgTxt, "constraints.Email.message", emailArgs));
			fieldMsgs.put("email", emailMsgs);
			List<ConstraintViolationMessage> globalMsgs = new ArrayList<ConstraintViolationMessage>();
			ValidationResult result = new ValidationResult(fieldMsgs, globalMsgs);
			Person person = TestData.newPerson();
			FormData<Person> formData = new FormData<Person>(person, result);
			byte[] serialized = SerializationUtils.serialize(formData);
			FormData<Person> deserialized = (FormData<Person>)SerializationUtils.deserialize(serialized);
			Assert.assertTrue("Data was not correctly deserialized", deserialized != null);
			Assert.assertFalse(deserialized.getValidationResult().isSuccess());
			Assert.assertTrue(deserialized.getValidationResult().getFieldMessages().get("email").size() == 1);
			Assert.assertEquals(emailMsgTxt, deserialized.getValidationResult().getFieldMessages().get("email").iterator().next().getText());
			Assert.assertEquals(person.getLastName(), deserialized.getData().getLastName());
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
			Assert.fail("Form data is not serializable: " + ex.getMessage());
		}
	}

}
