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
package net.formio.choice;

import java.util.Locale;

import net.formio.common.BundleMessageTranslator;

/**
 * Default implementation of {@link ChoiceRenderer}.
 * @author Radek Beran
 *
 * @param <T>
 */
public class DefaultChoiceRenderer<T> implements ChoiceRenderer<T> {

	private final Locale locale;
	
	public DefaultChoiceRenderer(Locale locale) {
		this.locale = locale;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Returns {@link ChoiceItem} such that id is:
	 * Id of {@link Identified} item; or name of enum constant
	 * if the item is of an enum type; or index as a String.
	 * Title is: Title of {@link Titled} item; or localized name of enum constant
	 * if the item is of an enum type; or toString of the item.
	 */
	@Override
	public ChoiceItem getItem(T item, int itemIndex) {
		return ChoiceItem.valueOf(getChoiceId(item, itemIndex), getChoiceTitle(item));
	}
	
	private String getChoiceId(T item, int itemIndex) {
		String id = "" + itemIndex;
		if (item instanceof Identified) {
			id = "" + ((Identified<?>)item).getId();
		} else if (item != null && item.getClass().isEnum()) {
			id = ((Enum<?>)item).name();
		}
		return id;
	}

	private String getChoiceTitle(T item) {
		String title = "???";
		if (item instanceof Titled) {
			title = ((Titled)item).getTitle();
		} else if (item != null && item.getClass().isEnum()) {
			Enum<?> e = (Enum<?>)item;
			BundleMessageTranslator tr = new BundleMessageTranslator(item.getClass(), this.locale);
			title = tr.getMessage(e.name());
		} else if (item != null) {
			title = "" + item.toString();
		}
		return title;
	}

}
