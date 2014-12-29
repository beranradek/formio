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
 * Type of form component.
 * @author Radek Beran
 */
public enum FormComponent {
	LABEL("label"),
	TEXT_FIELD("text"),
	TEXT_AREA("textarea"),
	PASSWORD("password"),
	HIDDEN_FIELD("hidden"),
	CHECK_BOX("checkbox"),
	MULTIPLE_CHECK_BOX("checkbox_multiple"),
	RADIO_CHOICE("radio"),
	DROP_DOWN_CHOICE("select"),
	MULTIPLE_CHOICE("select_multiple"),
	BUTTON("submit"),
	LINK("link"),
	DATE_PICKER("date"),
	FILE_UPLOAD("file");
	
	private final String type;
	
	private FormComponent(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static FormComponent findByType(String typeName) {
		for (FormComponent fc : FormComponent.values()) {
			if (fc.getType().equals(typeName)) {
				return fc;
			}
		}
		return null;
	}
}
