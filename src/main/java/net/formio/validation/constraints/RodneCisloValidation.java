/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Validation of Czech "Rodne cislo".
 * 
 * @author Radek Beran
 */
public final class RodneCisloValidation {
	private static final Pattern RC_PATTERN = Pattern.compile("^(\\d\\d)(\\d\\d)(\\d\\d)[/]?(\\d\\d\\d)(\\d?)$");
	
	/**
	 * Returns true if given "rodne cislo" is valid according to complex rules
	 * (not only the format is validated). 
	 * <p>
	 * RC can but need not to contains slash.
	 * 
	 * @param rodneCislo
	 * @return result of validation, false if rodneCislo is {@code null}
	 */
	public static boolean isRodneCislo(String rodneCislo) {
		if (rodneCislo == null || rodneCislo.isEmpty()) return false;
		// Validni RC je napr. 780123/3540, 0531135099, 0681186066
		Matcher matcher = RC_PATTERN.matcher(rodneCislo);
		boolean valid = matcher.matches();
		if (valid) {
			// Input string answers to regular expression pattern
			matcher.reset();
			String yearStr = null, monthStr = null, dayStr = null, extStr = null, cStr = "";
			int year = 0, month = 0, c = 0;
			boolean cParsed = false;
			// Extracting parts within parenthesis
            while (matcher.find()) {
                yearStr = matcher.group(1);
                monthStr = matcher.group(2);
                dayStr = matcher.group(3);
                extStr = matcher.group(4);
                year = Integer.parseInt(yearStr);
                month = Integer.parseInt(monthStr);
                if (matcher.groupCount() > 4 && !matcher.group(5).trim().isEmpty()) {
                	cStr = matcher.group(5);
                	c = Integer.parseInt(cStr);
                	cParsed = true;
                }
            }
            // Do roku 1954 pridelovana devitimistna RC nelze overit.
            // Take RC, ktera v pripade pristehovanych cizincu konci na 9999
            // nelze validovat a zvaliduje se jen datum.
            if (!cParsed || (extStr + cStr).equals("9999")) {
                if (!cParsed) {
                	valid = year < 54;
                }
            } else {
	            // posledni kontrolni cislice
            	// vypocitame zbytek po deleni prvnich deviti cislic a cisla 11 
            	Integer firstNineDigits = Integer.valueOf(yearStr + monthStr + dayStr + extStr);
	            int mod = firstNineDigits.intValue() % 11;
	            // je-li zbytek 10, musi byt posledni cislice 0, jinak posledni cislice musi byt rovna zbytku  
	            if (mod == 10) {
	            	mod = 0;
	            }
	            valid = (c == mod);
            }
            
            if (valid) {
            	// kontrola data
            	year += year < 54 ? 2000 : 1900;
            	yearStr = year + "";
            	// k mesici muze byt pripocteno 20, 50 nebo 70
                if (month > 70 && year > 2003) {
                	month -= 70;
                } else if (month > 50) {
                	month -= 50;
                } else if (month > 20 && year > 2003) { 
                	month -= 20;
                }
                monthStr = month + "";
                if (monthStr.length() == 1) {
                	monthStr = "0" + monthStr;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setLenient(false); // strict parsing
                try {
                	format.parse(yearStr + "-" + monthStr + "-" + dayStr);
                } catch (ParseException ex) {
                	valid = false;
                }
            }
		}
		return valid;
	}
	
	private RodneCisloValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
