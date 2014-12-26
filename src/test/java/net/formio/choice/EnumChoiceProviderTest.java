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

import java.util.List;

import net.formio.domain.inputs.Country;

import org.junit.Test;

/**
 * Tests for {@link EnumChoiceProvider}.
 * @author Radek Beran
 */
public class EnumChoiceProviderTest {

	@Test
	public void testGetItems() {
		EnumChoiceProvider<Country> countryProvider = new EnumChoiceProvider<Country>(Country.class);
		List<? extends Country> countries = countryProvider.getItems();
		assertEquals(Country.values().length, countries.size());
		assertEquals(Country.values()[0], countries.get(0));
	}

}
