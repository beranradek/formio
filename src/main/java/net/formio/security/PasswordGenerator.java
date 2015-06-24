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

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * Generator of random passwords/strings 
 * from potentially provided list of allowed characters.
 *  
 * @author Radek Beran
 */
public final class PasswordGenerator {
	
	public static final int DEFAULT_PWD_LENGTH = 8;
	public static final String DEFAULT_PWD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

	public static String generatePassword() {
		return generatePassword(DEFAULT_PWD_LENGTH, DEFAULT_PWD_CHARS); 
	}
	
	public static String generatePassword(int length) {
		return generatePassword(length, DEFAULT_PWD_CHARS); 
	}

	public static String generatePassword(int length, String digits) {
		return generatePassword(length, digits, 0, "");
	}

	/**
	 * Creates secure password by generating random sequence of characters. Which characters will appear in 
	 * password is controlled by method parameter.
	 * <p>
	 * Note <b>never</b> use this method to generate more that one password in sequence. Sequential
	 * initialization of random generator dos not guarantee enough "randomness". Use {@link #generatePassword(int, String, int, String)}
	 * method instead.
	 * 
	 * @param length length of password to be generated
	 * @param digits alphabet of letters allows within password
	 * @param grpSize if password character groups should be delimited, size of character group. Zero otherwise 
	 * @param grpSep password character separator
	 * @return random sequence of character from provided alphabet with given lenght and 
	 *         optionaly separated by provided separator at every <code>grpSize</code> position
	 * @throws IllegalArgumentException if any character of separator is contained in alphabet
	 */
	public static String generatePassword(int length, String digits, int grpSize, String grpSep) {
		String groupSeparator = grpSep;
		groupSeparator = testSeparator(digits, groupSeparator);
		SecureRandom rnd = new SecureRandom();
		return generateSinglePswd(length, digits, grpSize, groupSeparator, rnd);
	}

	protected static String generateSinglePswd(int length, String digits, int grpSize, String grpSep, SecureRandom rnd) {
		int l = digits.length();
		int bits = (int) Math.ceil(Math.log(Math.pow(l,length))/Math.log(2));
		StringBuffer buf = new StringBuffer();
		BigInteger bl = BigInteger.valueOf(l);
		BigInteger bi = new BigInteger(bits,rnd);
		for (int j=0;j<length;j++) {
			if (j!=0 && grpSize>0 && j%grpSize==0) buf.append(grpSep);
			int d = bi.mod(bl).intValue();
			bi = bi.divide(bl);
			buf.append(digits.charAt(d));
		}
		return buf.toString();
	}

	private static String testSeparator(String digits, String grpSep) {
		String groupSeparator = grpSep;
		if (groupSeparator==null) {
			groupSeparator = "";
		}
		for (int i=0;i<groupSeparator.length();i++) { 
			if (digits.indexOf(groupSeparator.charAt(i))>=0) { 
				throw new IllegalArgumentException("Separator contains digits");
			}
		}
		return groupSeparator;
	}
	
	private PasswordGenerator() {
	}
}
