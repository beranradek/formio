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
package net.formio.render;

import static org.junit.Assert.assertNotNull;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.inputs.Profile;
import net.formio.format.Location;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link FormRenderer}.
 * @author Radek Beran
 */
public class FormRendererTest {

	@Test
	public void testRenderForm() {
		final Location loc = Location.ENGLISH;
		Profile inputs = TestData.newAllFields();
		String pathSep = TestForms.ALL_FIELDS_FORM.getConfig().getPathSeparator();
		
		// Validation errors will be displayed when the form is shown for the first time
		// (due to fillAndValidate call)
		FormMapping<Profile> filledForm = TestForms.ALL_FIELDS_FORM.fillAndValidate(new FormData<Profile>(inputs, ValidationResult.empty), loc);
		assertNotNull(filledForm.getValidationResult().getFieldMessages().get("profile" + pathSep + "employers[0]" + pathSep + "fromYear"));
		
		Forms.previewForm(filledForm, loc);
	}

}
