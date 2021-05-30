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
package net.formio.binding;

import net.formio.format.Location;
import net.formio.format.Formatter;

/**
 * Information about value that should be bound to an object instance.
 * @author Radek Beran
 */
public final class BoundValuesInfo {
	private final Object[] values;
	private final String pattern;
	private final Formatter<Object> formatter;
	private final Location location;

	public static BoundValuesInfo getInstance(Object[] values, String pattern, Formatter<?> formatter, Location loc) {
		return new BoundValuesInfo(values, pattern, formatter, loc);
	}
	
	public static BoundValuesInfo getInstance(Object[] values, String pattern) {
		return getInstance(values, pattern, null, Location.DEFAULT);
	}
	
	public static BoundValuesInfo getInstance(Object[] values) {
		return getInstance(values, null);
	}
	
	private BoundValuesInfo(Object[] values, String pattern, Formatter<?> formatter, Location loc) {
		this.values = values;
		this.pattern = pattern;
		this.formatter = (Formatter<Object>)formatter;
		this.location = loc;
	}

	public Object[] getValues() {
		return values != null ? values.clone() : null;
	}
	
	public Object getValue() {
		Object value = null;
		if (values != null && values.length > 0) {
			value = values[0];
		}
		return value;
	}

	/**
	 * Pattern for parsing a String value to target value. 
	 * @return
	 */
	public String getPattern() {
		return pattern;
	}
	
	/**
	 * Formatter that formats object to String and vice versa.
	 * @return
	 */
	public Formatter<Object> getFormatter() {
		return formatter;
	}

	public Location getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (values != null) {
			for (Object v : values) {
				sb.append(v).append("; ");
			}
		}
		return sb.toString();
	}

}
