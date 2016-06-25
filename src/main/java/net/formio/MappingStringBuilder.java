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

import java.util.List;
import java.util.Map;

/**
 * Builder of toString method of {@link FormMapping}s.
 * @author Radek Beran
 * @param <T>
 */
class MappingStringBuilder<T> {
	private final Class<T> dataClass;
	private final String path;
	private final int order;
	private final Map<String, FormField<?>> fields;
	private final Map<String, FormMapping<?>> nested;
	private final List<FormMapping<T>> listMappings;
	
	MappingStringBuilder(
		Class<T> dataClass,
		String path,
		int order,
		Map<String, FormField<?>> fields,
		Map<String, FormMapping<?>> nested,
		List<FormMapping<T>> listMappings) {
		this.dataClass = dataClass;
		this.path = path;
		this.order = order;
		this.fields = fields;
		this.nested = nested;
		this.listMappings = listMappings;
	}
	
	String build(String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent).append(path).append(" : ").append(dataClass.getSimpleName()).append(" {");
		boolean firstBlock = true;
		sb.append("\n").append(indent).append("  order : ").append(order).append(",");
		if (fields != null && !fields.isEmpty()) {
			sb.append("\n").append(indent).append("  fields {");
			boolean first = true;
			for (FormField<?> f : fields.values()) {
				if (!first) {
					sb.append(",");
				} else first = false;
				sb.append("\n    ").append(indent).append(f);
			}
			sb.append("\n").append(indent).append("  }");
			firstBlock = false;
		}
		if (nested != null && !nested.isEmpty()) {
			if (!firstBlock) sb.append(", ");
			sb.append("\n").append(indent).append("  nested {");
			boolean first = true;
			for (FormMapping<?> nm : nested.values()) {
				if (!first) {
					sb.append(",");
				} else first = false;
				sb.append("\n").append(nm.toString(indent + "    "));
			}
			sb.append("\n").append(indent).append("  }");
			firstBlock = false;
		}
		if (listMappings != null && !listMappings.isEmpty()) {
			if (!firstBlock) {  // NOPMD by Radek on 2.3.14 18:33
				sb.append(", ");
			}
			sb.append("\n").append(indent).append("  list {");
			boolean first = true;
			for (FormMapping<?> m : listMappings) {
				if (!first) {
					sb.append(",");
				} else {
					first = false;
				}
				sb.append("\n").append(m.toString(indent + "    "));
			}
			sb.append("\n").append(indent).append("  }");
			firstBlock = false;
		}
		sb.append("\n").append(indent).append("}");
		return sb.toString();
	}
}
