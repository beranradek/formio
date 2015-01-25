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

import java.io.Serializable;

import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;
import net.formio.props.FieldProperty;

/**
 * Default implementation of {@link FormProperties}. Immutable.
 * @author Radek Beran
 */
public class FormPropertiesImpl implements FormProperties, Serializable {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private static final long serialVersionUID = 8353865315646591562L;
	
	private final HeterogMap<String> properties;
	
	FormPropertiesImpl(final HeterogMap<String> properties) {
		if (properties == null) throw new IllegalArgumentException("formProperties cannot be null, only empty");
		this.properties = HeterogCollections.unmodifiableMap(properties);
	}
	
	FormPropertiesImpl(final FormProperties src) {
		this(src, (FieldProperty<?>)null, null);
	}
	
	<T> FormPropertiesImpl(final FormProperties src, FieldProperty<T> property, T value) {
		this(copyProperties(src.getProperties(), property, value));
	}
	
	@Override
	public boolean isVisible() {
		return this.properties.getTyped(FieldProperty.VISIBLE).booleanValue();
	}
	
	@Override
	public boolean isEnabled() {
		return this.properties.getTyped(FieldProperty.ENABLED).booleanValue();
	}
	
	@Override
	public boolean isReadonly() {
		return this.properties.getTyped(FieldProperty.READ_ONLY).booleanValue();
	}
	
	@Override
	public boolean isRequired() {
		return this.properties.getTyped(FieldProperty.REQUIRED).booleanValue();
	}
	
	@Override
	public String getHelp() {
		return this.properties.getTyped(FieldProperty.HELP);
	}
	
	@Override
	public String getDataAjaxUrl() {
		return this.properties.getTyped(FieldProperty.DATA_AJAX_URL);
	}
	
	@Override
	public HeterogMap<String> getProperties() {
		return this.properties;
	}
	
	@Override
	public String toString() {
		return "FormPropertiesImpl [formProperties=" + properties + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormPropertiesImpl))
			return false;
		FormPropertiesImpl other = (FormPropertiesImpl) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	private static <T> HeterogMap<String> copyProperties(HeterogMap<String> source, FieldProperty<T> property, T value) {
		final HeterogMap<String> map = HeterogCollections.<String>newLinkedMap();
		map.putAllFromSource(source);
		if (property != null) {
			// value is specified
			map.putTyped(property, value);
		}
		return map;
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}
	
	/**
	 * Returns new formProperties with added/replaced property.
	 * @param property
	 * @param value
	 * @return
	 */
	<T> FormProperties withProperty(FieldProperty<T> property, T value) {
		return new FormPropertiesImpl(copyProperties(this.getProperties(), property, value));
	}
}
