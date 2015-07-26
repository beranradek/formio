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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import net.formio.data.TestForms;
import net.formio.data.TestParams;
import net.formio.domain.NewCollegue;
import net.formio.domain.NewCollegue.New;
import net.formio.domain.Registration;
import net.formio.format.Location;
import net.formio.inmemory.MapParams;

import org.junit.Test;

/**
 * @author Radek Beran
 */
public class BindNestedMappingOnlyTest {

	@Test
	public void testBindToNestedMappingOnly() {
		FormMapping<Registration> regForm = TestForms.BASIC_REG_FORM;
		final Location loc = Location.ENGLISH;
		
		// Binding data from request
		FormMapping<NewCollegue> newCollegueMapping = regForm.getMapping(NewCollegue.class, "newCollegue");
		
		MapParams params = TestParams.newRegistrationCollegueParams();
		final String sep = Forms.PATH_SEP;
		params.put("registration" + sep + "email", "invalidemail.com"); // unrelated param
		FormData<NewCollegue> formData = newCollegueMapping.bind(params, loc, New.class);
		NewCollegue newCollegue = formData.getData();
		assertEquals(2014, newCollegue.getRegDate().getYear());
		assertEquals(11, newCollegue.getRegDate().getMonth());
		assertEquals("Joshua", newCollegue.getName());
		assertFalse("Form should not be valid, email is not filled", formData.isValid());
		assertEquals(1, formData.getValidationResult().getFieldMessages().size());
		
		// What to do next if this partial binding is meant as a part of processing an AJAX request:
		if (formData.isValid()) {
			// add new collegue and refresh end of list with collegues (added collegue)
			// refresh part of page with new collegue data (empty fields)
		} else {
			// refresh part of page with new collegue data (show validation errors)
		}
	}

}
