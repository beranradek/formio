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
import net.formio.FormData;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.Car;
import net.formio.domain.Engine;
import net.formio.domain.Registration;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Test form fields with various properties.
 * @author Radek Beran
 */
public class FormElementPropertiesTest {

	@Test
	public void testDefaultFormFieldProperties() {
		FormMapping<Registration> filledForm = TestForms.REG_FORM.fill(
			new FormData<Registration>(TestData.newRegistration(), ValidationResult.empty));
		
		FormField<?> field = filledForm.getFields().get("email");
		assertEquals(FormElementProperty.VISIBLE.getDefaultValue(), Boolean.valueOf(field.getFormProperties().isVisible()));
		assertEquals(FormElementProperty.ENABLED.getDefaultValue(), Boolean.valueOf(field.getFormProperties().isEnabled()));
		assertEquals(FormElementProperty.READ_ONLY.getDefaultValue(), Boolean.valueOf(field.getFormProperties().isReadonly()));
	}
	
	@Test
	public void testFormFieldProperties() {
		FormMapping<Car> form = TestForms.CAR_ACCESSIBILITY_FORM;
		FormField<?> brand = form.getFields().get("brand");
		FormField<?> maxSpeed = form.getFields().get("maxSpeed");
		FormField<?> color = form.getFields().get("color");
		FormField<?> cylinderCount = form.getMapping(Engine.class, "engine").getFields().get("cylinderCount");
		FormField<?> volume = form.getMapping(Engine.class, "engine").getFields().get("volume");
		
		assertEquals(Boolean.TRUE, Boolean.valueOf(brand.getFormProperties().isReadonly()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(maxSpeed.getFormProperties().isEnabled()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(color.getFormProperties().isVisible()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(cylinderCount.isRequired()));
		assertEquals("In units...", volume.getFormProperties().getHelp());
		
		FormMapping<Car> filledForm = TestForms.CAR_ACCESSIBILITY_FORM.fill(
			new FormData<Car>(TestData.newCar(), ValidationResult.empty));
		
		FormField<?> filledBrand = filledForm.getFields().get("brand");
		FormField<?> filledMaxSpeed = filledForm.getFields().get("maxSpeed");
		FormField<?> filledColor = filledForm.getFields().get("color");
		FormField<?> filledCylinderCount = filledForm.getMapping(Engine.class, "engine").getFields().get("cylinderCount");
		FormField<?> filledVolume = filledForm.getMapping(Engine.class, "engine").getFields().get("volume");
		
		assertEquals(Boolean.TRUE, Boolean.valueOf(filledBrand.getFormProperties().isReadonly()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(filledMaxSpeed.getFormProperties().isEnabled()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(filledColor.getFormProperties().isVisible()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(filledCylinderCount.isRequired()));
		assertEquals("In units...", filledVolume.getFormProperties().getHelp());
	}
}
