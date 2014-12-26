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

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.data.TestData;
import net.formio.data.TestForms;
import net.formio.domain.Car;
import net.formio.utils.TestUtils;
import net.formio.validation.ValidationResult;

/**
 * Renders HTML page with test form and displays it in default browser.
 * @author Radek Beran
 */
public class FormViewer {
	
	private static final Logger LOG = Logger.getLogger(FormViewer.class.getName());

	public static void main(String ... args) {
		FormData<Car> formData = new FormData<Car>(TestData.newCar(), ValidationResult.empty);
		FormMapping<Car> filledForm = TestForms.CAR_ACCESSIBILITY_FORM.fill(formData);
		String html = new BasicFormRenderer().renderHtmlPage(filledForm, FormMethod.POST, "/save", Locale.getDefault());
		File f = new File(TestUtils.getTempDir(), "test_form_view.html");
		LOG.info("Writing form HTML to " + f.getAbsolutePath());
		TestUtils.saveContentToTextFile(f, html, "UTF-8");
		TestUtils.openInBrowser("file:///" + f.getAbsolutePath().replace("\\", "/"));
	}
}
