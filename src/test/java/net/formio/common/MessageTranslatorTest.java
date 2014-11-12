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
package net.formio.common;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests for {@link MessageTranslator}.
 * @author Radek Beran
 */
public class MessageTranslatorTest {

	private static final String WEEK_DAYS_BUNDLE = "net/formio/common/WeekDays";

	@Test
	public void testGetMessage() {
		MessageTranslator de = new MessageTranslator(WEEK_DAYS_BUNDLE, Locale.GERMAN);
		assertEquals("Dienstag", de.getMessage("tu"));
		
		// default language
		MessageTranslator def = new MessageTranslator(WEEK_DAYS_BUNDLE);
		assertEquals("Tuesday", def.getMessage("tu"));
	}
	
	@Test
	public void testGetMessageForLocale() {
		MessageTranslator en = new MessageTranslator(WEEK_DAYS_BUNDLE);
		assertEquals("Dienstag", en.getMessage("tu", Locale.GERMAN));
	}
	
	@Test
	public void testGetMessageForMissingKey() {
		MessageTranslator def = new MessageTranslator(WEEK_DAYS_BUNDLE);
		// unknown message key - ???<key>??? is returned back as a translation
		assertEquals("Message translation found for invalid key rt!", "???rt???", def.getMessage("rt"));
	}
	
	@Test
	public void testGetMessageFromInvalidBundle() {
		MessageTranslator de = new MessageTranslator("some/Weeks", Locale.GERMAN);
		assertEquals("???tu???", de.getMessage("tu"));
	}
	
	@Test
	public void testFallbackToDefaultBundle() {
		// Italian is unsupported language in this test
		MessageTranslator it = new MessageTranslator(WEEK_DAYS_BUNDLE, Locale.ITALIAN);
		assertEquals("Tuesday", it.getMessage("tu"));
	}
	
	@Test
	public void testGetMessageWithArgs() {
		MessageTranslator tr = new MessageTranslator(WEEK_DAYS_BUNDLE);
		assertEquals("First week of year " + 2014 + ".", tr.getMessage("week", "First", Integer.valueOf(2014)));
	}

	@Test
	public void testGetMessageForClass() {
		assertEquals("Freitag", WeekDays.FRIDAY.getDayLocalized(Locale.GERMANY));
		assertEquals("Friday", WeekDays.FRIDAY.getDayLocalized(Locale.ENGLISH));
		assertEquals("Friday", WeekDays.FRIDAY.getDayLocalized(Locale.ITALIAN)); // fallback to default message bundle
		MessageTranslator tr = new MessageTranslator(WeekDays.class);
		assertEquals("Freitag", tr.getMessage("fr", Locale.GERMANY));
	}
}

