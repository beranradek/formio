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
package net.formio.props.types;

/**
 * Type of form button.
 * @author Radek Beran
 */
public enum ButtonType {
	/**
	 * Submits the form.
	 */
	SUBMIT("submit"),
	
	/**
	 * Resets filled form values.
	 */
	RESET("reset"),
	
	/** 
	 * Button with action defined using the onclick attribute; or without an action.
	 */
	BUTTON("button");
	
	private final String typeName;
	
	private ButtonType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public static ButtonType fromTypeName(String typeName) {
		ButtonType bt = null;
		if (typeName != null) {
			String typeNameLc = typeName.toLowerCase(); 
			for (ButtonType b : values()) {
				if (b.getTypeName().toLowerCase().equals(typeNameLc)) {
					bt = b;
					break;
				}
			}
		}
		return bt;
	}
}
