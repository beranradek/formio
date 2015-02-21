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

import net.formio.choice.ChoiceItem;
import net.formio.choice.ChoiceRenderer;

/**
 * Methods for manipulation with choice items.
 * @author Radek Beran
 */
class ChoiceItems {
	/**
	 * Converts values from request (choosen ids) to choice items (from a codebook).
	 * @param field
	 * @param requestValues
	 * @return
	 */
	static <U> U[] convertParamsToChoiceItems(FormField<U> field, String[] requestValues) {
		U[] items = (U[])new Object[requestValues.length];
		for (int i = 0; i < requestValues.length; i++) {
			String itemId = requestValues[i];
			items[i] = findChoiceItem(field, itemId);
		}
		return items;
	}
	
	/**
	 * Returns choice item (item from a codebook) with given rendered itemId, 
	 * or {@code null} if not found.
	 * @param field
	 * @param itemId
	 * @return
	 */
	private static <U> U findChoiceItem(FormField<U> field, String itemId) {
		U foundItem = null;
		if (itemId != null && field.getChoices() != null) {
			List<? extends U> items = field.getChoices().getItems();
			if (items != null) {
				ChoiceRenderer<U> choiceRenderer = field.getChoiceRenderer();
				int itemIndex = 0;
				for (U item : items) {
					ChoiceItem choiceItem = choiceRenderer.getItem(item, itemIndex);
					if (choiceItem.getId() != null && choiceItem.getId().equals(itemId)) {
						foundItem = item;
						break;
					}
					itemIndex++;
				}
			}
		}
		return foundItem;
	}
}
