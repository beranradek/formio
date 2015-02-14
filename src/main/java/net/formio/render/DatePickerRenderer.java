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

import net.formio.FormField;


/**
 * Renders date picker components.
 * @author Radek Beran
 */
class DatePickerRenderer {
	private final RenderContext ctx;

	DatePickerRenderer(RenderContext ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
		this.ctx = ctx;
	}
	
	protected <T> String renderDatePickerJavaScript(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script>" + newLine());
		sb.append("$(function(){" + newLine());
		sb.append("	$('#id-" + field.getName() + "').datepicker({ dateFormat: \""
			+ getDatePickerPattern(field) + "\" });" + newLine());
		sb.append("});" + newLine());
		sb.append("</script>" + newLine());
		return sb.toString();
	}
	
	protected RenderContext getRenderContext() {
		return ctx;
	}
	
	private <T> String getDatePickerPattern(@SuppressWarnings("unused") FormField<T> field) {
		return "d.m.yy";
	}
	
	private String newLine() {
		return getRenderContext().newLine();
	}
}
