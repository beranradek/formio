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
package net.formio.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.formio.format.Location;
import net.formio.format.Formatter;
import net.formio.format.StringParseException;

/**
 * Formatters for tests.
 * @author Radek Beran
 */
public final class TestFormatters {
	public static final Formatter<Date> CUSTOM_DATE_FORMATTER = new Formatter<Date>() {
		private static final String FIXED_FORMAT = "d-M-yyyy HH-mm";
		
		@Override
		public Date parseFromString(String str, Class<Date> destClass, String formatPattern, Location loc) {
			try {
				return new SimpleDateFormat(FIXED_FORMAT).parse(str);
			} catch (Exception ex) {
				throw new StringParseException(Date.class, str, ex);
			}
		}
		
		@Override
		public String makeString(Date value, String formatPattern, Location loc) {
			return new SimpleDateFormat(FIXED_FORMAT).format(value);
		}
	};
	
	private TestFormatters() {
	}
}
