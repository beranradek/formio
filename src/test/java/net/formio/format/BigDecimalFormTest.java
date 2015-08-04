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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.logging.Logger;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.data.TestForms;
import net.formio.domain.BigDecimalValue;
import net.formio.inmemory.MapParams;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Test binding a BigDecimal value to/from the form.
 * @author Radek Beran
 */
public class BigDecimalFormTest {
	private static final Logger LOG = Logger.getLogger(BigDecimalFormTest.class.getName());

	@Test
	public void testFillingBindingOfBigDecimal() {
		try {
			final Location loc = Location.CZECH;

			// Filling form with initial data
			FormData<BigDecimalValue> formData = new FormData<BigDecimalValue>(getInitData(), ValidationResult.empty);
			FormMapping<BigDecimalValue> valueForm = TestForms.VALUE_FORM;
			FormMapping<BigDecimalValue> filledForm = valueForm.fill(formData, loc);

			LOG.info("Filled form: \n" + filledForm);

			String filledValue = filledForm.getFields().get("value").getValue();
			assertEquals("20,99", filledValue);

			assertEquals(getInitData(), filledForm.getFilledObject());
			
			// Preparing data (filled "by the user" into the form)
			MapParams reqParams = new MapParams();
			reqParams.put(valueForm.getName() + valueForm.getConfig().getPathSeparator() + "value", "30,98");

			// Binding data from request to model
			FormData<BigDecimalValue> boundFormData = valueForm.bind(reqParams, loc);
			BigDecimalValue value = boundFormData.getData();

			assertEquals(BigDecimal.valueOf(3098, 2), value.getValue());
			assertTrue(boundFormData.getValidationResult().isSuccess());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Test failed: " + ex.getMessage());
		}
	}
	
	private BigDecimalValue getInitData() {
		BigDecimalValue v = new BigDecimalValue();
		v.setValue(BigDecimal.valueOf(2099, 2)); // 20,99
		return v;
	}

}
