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

import net.formio.common.heterog.HeterogMap;

/**
 * Default implementation of {@link FormMappingProperties}. Immutable.
 * @author Radek Beran
 */
public class FormMappingPropertiesImpl extends FormPropertiesImpl implements FormMappingProperties {
	// public because of introspection required by some template frameworks
	
	private static final long serialVersionUID = -4067260347034795236L;

	/** For internal use only. */
	public FormMappingPropertiesImpl(final HeterogMap<String> properties) {
		super(properties);
	}
	
	/** For internal use only. */
	public FormMappingPropertiesImpl(final FormMappingProperties src) {
		this(src, (FormElementProperty<?>)null, null);
	}
	
	/** For internal use only. */
	public <T> FormMappingPropertiesImpl(final FormMappingProperties src, FormElementProperty<T> property, T value) {
		super(src, property, value);
	}
	
	@Override
	public boolean isFieldsetDisplayed() {
		Boolean b = getProperty(FormElementProperty.FIELDSET_DISPLAYED);
		return b != null && b.booleanValue();
	}
}
