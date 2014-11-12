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
package net.formio.props;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.formio.common.heterog.AbstractTypedKey;
import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;

/**
 * Common field formProperties.
 * @author Radek Beran
 */
public class FieldProperty<T> extends AbstractTypedKey<String, T> implements Property<T> {
	private static final long serialVersionUID = 4271239940342562765L;
	protected static List<FieldProperty<Object>> props;
	
	static {
		props = new ArrayList<FieldProperty<Object>>();
	}
	
	public static final FieldProperty<Boolean> VISIBLE = register(new FieldProperty<Boolean>("visible", Boolean.class, Boolean.TRUE));
	public static final FieldProperty<Boolean> ENABLED = register(new FieldProperty<Boolean>("enabled", Boolean.class, Boolean.TRUE));
	public static final FieldProperty<Boolean> READ_ONLY = register(new FieldProperty<Boolean>("readonly", Boolean.class, Boolean.FALSE));
	public static final FieldProperty<Boolean> REQUIRED = register(new FieldProperty<Boolean>("required", Boolean.class, Boolean.FALSE));
	public static final FieldProperty<String> HELP = register(new FieldProperty<String>("help", String.class, ""));
	
	protected static <T> FieldProperty<T> register(FieldProperty<T> prop) {
		if (props.contains(prop)) {
			throw new IllegalArgumentException("Property with name '" + prop.getName() + "' is already registered.");
		}
		props.add((FieldProperty<Object>)prop);
		return prop;
	}
	
	public static List<FieldProperty<Object>> getValues() {
		return Collections.unmodifiableList(props);
	}
	
	public static <T> FieldProperty<T> fromName(String propName) {
		FieldProperty<T> prop = null;
		if (propName != null) {
			for (FieldProperty<Object> p : props) {
				if (p.getName().equals(propName)) {
					prop = (FieldProperty<T>)p;
					break;
				}
			}
		}
		return prop;
	}
	
	/**
	 * Returns new heterogeneous map with default formProperties for form field.
	 * @return
	 */
	public static HeterogMap<String> createDefaultFieldProperties() {
		HeterogMap<String> propMap = HeterogCollections.<String>newLinkedMap();
		for (FieldProperty<Object> p : props) {
			propMap.putTyped(p, p.getDefaultValue());
		}
		return propMap;
	}
	
	private final T defaultValue;
	
	protected FieldProperty(String name, Class<T> valueClass, T defaultValue) {
		super(name, valueClass);
		if (defaultValue == null) throw new IllegalArgumentException("defaultValue cannot be null");
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getName() {
		return getKey();
	}
	
	@Override
	public T getDefaultValue() {
		return defaultValue;
	}
}
