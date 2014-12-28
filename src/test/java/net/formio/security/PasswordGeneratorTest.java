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
package net.formio.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

/**
 * Tests for {@link PasswordGenerator}.
 * @author Radek Beran
 */
public class PasswordGeneratorTest {
	
	private static final Logger LOG = Logger.getLogger(PasswordGeneratorTest.class.getName());
	private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_@#$%^&*";
	
	@Test
	public void testGeneratePassword() {
		String pwd = PasswordGenerator.generatePassword();
		assertEquals(PasswordGenerator.DEFAULT_PWD_LENGTH, pwd.length());
	}
	
	@Test
	public void testGeneratePasswordWithLength() {
		assertEquals(4, PasswordGenerator.generatePassword(4).length());
		String pwd = PasswordGenerator.generatePassword(20, ALLOWED_CHARS);
		LOG.info(pwd);
		assertEquals(20, pwd.length());
	}
	
	@Test
	public void testGeneratePasswordWithLengthAndAllowedChars() {
		String allowedChars = "abcd";
		String pwd = PasswordGenerator.generatePassword(8, allowedChars);
		assertEquals(8, pwd.length());
		for (int i = 0; i < pwd.length(); i++) {
			assertTrue(allowedChars.indexOf(pwd.charAt(i)) >= 0);
		}
	}
}
