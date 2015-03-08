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
package net.formio.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.formio.FormElement;

/**
 * Builder of TDI response.
 * @author Radek Beran
 */
public class TdiResponseBuilder {
	
	private final BasicFormRenderer renderer;
	private final List<String> instructions;
	
	protected TdiResponseBuilder(BasicFormRenderer renderer) {
		this.renderer = renderer;
		this.instructions = new ArrayList<String>();
	}
	
	/**
	 * Adds instruction to AJAX response: Status.
	 * The default value is OK. At this time, this is the only value recognised as success by TDI.
	 * Any other value is treated as an error and is displayed using alert() function. 
	 * @param status
	 * @return
	 */
	public TdiResponseBuilder status(String status) {
		instructions.add(getStatus(status));
		return this;
	}
	
	/**
	 * Adds instruction to AJAX response: Script.
	 * @param script inline Javascript code - it is invoked in the window scope
	 * @param src URL of the external Javascript
	 * @param id ID of the script - if there is another script on the page with the same name, 
	 * the script will not be downloaded more than once
	 * @return
	 */
	public TdiResponseBuilder script(String script, String src, String id) {
		instructions.add(getScript(script, src, id));
		return this;
	}
	
	/**
	 * Adds instruction to AJAX response: Script.
	 * @param script inline Javascript code - it is invoked in the window scope
	 * @param src URL of the external Javascript
	 * @return
	 */
	public TdiResponseBuilder script(String script, String src) {
		return script(script, src, null);
	}
	
	/**
	 * Adds instruction to AJAX response: Script.
	 * @param script inline Javascript code - it is invoked in the window scope
	 * @return
	 */
	public TdiResponseBuilder script(String script) {
		return script(script, null);
	}
	
	/**
	 * Adds instruction to AJAX response: Set focus to element with given name.
	 * @param elementName
	 * @return
	 */
	public TdiResponseBuilder scriptFocusForName(String elementName) {
		return script("$(\"#" + getIdForName(elementName) + "\").focus();");
	}
	
	/**
	 * Adds instruction to AJAX response: Set focus to element with given id.
	 * @param elementId
	 * @return
	 */
	public TdiResponseBuilder scriptFocusForId(String elementId) {
		return script("$(\"#" + elementId + "\").focus();");
	}
	
	/**
	 * Adds instruction to AJAX response: Update of form element.
	 * @param filledElement
	 * @return
	 */
	public <T> TdiResponseBuilder update(FormElement<T> element) {
		if (element == null) {
			throw new IllegalArgumentException("updated element cannot be null");
		}
		return update(element.getName(), renderElementMarkup(element));
	}
	
	/**
	 * Adds instruction to AJAX response: Update of form element.
	 * @param filledElement
	 * @return
	 */
	public TdiResponseBuilder update(String elementName, String elementMarkup) {
		String str = renderUpdateBeginTag(getRenderer().renderElementPlaceholderId(elementName)) +
			renderCDataBegin() +
			elementMarkup +
			renderCDataEnd() +
			renderUpdateEndTag();
		instructions.add(str);
		return this;
	}
	
	/**
	 * Adds instructions to AJAX response: Updates of form elements.
	 * @param elements
	 * @return
	 */
	public TdiResponseBuilder update(List<FormElement<?>> elements) {
		if (elements != null) {
			for (FormElement<?> el : elements) {
				update(el);
			}
		}
		return this;
	}
	
	/**
	 * Adds instructions to AJAX response: Updates of form elements.
	 * @param elements
	 * @return
	 */
	public TdiResponseBuilder update(FormElement<?>[] elements) {
		return update(elements == null ? new ArrayList<FormElement<?>>() : Arrays.asList(elements));
	}
	
	/**
	 * Adds redirect instruction to AJAX response. 
	 * @param url
	 * @return
	 */
	public TdiResponseBuilder redirect(String url) {
		instructions.add("<redirect href=\"" + url + "\"></redirect>" + newLine());
		return this;
	}

	/**
	 * Returns AJAX response in form of a string.
	 * @return
	 */
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(renderXmlDeclaration() +
			renderResponseBeginTag());
		boolean statusFound = false;
		for (String i : instructions) {
			if (i.contains("<" + getStatusTagName())) {
				statusFound = true;
				break;
			}
		}
		List<String> completeInstructions = new ArrayList<String>();
		if (!statusFound) {
			completeInstructions.add(getStatus("OK"));
		}
		completeInstructions.addAll(instructions);
		for (String i : completeInstructions) {
			sb.append(i);
		}
		sb.append(renderResponseEndTag());
		return sb.toString();
	}
	
	protected String renderXmlDeclaration() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + newLine(); 
	}
	
	protected String renderResponseBeginTag() {
		return "<response>" + newLine(); 
	}
	
	protected String renderResponseEndTag() {
		return "</response>" + newLine(); 
	}
	
	protected String renderUpdateBeginTag(String id) {
		return "<update target=\"" + id + "\" class-remove=\"hidden\">" + newLine();
	}
	
	protected String renderUpdateEndTag() {
		return "</update>" + newLine();
	}
	
	protected String renderCDataBegin() {
		return "<![CDATA[" + newLine();
	}
	
	protected String renderCDataEnd() {
		return "]]>" + newLine();
	}
	
	protected BasicFormRenderer getRenderer() {
		return renderer;
	}
	
	public String getElementId(FormElement<?> element) {
		return renderer.renderElementId(element);
	}
	
	public String getIdForName(String name) {
		return renderer.renderIdForName(name);
	}
	
	protected List<String> getInstructions() {
		return instructions;
	}
	
	protected <T> String renderElementMarkup(FormElement<T> element) {
		return getRenderer().renderElementMarkup(element);
	}
	
	private String newLine() {
		return System.getProperty("line.separator");
	}
	
	private String getStatus(String statusText) {
		return "<" + getStatusTagName() + ">" + statusText + "</" + getStatusTagName() + ">" + newLine();
	}
	
	private String getScript(String script, String src, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script");
		if (src != null && !src.isEmpty()) {
			sb.append(" src=\"" + src + "\"");
		}
		if (id != null && !id.isEmpty()) {
			sb.append(" id=\"" + id + "\"");
		}
		sb.append(">" + newLine());
		if (script != null && !script.isEmpty()) {
			sb.append(script + newLine());
		} 
		sb.append("</script>" + newLine());
		return sb.toString();
	}
	
	private String getStatusTagName() {
		return "status";
	}
}
