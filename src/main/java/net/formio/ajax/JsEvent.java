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
package net.formio.ajax;

/**
 * JavaScript event.
 * @author Radek Beran
 */
public enum JsEvent {
	/** When a user leaves an input field. */
	BLUR("blur"),
	/** When a user changes the content of an input field or selects a dropdown value. */
	CHANGE("change"),
	/** When an input field gets focus. */
	FOCUS("focus"),
	/** When input text is selected. */
	SELECT("select"),
	/** When a user clicks the submit button. */
	SUBMIT("submit"),
	/** When a user clicks the reset button. */
	RESET("reset"),
	/** When a user is pressing/holding down a key. */
	KEYDOWN("keydown"),
	/** When a user pressed a key. */
	KEYPRESS("keypress"),
	/** When the user releases a key. */
	KEYUP("keyup"),
	MOUSEENTER("mouseenter"),
	MOUSELEAVE("mouseleave"),
	CLICK("click");
	
	private final String eventName;
	
	private JsEvent(String eventName) {
		this.eventName = eventName;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public String getAttributeName() {
		return "on" + eventName;
	}
}
