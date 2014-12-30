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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import net.formio.FormComponent;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.choice.ChoiceRenderer;
import net.formio.choice.DefaultChoiceProvider;
import net.formio.choice.EnumChoiceProvider;
import net.formio.domain.inputs.Country;
import net.formio.domain.inputs.Function;
import net.formio.domain.inputs.Salutation;
import net.formio.domain.inputs.Skill;
import net.formio.domain.inputs.VariousInputs;
import net.formio.utils.TestUtils;
import net.formio.validation.ValidationResult;

import org.junit.Test;

/**
 * Tests for {@link BasicFormRenderer}.
 * @author Radek Beran
 */
public class BasicFormRendererTest {
	private static final Logger LOG = Logger.getLogger(BasicFormRendererTest.class.getName());
	
	private static final FormMapping<VariousInputs> profileForm = Forms.basic(VariousInputs.class, "profile")
		.field("profileId", FormComponent.HIDDEN_FIELD.getType())
		.field("header", FormComponent.LABEL.getType())
		.field(Forms.<Salutation>field("salutation", FormComponent.RADIO_CHOICE.getType())
			.choiceProvider(new EnumChoiceProvider<Salutation>(Salutation.class)))
		.field("firstName", FormComponent.TEXT_FIELD.getType())
		.field("password", FormComponent.PASSWORD.getType())
		.field(Forms.<Country>field("country", FormComponent.DROP_DOWN_CHOICE.getType())
			.choiceProvider(new EnumChoiceProvider<Country>(Country.class)))
		.field("birthDate", FormComponent.DATE_PICKER.getType())
		.field(Forms.<Skill>field("skills", FormComponent.MULTIPLE_CHECK_BOX.getType())
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
		.field(Forms.<Function>field("functions", FormComponent.MULTIPLE_CHOICE.getType())
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
		.field("certificate", FormComponent.FILE_UPLOAD.getType())
		.field("note", FormComponent.TEXT_AREA.getType())
		.field("agreement", FormComponent.CHECK_BOX.getType())
		.build();

	@Test
	public void testRenderForm() {
		VariousInputs inputs = newVariousInputs();
		FormData<VariousInputs> formData = new FormData<VariousInputs>(inputs, ValidationResult.empty);
		FormMapping<VariousInputs> filledForm = profileForm.fill(formData);
		String html = new BasicFormRenderer().renderHtmlPage(filledForm, FormMethod.POST, "#", Locale.ENGLISH);
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
	
	private VariousInputs newVariousInputs() {
		VariousInputs inputs = new VariousInputs();
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
