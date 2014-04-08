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
package net.formio.format;

public class StringParseException extends RuntimeException {
	private static final long serialVersionUID = 1407704812024515683L;
	private final Class<?> targetTypeClass;
	private final String parsedString;

	public StringParseException(Class<?> targetTypeClass,
			String parsedString, Throwable cause) {
		super("Error while parsing " + targetTypeClass.getName()
				+ " from String '" + parsedString + "'", cause);
		this.targetTypeClass = targetTypeClass;
		this.parsedString = parsedString;
	}

	public Class<?> getTargetTypeClass() {
		return targetTypeClass;
	}

	public String getParsedString() {
		return parsedString;
	}

}
