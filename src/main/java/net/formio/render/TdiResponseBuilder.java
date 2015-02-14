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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
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
	 * Adds instruction to AJAX response: Update of form element.
	 * @param filledElement
	 * @return
	 */
	public TdiResponseBuilder update(FormElement element) {
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
			renderResponseBeginTag() +
			renderOkStatus());
		for (String i : instructions) {
			sb.append(i);
		}
		sb.append(renderResponseEndTag());
		return sb.toString();
	}
	
	/**
	 * Writes AJAX response to {@link HttpServletResponse}. Response is closed for further writing.
	 * @param response
	 */
	public void writeToResponse(final HttpServletResponse response) {
		response.setContentType(ContentTypes.XML);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(asString());
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * Convenience method that updates given form elements using TDI AJAX response.
	 * @param response
	 * @param elements
	 */
	public void update(HttpServletResponse response, FormElement ... elements) {
		if (elements != null) {
			for (FormElement el : elements) {
				update(el);
			}
		}
		writeToResponse(response);
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
	
	protected String renderOkStatus() {
		return "<status>OK</status>" + newLine();
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
	
	protected List<String> getInstructions() {
		return instructions;
	}
	
	protected String renderElementMarkup(FormElement element) {
		return getRenderer().renderElementMarkup(element);
	}
	
	private String newLine() {
		return System.getProperty("line.separator");
	}
}
