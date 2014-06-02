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
package net.formio.validation.constraints;

import static org.junit.Assert.*;

import org.junit.Test;

public class UrlValidationTest {

	@Test
	public void testIsUrl() {
		assertFalse("should not be valid URL", UrlValidation.isUrl(null));
		assertFalse("should not be valid URL", UrlValidation.isUrl(""));
		assertFalse("should not be valid URL", UrlValidation.isUrl("hello"));
		assertFalse("should not be valid URL", UrlValidation.isUrl("/some/folders"));
		assertFalse("should not be valid URL", UrlValidation.isUrl("//some/folders"));
		
		assertTrue("should be valid URL", UrlValidation.isUrl("http://typesafe.com/"));
		assertTrue("should be valid URL", UrlValidation.isUrl("https://formulare.mpsv.cz/oksluzby/cs/form/edit.jsp?CMD=EditForm&FN=OZPPO140101573&SSID=AGAliACvcboTZC6~B7tDt5cc3IFKjCOq"));
		assertTrue("should be valid URL", UrlValidation.isUrl("ftp://ftp.fetchsoftworks.com/Fetch_5.dmg"));
	}

}
