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
package net.formio.debug;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import net.formio.FormData;
import net.formio.FormFieldType;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.MappingType;
import net.formio.choice.ChoiceRenderer;
import net.formio.choice.DefaultChoiceProvider;
import net.formio.choice.EnumChoiceProvider;
import net.formio.domain.inputs.Country;
import net.formio.domain.inputs.Employer;
import net.formio.domain.inputs.Function;
import net.formio.domain.inputs.Profile;
import net.formio.domain.inputs.Salutation;
import net.formio.domain.inputs.Skill;
import net.formio.utils.TestUtils;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link BasicFormRenderer}.
 * @author Radek Beran
 */
public class BasicFormRendererTest {
	private static final Logger LOG = Logger.getLogger(BasicFormRendererTest.class.getName());
	
	private static final FormMapping<Profile> profileForm = Forms.basic(Profile.class, "profile")
		.field("profileId", FormFieldType.HIDDEN_FIELD.getType())
		.field("header", FormFieldType.LABEL.getType())
		.field(Forms.<Salutation>field("salutation", FormFieldType.RADIO_CHOICE.getType())
			.choiceProvider(new EnumChoiceProvider<Salutation>(Salutation.class)))
		.field("firstName", FormFieldType.TEXT_FIELD.getType())
		.field("password", FormFieldType.PASSWORD.getType())
		.field(Forms.<Country>field("country", FormFieldType.DROP_DOWN_CHOICE.getType())
			.choiceProvider(new EnumChoiceProvider<Country>(Country.class)))
		.field("birthDate", FormFieldType.DATE_PICKER.getType())
		.nested(Forms.basic(Employer.class, "employers", MappingType.LIST)
			.field(Forms.field("name", FormFieldType.TEXT_FIELD.getType()).readonly(true))
			.field("fromYear", FormFieldType.TEXT_FIELD.getType())
			.field("toYear", FormFieldType.TEXT_FIELD.getType())
			.build()
		)
		.field(Forms.<Skill>field("skills", FormFieldType.MULTIPLE_CHECK_BOX.getType())
			.choiceProvider(new DefaultChoiceProvider<Skill>(skillsCodebook()))
			.choiceRenderer(new ChoiceRenderer<Skill>() {
				
				@Override
				public String getTitle(Skill item, int itemIndex) {
					return item.getName();
				}
				
				@Override
				public String getId(Skill item, int itemIndex) {
					return "" + item.getId();
				}
			})
		)	
		.field(Forms.<Function>field("functions", FormFieldType.MULTIPLE_CHOICE.getType())
			.choiceProvider(new DefaultChoiceProvider<Function>(functionsCodebook()))
			.choiceRenderer(new ChoiceRenderer<Function>() {
				
				@Override
				public String getTitle(Function item, int itemIndex) {
					return item.getName();
				}
				
				@Override
				public String getId(Function item, int itemIndex) {
					return "" + item.getId();
				}
			})
		 )
		.field("certificate", FormFieldType.FILE_UPLOAD.getType())
		.field(Forms.field("note", FormFieldType.TEXT_AREA.getType()).enabled(false))
		.field("agreement", FormFieldType.CHECK_BOX.getType())
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
		
		RenderContext<Profile> ctx = new RenderContext<Profile>();
		ctx.setFilledForm(filledForm);
		ctx.setMethod(FormMethod.POST);
		ctx.setActionUrl("#");
		ctx.setLocale(locale);
		String html = new BasicFormRenderer().renderHtmlPage(ctx);
		File f = null;
		try {
			f = File.createTempFile("test_inputs_", ".html", TestUtils.getTempDir());
			// f.deleteOnExit();
			LOG.info("Writing form HTML to " + f.getAbsolutePath());
			TestUtils.saveContentToTextFile(f, html, "UTF-8");
			TestUtils.openInBrowser("file:///" + f.getAbsolutePath().replace("\\", "/"));
		} catch (IOException ex) {
			fail("IO error: " + ex.getMessage());
		}
	}
	
	private Profile newVariousInputs() {
		Profile inputs = new Profile();
		inputs.setAgreement(true);
		Calendar birthCal = Calendar.getInstance();
		birthCal.set(1980, 11, 6);
		inputs.setBirthDate(birthCal.getTime());
		inputs.setCertificate(null);
		inputs.setCountry(Country.GB);
		inputs.setFirstName("Marry");
		List<Function> functions = new ArrayList<Function>();
		functions.add(new Function(Long.valueOf(300), "Sportsman"));
		inputs.setFunctions(functions);
		inputs.setHeader("Research");
		inputs.setNote("These are the most important moments of my life...");
		inputs.setPassword("secret123");
		inputs.setProfileId("ab565");
		inputs.setSalutation(Salutation.MS);
		Set<Skill> skills = new LinkedHashSet<Skill>();
		skills.add(new Skill(Long.valueOf(17), "CRM"));
		inputs.setSkills(skills);
		
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
		
		inputs.setEmployers(employers);
		return inputs;
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

}
