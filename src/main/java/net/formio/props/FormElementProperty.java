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
 * Common form element properties.
 * @author Radek Beran
 */
public class FormElementProperty<T> extends AbstractTypedKey<String, T> implements Property<T> {
	private static final long serialVersionUID = 4271239940342562765L;
	protected static List<FormElementProperty<Object>> props;
	
	static {
		props = new ArrayList<FormElementProperty<Object>>();
	}
	
	public static final FormElementProperty<Boolean> VISIBLE = register(new FormElementProperty<Boolean>("visible", Boolean.class, Boolean.TRUE));
	public static final FormElementProperty<Boolean> ENABLED = register(new FormElementProperty<Boolean>("enabled", Boolean.class, Boolean.TRUE));
	public static final FormElementProperty<Boolean> READ_ONLY = register(new FormElementProperty<Boolean>("readonly", Boolean.class, Boolean.FALSE));
	public static final FormElementProperty<String> HELP = register(new FormElementProperty<String>("help", String.class, ""));
	public static final FormElementProperty<Boolean> CHOOSE_OPTION_DISPLAYED = register(new FormElementProperty<Boolean>("chooseOptionDisplayed", Boolean.class, Boolean.FALSE));
	public static final FormElementProperty<String> CHOOSE_OPTION_TITLE = register(new FormElementProperty<String>("chooseOptionTitle", String.class, "Choose One"));
	
	// TDI properties
	public static final FormElementProperty<String> DATA_AJAX_URL = register(new FormElementProperty<String>("dataAjaxUrl", String.class, ""));
	public static final FormElementProperty<JsEventToUrl[]> DATA_AJAX_EVENTS = register(new FormElementProperty<JsEventToUrl[]>("dataAjaxEvent", JsEventToUrl[].class, new JsEventToUrl[0]));
	public static final FormElementProperty<String> DATA_RELATED_ELEMENT = register(new FormElementProperty<String>("dataRelatedElement", String.class, ""));
	public static final FormElementProperty<String> DATA_RELATED_ANCESTOR = register(new FormElementProperty<String>("dataRelatedAncestor", String.class, ""));
	public static final FormElementProperty<String> DATA_CONFIRM = register(new FormElementProperty<String>("dataConfirm", String.class, ""));
	
	protected static <T> FormElementProperty<T> register(FormElementProperty<T> prop) {
		if (props.contains(prop)) {
			throw new IllegalArgumentException("Property with name '" + prop.getName() + "' is already registered.");
		}
		props.add((FormElementProperty<Object>)prop);
		return prop;
	}
	
	public static List<FormElementProperty<Object>> getValues() {
		return Collections.unmodifiableList(props);
	}
	
	public static <T> FormElementProperty<T> fromName(String propName) {
		FormElementProperty<T> prop = null;
		if (propName != null) {
			for (FormElementProperty<Object> p : props) {
				if (p.getName().equals(propName)) {
					prop = (FormElementProperty<T>)p;
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
		for (FormElementProperty<Object> p : props) {
			propMap.putTyped(p, p.getDefaultValue());
		}
		return propMap;
	}
	
	private final T defaultValue;
	
	protected FormElementProperty(String name, Class<T> valueClass, T defaultValue) {
		super(name, valueClass);
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
