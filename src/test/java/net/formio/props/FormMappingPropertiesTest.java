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
import net.formio.FormMapping;
import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.Address;
import net.formio.domain.Car;
import net.formio.domain.CarDimensions;
import net.formio.domain.Registration;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Test mappings with various properties.
 * @author Radek Beran
 */
public class FormMappingPropertiesTest {

	@Test
	public void testDefaultPropertiesOfRootMapping() {
		FormMapping<Registration> filledForm = TestForms.REG_FORM.fill(
			new FormData<Registration>(TestData.newRegistration(), ValidationResult.empty));
		
		assertEquals(FieldProperty.VISIBLE.getDefaultValue(), Boolean.valueOf(filledForm.getFormProperties().isVisible()));
		assertEquals(FieldProperty.ENABLED.getDefaultValue(), Boolean.valueOf(filledForm.getFormProperties().isEnabled()));
		assertEquals(FieldProperty.REQUIRED.getDefaultValue(), Boolean.valueOf(filledForm.getFormProperties().isRequired()));
		assertEquals(FieldProperty.READ_ONLY.getDefaultValue(), Boolean.valueOf(filledForm.getFormProperties().isReadonly()));
	}
	
	@Test
	public void testDefaultPropertiesOfNestedMapping() {
		FormMapping<Registration> filledForm = TestForms.REG_FORM.fill(
			new FormData<Registration>(TestData.newRegistration(), ValidationResult.empty));
		
		FormMapping<Address> address = filledForm.getMapping(Address.class, "contactAddress");
		
		assertEquals(FieldProperty.VISIBLE.getDefaultValue(), Boolean.valueOf(address.getFormProperties().isVisible()));
		assertEquals(FieldProperty.ENABLED.getDefaultValue(), Boolean.valueOf(address.getFormProperties().isEnabled()));
		assertEquals(FieldProperty.REQUIRED.getDefaultValue(), Boolean.valueOf(address.getFormProperties().isRequired()));
		assertEquals(FieldProperty.READ_ONLY.getDefaultValue(), Boolean.valueOf(address.getFormProperties().isReadonly()));
	}
	
	@Test
	public void testInvisibleNestedMapping() {
		FormMapping<Car> form = TestForms.CAR_ACCESSIBILITY_FORM;
		FormMapping<CarDimensions> carMapping = form.getMapping(CarDimensions.class, "dimensions");
		assertEquals(Boolean.FALSE, Boolean.valueOf(carMapping.getFormProperties().isVisible()));
		assertEquals(FieldProperty.ENABLED.getDefaultValue(), Boolean.valueOf(carMapping.getFormProperties().isEnabled()));
		assertEquals(FieldProperty.REQUIRED.getDefaultValue(), Boolean.valueOf(carMapping.getFormProperties().isRequired()));
		assertEquals(FieldProperty.READ_ONLY.getDefaultValue(), Boolean.valueOf(carMapping.getFormProperties().isReadonly()));
		
		FormMapping<Car> filledForm = TestForms.CAR_ACCESSIBILITY_FORM.fill(
			new FormData<Car>(TestData.newCar(), ValidationResult.empty));
		
		FormMapping<CarDimensions> filledCarMapping = filledForm.getMapping(CarDimensions.class, "dimensions");
		
		assertEquals(Boolean.FALSE, Boolean.valueOf(filledCarMapping.isVisible()));
		assertEquals("Should be invisible because parent mapping is invisible", 
			Boolean.FALSE, Boolean.valueOf(filledCarMapping.getFields().get("length").isVisible()));
		assertEquals("Visible property of length should be true even if invisible due to parent", 
			Boolean.TRUE, Boolean.valueOf(filledCarMapping.getFields().get("length").getFormProperties().isVisible()));
		assertEquals(FieldProperty.ENABLED.getDefaultValue(), Boolean.valueOf(filledCarMapping.getFormProperties().isEnabled()));
		assertEquals(FieldProperty.REQUIRED.getDefaultValue(), Boolean.valueOf(filledCarMapping.getFormProperties().isRequired()));
		assertEquals(FieldProperty.READ_ONLY.getDefaultValue(), Boolean.valueOf(filledCarMapping.getFormProperties().isReadonly()));
	}
}
