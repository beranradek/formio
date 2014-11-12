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
package net.formio.data;

import java.util.Date;

import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.MappingType;
import net.formio.domain.Address;
import net.formio.domain.BigDecimalValue;
import net.formio.domain.Car;
import net.formio.domain.CarDimensions;
import net.formio.domain.Collegue;
import net.formio.domain.Engine;
import net.formio.domain.NewCollegue;
import net.formio.domain.Person;
import net.formio.domain.RegDate;
import net.formio.domain.Registration;

/**
 * Form definitions for tests.
 * @author Radek Beran
 */
public final class TestForms {

	// immutable definition of the form, can be freely shared/cached
	private static final FormMapping<RegDate> REG_DATE_MAPPING = Forms.basic(RegDate.class, "regDate").fields("month", "year").build();
	public static final FormMapping<Registration> BASIC_REG_FORM = 
		Forms.basic(Registration.class, "registration")
		  // whitelist of formProperties to bind
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
	public static final FormMapping<Registration> REG_FORM = 
		Forms.automatic(Registration.class, "registration")
			.nested(Forms.automatic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance")).build())
			.build();
	
	public static final FormMapping<Person> PERSON_FORM = Forms.basic(Person.class, "person") // NOPMD by Radek on 2.3.14 19:29
		// whitelist of formProperties to bind
		.fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation")
		.field(Forms.<Date>field("birthDate", "text").formatter(TestFormatters.CUSTOM_DATE_FORMATTER).build())
		.build();
	
	public static final FormMapping<BigDecimalValue> VALUE_FORM = 
		Forms.automatic(BigDecimalValue.class, "valueForm").build();
	
	public static final FormMapping<Car> CAR_FORM =
		Forms.automatic(Car.class, "carForm")
		.field(Forms.field("brand").readonly(true).build())
		.field(Forms.field("maxSpeed").enabled(false).build())
		.field(Forms.field("productionYear").visible(false).build())
		// field "color" is automatically created and bound
		// field "description" is automatically created and bound, it has no formProperties
		.nested(Forms.automatic(Engine.class, "engine")
			.field(Forms.field("cylinderCount").required(true).build())	
			.field(Forms.field("volume").help("In units...").build())
			.build())
		.build();
	
	public static final FormMapping<Car> CAR_ACCESSIBILITY_FORM =
		Forms.basic(Car.class, "carForm")
			.field("carId", "hidden")
			.field(Forms.field("brand").readonly(true).build())
			.field(Forms.field("maxSpeed").enabled(false).build())
			.field("productionYear")
			.field(Forms.field("color").visible(false).build())
			.field("description", "textarea")
			.nested(Forms.basic(Engine.class, "engine")
				.field(Forms.field("cylinderCount").required(true).build())	
				.field(Forms.field("volume").help("In units...").build())
				.build())
			.nested(Forms.basic(CarDimensions.class, "dimensions")
				.field("length")	
				.field("width")
				.field("height")
				.visible(false)
				.build())
			.build();
	
	private TestForms() {
	}
}
