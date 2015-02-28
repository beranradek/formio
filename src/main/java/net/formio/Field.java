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
package net.formio;

/**
 * Type of form field.
 * @author Radek Beran
 */
public enum Field {
	TEXT("text", "text"),
	TEXT_AREA("textarea", ""),
	PASSWORD("password", "password"),
	HIDDEN("hidden", "hidden"),
	CHECK_BOX("checkbox", "checkbox"),
	MULTIPLE_CHECK_BOX("checkbox-multiple", "checkbox"),
	RADIO_CHOICE("radio", "radio"),
	DROP_DOWN_CHOICE("select", ""),
	MULTIPLE_CHOICE("select-multiple", ""),
	DATE_PICKER("date-picker", "text"),
	FILE_UPLOAD("file", "file"),
	SUBMIT_BUTTON("submit", "submit"),
	
	// HTML 5:
	COLOR("color", "color"),
	DATE("date", "date"),
	DATE_TIME("datetime", "datetime"),
	DATE_TIME_LOCAL("datetime-local", "datetime-local"),
	TIME("time", "time"),
	EMAIL("email", "email"),
	MONTH("month", "month"),
	NUMBER("number", "number"),
	RANGE("range", "range"),
	SEARCH("search", "search"),
	TEL("tel", "tel"),
	URL("url", "url"),
	WEEK("week", "week");
	
	// LABEL("label"),
	// LINK("link"),
	// TODO: Multiple date and file?
	
	private final String type;
	private final String inputType;
	
	private Field(String type, String inputType) {
		this.type = type;
		this.inputType = inputType;
	}

	public String getType() {
		return type;
	}
	
	public String getInputType() {
		return inputType;
	}

	public boolean isChoice() {
		return this == MULTIPLE_CHECK_BOX || this == RADIO_CHOICE || this == DROP_DOWN_CHOICE || this == MULTIPLE_CHOICE;
	}
	
	public boolean isWithMultipleInputs() {
		return this == MULTIPLE_CHECK_BOX || this == RADIO_CHOICE;
	}
	
	public static Field findByType(String type) {
		for (Field fc : Field.values()) {
			if (fc.getType().equals(type)) {
				return fc;
			}
		}
		return null;
	}
}
