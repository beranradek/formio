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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.formio.Field;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.MappingType;
import net.formio.choice.ChoiceItem;
import net.formio.choice.ChoiceRenderer;
import net.formio.choice.EnumChoiceProvider;
import net.formio.domain.Address;
import net.formio.domain.inputs.Country;
import net.formio.domain.inputs.Employer;
import net.formio.domain.inputs.Function;
import net.formio.domain.inputs.Profile;
import net.formio.domain.inputs.Salutation;
import net.formio.domain.inputs.Skill;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link BasicFormRenderer}.
 * @author Radek Beran
 */
public class BasicFormRendererTest {
	
	private static final FormMapping<Profile> profileForm = Forms.basic(Profile.class, "profile")
		.field("profileId", Field.HIDDEN)
		.field(Forms.<Salutation>field("salutation", Field.RADIO_CHOICE)
			.choices(new EnumChoiceProvider<Salutation>(Salutation.class)))
		.field("firstName", Field.TEXT)
		.field("password", Field.PASSWORD)
		.field(Forms.<Country>field("country", Field.DROP_DOWN_CHOICE)
			.choices(new EnumChoiceProvider<Country>(Country.class)))
		.field("birthDate", Field.DATE_PICKER)
		.nested(Forms.basic(Employer.class, "employers", MappingType.LIST)
			.field(Forms.field("name", Field.TEXT).readonly(true))
			.field("fromYear", Field.TEXT)
			.field("toYear", Field.TEXT)
			.build()
		)
		.field(Forms.<Skill>field("skills", Field.MULTIPLE_CHECK_BOX)
			.choices(skillsCodebook())
			.choiceRenderer(new ChoiceRenderer<Skill>() {
				@Override
				public ChoiceItem getItem(Skill item, int itemIndex) {
					return ChoiceItem.valueOf("" + item.getId(), item.getName());
				}
			})
		)	
		.field(Forms.<Function>field("functions", Field.MULTIPLE_CHOICE)
			.choices(functionsCodebook())
			.choiceRenderer(new ChoiceRenderer<Function>() {
				@Override
				public ChoiceItem getItem(Function item, int itemIndex) {
					return ChoiceItem.valueOf("" + item.getId(), item.getName());
				}
			})
		 )
		.field("certificate", Field.FILE_UPLOAD)
		.field(Forms.field("note", Field.TEXT_AREA).enabled(false))
		.nested(Forms.automatic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance"))
			.fields("street", "city", "zipCode").build())
		.field(Forms.field("registrationDate", Field.DATE).pattern("yyyy-MM-dd"))
		.field("email", Field.EMAIL)
		.field("phone", Field.TEL)
		.field("favoriteColor", Field.COLOR)
		.field("yearMonth", Field.MONTH)
		.field("yearWeek", Field.WEEK)
		.field("favoriteNumber", Field.NUMBER)
		.field("secondFavoriteNumber", Field.RANGE)
		.field("search", Field.SEARCH)
		.field("homepage", Field.URL)
		.field("agreement", Field.CHECK_BOX)
		.field("submitValue", Field.SUBMIT_BUTTON)
		.build();

	@Test
	public void testRenderForm() {
		final Locale locale = Locale.ENGLISH;
		Profile inputs = newVariousInputs();
		String pathSep = Forms.PATH_SEP;
		
		// Validation errors will be displayed when the form is shown for the first time
		// (due to fillAndValidate call)
		FormMapping<Profile> filledForm = profileForm.fillAndValidate(new FormData<Profile>(inputs, ValidationResult.empty), locale);
		assertNotNull(filledForm.getValidationResult().getFieldMessages().get("profile" + pathSep + "employers[0]" + pathSep + "fromYear"));
		
		Forms.previewForm(filledForm, locale);
	}
	
	private Profile newVariousInputs() {
		Profile profile = new Profile();
		profile.setAgreement(true);
		Calendar birthCal = Calendar.getInstance();
		birthCal.set(1980, 11, 6);
		profile.setBirthDate(birthCal.getTime());
		profile.setCertificate(null);
		profile.setCountry(Country.GB);
		profile.setFirstName("Marry " + getScriptInjectionAttempt());
		List<Function> functions = new ArrayList<Function>();
		functions.add(new Function(Long.valueOf(200), "Student"));
		functions.add(new Function(Long.valueOf(400), "Manager"));
		profile.setFunctions(functions);
		profile.setHeader("Research");
		profile.setNote("These are the most important moments of my life... " + getScriptInjectionAttempt());
		profile.setPassword("");
		profile.setProfileId("ab565");
		profile.setSalutation(Salutation.MS);
		Set<Skill> skills = new LinkedHashSet<Skill>();
		skills.add(new Skill(Long.valueOf(17), "CRM"));
		profile.setSkills(skills);
		
		List<Employer> employers = new ArrayList<Employer>();
		Employer e1 = new Employer();
		e1.setName("IBM");
		e1.setFromYear(19999); // invalid year
		e1.setToYear(1999);
		employers.add(e1);
		
		Employer e2 = new Employer();
		e2.setName("Microsoft");
		e2.setFromYear(1999);
		e2.setToYear(2014);
		employers.add(e2);
		
		profile.setEmployers(employers);
		
		profile.setRegistrationDate(new Date());
		profile.setSubmitValue("submitted");
		profile.setEmail("invalid-email.com");
		profile.setPhone("invalid-/%@phone123");
		profile.setFavoriteColor("#0000ff");
		profile.setFavoriteNumber(Integer.valueOf(4));
		profile.setSecondFavoriteNumber(Integer.valueOf(7));
		profile.setSearch("Something to search for...");
		profile.setHomepage("invalid-url");
		profile.setYearWeek("2014-W15");
		profile.setYearMonth("2014-04");
		return profile;
	}
	
	private static List<Skill> skillsCodebook() {
		List<Skill> skills = new ArrayList<Skill>();
		skills.add(new Skill(Long.valueOf(5), "Leadership"));
		skills.add(new Skill(Long.valueOf(2), "Management"));
		skills.add(new Skill(Long.valueOf(17), "CRM"));
		skills.add(new Skill(Long.valueOf(19), "Sales"));
		return skills;
	}
	
	private static List<Function> functionsCodebook() {
		List<Function> functions = new ArrayList<Function>();
		functions.add(new Function(Long.valueOf(200), "Student"));
		functions.add(new Function(Long.valueOf(300), "Sportsman"));
		functions.add(new Function(Long.valueOf(400), "Manager"));
		return functions;
	}
	
	private static String getScriptInjectionAttempt() {
		return "\" onFocus=\"window.alert('Alert attempt');\" style=\"";
	}

}
