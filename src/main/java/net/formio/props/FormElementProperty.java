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

import net.formio.ajax.action.HandledJsEvent;
import net.formio.common.heterog.AbstractTypedKey;
import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;
import net.formio.props.types.ButtonType;
import net.formio.props.types.InlinePosition;

/**
 * Common form element properties.
 * @author Radek Beran
 */
public class FormElementProperty<T> extends AbstractTypedKey<String, T> implements Property<T> {
	private static final long serialVersionUID = 4271239940342562765L;
	protected static List<FormElementProperty<Object>> props;
	
	private static final HandledJsEvent[] EMPTY_EVENT_TO_ACTIONS = new HandledJsEvent[0];
	
	static {
		props = new ArrayList<FormElementProperty<Object>>();
	}
	
	/** Whether the form element should be visible - rendered at all. */
	public static final FormElementProperty<Boolean> VISIBLE = register(new FormElementProperty<Boolean>("visible", Boolean.class, Boolean.TRUE));
	
	/** Whether the form element should be rendered in enabled state. */
	public static final FormElementProperty<Boolean> ENABLED = register(new FormElementProperty<Boolean>("enabled", Boolean.class, Boolean.TRUE));
	
	/** Whether the form element should be rendered as read-only. */
	public static final FormElementProperty<Boolean> READ_ONLY = register(new FormElementProperty<Boolean>("readonly", Boolean.class, Boolean.FALSE));
	
	/** Help/info for the form element. */
	public static final FormElementProperty<String> HELP = register(new FormElementProperty<String>("help", String.class, ""));
	
	/** Whether the first "Choose one" option should be rendered in select. */
	public static final FormElementProperty<Boolean> CHOOSE_OPTION_DISPLAYED = register(new FormElementProperty<Boolean>("chooseOptionDisplayed", Boolean.class, Boolean.FALSE));
	
	/** Title for the first "Choose one" option of select. */
	public static final FormElementProperty<String> CHOOSE_OPTION_TITLE = register(new FormElementProperty<String>("chooseOptionTitle", String.class, "Choose One"));
	
	/** Placeholder text that will appear (as a hint/label) inside the text field. */
	public static final FormElementProperty<String> PLACEHOLDER = register(new FormElementProperty<String>("placeholder", String.class, null));
	
	/** Whether the label of form element is visible. */
	public static final FormElementProperty<Boolean> LABEL_VISIBLE = register(new FormElementProperty<Boolean>("labelVisible", Boolean.class, Boolean.TRUE));
	
	/** Whether the form field or mapping is independent on the form object, is not filled, nor bound from request. */
	public static final FormElementProperty<Boolean> DETACHED = register(new FormElementProperty<Boolean>("detached", Boolean.class, Boolean.FALSE));
	
	/** Type of inlined form field. */
	public static final FormElementProperty<InlinePosition> INLINE = register(new FormElementProperty<InlinePosition>("inline", InlinePosition.class, null));
	
	/** Width of input in number of responsive grid columns. */
	public static final FormElementProperty<Integer> COL_INPUT_WIDTH = register(new FormElementProperty<Integer>("colInputWidth", Integer.class, null));
	
	/** Width of label in number of responsive grid columns. */
	public static final FormElementProperty<Integer> COL_LABEL_WIDTH = register(new FormElementProperty<Integer>("colLabelWidth", Integer.class, null));
	
	// TDI properties
	public static final FormElementProperty<HandledJsEvent[]> DATA_AJAX_ACTIONS = register(new FormElementProperty<HandledJsEvent[]>("dataAjaxActions", HandledJsEvent[].class, EMPTY_EVENT_TO_ACTIONS));	
	public static final FormElementProperty<String> DATA_RELATED_ELEMENT = register(new FormElementProperty<String>("dataRelatedElement", String.class, ""));
	public static final FormElementProperty<String> DATA_RELATED_ANCESTOR = register(new FormElementProperty<String>("dataRelatedAncestor", String.class, ""));
	public static final FormElementProperty<String> DATA_CONFIRM = register(new FormElementProperty<String>("dataConfirm", String.class, ""));
	
	// Render hints for which there are no convenience accessors or setters in builders
	/** HTML multiple attribute. */
	public static final FormElementProperty<Boolean> MULTIPLE = new FormElementProperty<Boolean>("multiple", Boolean.class, Boolean.FALSE);
	
	/** HTML size attribute. */
	public static final FormElementProperty<Integer> SIZE = new FormElementProperty<Integer>("size", Integer.class, null);
	
	/** HTML cols attribute for textareas. */
	public static final FormElementProperty<Integer> COLS = new FormElementProperty<Integer>("cols", Integer.class, null);
	
	/** HTML rows attribute for textareas. */
	public static final FormElementProperty<Integer> ROWS = new FormElementProperty<Integer>("rows", Integer.class, null);
	
	/** HTML maxlength attribute for inputs. */
	public static final FormElementProperty<Integer> MAX_LENGTH = new FormElementProperty<Integer>("maxlength", Integer.class, null);
	
	/** MIME types accepted by file upload input. */
	public static final FormElementProperty<String> ACCEPT = new FormElementProperty<String>("accept", String.class, null);
	
	/** Type of button: submit, reset or button. */
	public static final FormElementProperty<ButtonType> BUTTON_TYPE = new FormElementProperty<ButtonType>("buttonType", ButtonType.class, null);
	
	/** Render surrounding fieldset HTML element. */
	public static final FormElementProperty<Boolean> FIELDSET_DISPLAYED = new FormElementProperty<Boolean>("fieldsetDisplayed", Boolean.class, null);
	
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
	public static HeterogMap<String> createDefaultProperties() {
		HeterogMap<String> propMap = HeterogCollections.<String>newLinkedMap();
		for (FormElementProperty<Object> p : props) {
			if (p.getDefaultValue() != null) {
				propMap.putTyped(p, p.getDefaultValue());
			}
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
