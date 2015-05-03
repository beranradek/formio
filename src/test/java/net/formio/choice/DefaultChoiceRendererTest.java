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
package net.formio.choice;

import static org.junit.Assert.*;

import java.util.Locale;

import net.formio.domain.inputs.Country;
import net.formio.domain.inputs.Salutation;

import org.junit.Test;

/**
 * Tests for {@link DefaultChoiceRenderer}.
 * @author Radek Beran
 */
public class DefaultChoiceRendererTest {
	
	private static final Locale LOCALE = Locale.ENGLISH;

	@Test
	public void testGetTitle() {
		DefaultChoiceRenderer<Country> countryRenderer = new DefaultChoiceRenderer<Country>(LOCALE);
		assertEquals(Country.GB.getTitle(), countryRenderer.getItem(Country.GB, 0).getTitle());
		DefaultChoiceRenderer<Salutation> salutationRenderer = new DefaultChoiceRenderer<Salutation>(LOCALE);
		assertEquals("Mr.", salutationRenderer.getItem(Salutation.MR, 0).getTitle());
		assertEquals("???", salutationRenderer.getItem(null, 0).getTitle());
	}
	
	@Test
	public void testGetId() {
		DefaultChoiceRenderer<Country> countryRenderer = new DefaultChoiceRenderer<Country>(LOCALE);
		assertEquals(Country.GB.name(), countryRenderer.getItem(Country.GB, 0).getId());
		DefaultChoiceRenderer<Salutation> salutationRenderer = new DefaultChoiceRenderer<Salutation>(LOCALE);
		assertEquals(Salutation.MR.name(), salutationRenderer.getItem(Salutation.MR, 0).getId());
		assertEquals("0", salutationRenderer.getItem(null, 0).getId());
	}

}
