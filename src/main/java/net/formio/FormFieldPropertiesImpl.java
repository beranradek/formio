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

import net.formio.common.heterog.HeterogMap;
import net.formio.props.FormElementProperty;

/**
 * Default implementation of {@link FormFieldProperties}. Immutable.
 * @author Radek Beran
 */
public class FormFieldPropertiesImpl extends FormPropertiesImpl implements FormFieldProperties {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private static final long serialVersionUID = 8353865315646591562L;
	
	FormFieldPropertiesImpl(final HeterogMap<String> properties) {
		super(properties);
	}
	
	FormFieldPropertiesImpl(final FormFieldProperties src) {
		this(src, (FormElementProperty<?>)null, null);
	}
	
	<T> FormFieldPropertiesImpl(final FormFieldProperties src, FormElementProperty<T> property, T value) {
		super(src, property, value);
	}
	
	/**
	 * Returns new properties with added/replaced property.
	 * @param property
	 * @param value
	 * @return
	 */
	@Override
	<T> FormFieldProperties withProperty(FormElementProperty<T> property, T value) {
		return new FormFieldPropertiesImpl(copyProperties(this.getProperties(), property, value));
	}
	
	@Override
	public String getDataAjaxUrl() {
		return getProperties().getTyped(FormElementProperty.DATA_AJAX_URL);
	}
	
	@Override
	public String getDataRelatedElement() {
		return getProperties().getTyped(FormElementProperty.DATA_RELATED_ELEMENT);
	}
	
	@Override
	public String getDataRelatedAncestor() {
		return getProperties().getTyped(FormElementProperty.DATA_RELATED_ANCESTOR);
	}
	
	@Override
	public String getDataConfirm() {
		return getProperties().getTyped(FormElementProperty.DATA_CONFIRM);
	}
	
	@Override
	public boolean isChooseOptionDisplayed() {
		Boolean b = getProperties().getTyped(FormElementProperty.CHOOSE_OPTION_DISPLAYED);
		return b != null && b.booleanValue();
	}
	
	@Override
	public String getChooseOptionTitle() {
		return getProperties().getTyped(FormElementProperty.CHOOSE_OPTION_TITLE);
	}
}
