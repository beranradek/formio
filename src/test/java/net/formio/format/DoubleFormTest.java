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
package net.formio.format;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.data.TestForms;
import net.formio.domain.DoubleValue;
import net.formio.inmemory.MapParams;
import net.formio.validation.ValidationResult;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Test binding a Double value to/from the form.
 * @author Radek Beran
 */
public class DoubleFormTest {
	private static final Logger LOG = Logger.getLogger(DoubleFormTest.class.getName());

	@Test
	public void testFillingBindingOfDouble() {
		try {
			final Location loc = Location.CZECH;

			// Filling form with initial data
			FormData<DoubleValue> formData = new FormData<DoubleValue>(getInitData(), ValidationResult.empty);
			FormMapping<DoubleValue> valueForm = TestForms.DOUBLE_VALUE_FORM;
			FormMapping<DoubleValue> filledForm = valueForm.fill(formData, loc);

			LOG.info("Filled form: \n" + filledForm);

			String filledValue = filledForm.getFields().get("value").getValue();
			assertEquals("300", filledValue);

			assertEquals(getInitData(), filledForm.getFilledObject());
			
			// Preparing data (filled "by the user" into the form)
			MapParams reqParams = new MapParams();
			reqParams.put(valueForm.getName() + valueForm.getConfig().getPathSeparator() + "value", "40,000");

			// Binding data from request to model
			FormData<DoubleValue> boundFormData = valueForm.bind(reqParams, loc);
			DoubleValue value = boundFormData.getData();

			//assertEquals(new Double(40000d), value.getValue());
			//assertTrue(boundFormData.getValidationResult().isSuccess());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Test failed: " + ex.getMessage());
		}
	}
	
	private DoubleValue getInitData() {
		DoubleValue v = new DoubleValue();
		v.setValue(Double.valueOf(300));
		return v;
	}

}
