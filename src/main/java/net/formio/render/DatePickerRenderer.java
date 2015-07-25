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
	private final FormRenderer renderer;

	DatePickerRenderer(FormRenderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		this.renderer = renderer;
	}
	
	protected <T> String renderDatePickerScript(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script>" + renderer.newLine());
		sb.append("$(function(){" + renderer.newLine());
		sb.append("	$('#id-" + field.getName() + "').datepicker({ dateFormat: \""
			+ getDatePickerPattern(field) + "\" });" + renderer.newLine());
		sb.append("});" + renderer.newLine());
		sb.append("</script>" + renderer.newLine());
		return sb.toString();
	}
	
	private <T> String getDatePickerPattern(@SuppressWarnings("unused") FormField<T> field) {
		return "d.m.yy";
	}
}
