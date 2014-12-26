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

/**
 * Renders items for a form choice - ids and titles.
 * @author Radek Beran
 *
 * @param <T>
 */
public interface ChoiceRenderer<T> {
	
	/**
	 * Returns id of choice item.
	 * The id can be extracted from the object like a primary key; or if the list is stable 
	 * you can return an item index as a String.
	 * @param item
	 * @param itemIndex
	 * @return
	 */
	String getId(T item, int itemIndex);
	
	/**
	 * Returns displayed title of choice item.
	 * @param item
	 * @param itemIndex
	 * @return
	 */
	Object getTitle(T item, int itemIndex);
}
