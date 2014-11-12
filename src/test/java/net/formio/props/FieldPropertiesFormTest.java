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
package net.formio.props;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.logging.Logger;

import net.formio.FormData;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.data.TestForms;
import net.formio.domain.Car;
import net.formio.domain.Engine;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Test form with various field formProperties.
 * @author Radek Beran
 */
public class FieldPropertiesFormTest {
	private static final Logger LOG = Logger.getLogger(FieldPropertiesFormTest.class.getName());

	@Test
	public void testFormProcessing() {
		try {
			final Locale locale = new Locale("en");
			
			// Filling form with initial data
			FormData<Car> formData = new FormData<Car>(getInitData(), ValidationResult.empty);
			FormMapping<Car> carForm = TestForms.CAR_FORM;
			FormMapping<Car> filledForm = carForm.fill(formData, locale);
			
			LOG.info("Filled form: \n" + filledForm);
			
			assertTrue("brand should be readonly", filledForm.getField(String.class, "brand").isReadonly());
			assertFalse("maxSpeed should be disabled", filledForm.getField(Integer.class, "maxSpeed").isEnabled());
			assertFalse("productionYear should be invisible", filledForm.getField(Integer.class, "productionYear").isVisible());
			assertTrue("color should be required", filledForm.getField(Integer.class, "color").isRequired());
			assertTrue("cylinderCount should be required", filledForm.getNestedByProperty(Engine.class, "engine").getField(Integer.class, "cylinderCount").isRequired());
			assertEquals("In units...", filledForm.getNestedByProperty(Engine.class, "engine").getField(Integer.class, "volume").getHelp());
			
			// Default formProperties:
			FormField<String> descriptionField = filledForm.getField(String.class, "description");
			assertFalse("description field should not be readonly", descriptionField.isReadonly());
			assertTrue("description field should be enabled", descriptionField.isEnabled());
			assertTrue("description field should be visible", descriptionField.isVisible());
			assertFalse("description field should not be required", descriptionField.isRequired());
			assertEquals("description field should have empty help", "", descriptionField.getHelp());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Test failed: " + ex.getMessage());
		}
	}
	
	private Car getInitData() {
		Car car = new Car();
		Engine engine = new Engine();
		engine.setCylinderCount(4);
		engine.setVolume(1800);
		car.setEngine(engine);
		car.setBrand("Porsche");
		car.setMaxSpeed(240);
		car.setProductionYear(2014);
		car.setColor(255);
		car.setDescription("Sport car");
		return car;
	}
}
