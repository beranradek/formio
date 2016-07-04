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
 * Builder of toString method of {@link FormField}s.
 * @author Radek Beran
 */
class FormFieldStringBuilder {
	String build(FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(field.getName());
		boolean firstParam = true;
		sb.append(" (");
		if (field.getPattern() != null && !field.getPattern().isEmpty()) {
			if (!firstParam) sb.append(", ");
			sb.append("pattern=").append(field.getPattern());
			firstParam = false;
		}
		if (field.getValue() != null && !field.getValue().isEmpty()) {
			if (!firstParam) sb.append(", ");
			int cnt = 0;
			if (field.getFilledObjects() != null) {
				cnt = field.getFilledObjects().size();
			}
			sb.append("value=").append(field.getValue().length() > 17 ? field.getValue().substring(0, 17) + "..." : field.getValue()).append(" /count: ").append(cnt).append("/");
			firstParam = false;
		}
		sb.append(", order=").append(field.getOrder());
		sb.append(")");
		return sb.toString();
	}
}
