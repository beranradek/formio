/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.validation;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Locates a resource bundle for given locale.
 * @author Radek Beran
 */
public interface ResBundleLocator {

	/**
	 * Returns a resource bundle for the given locale.
	 * @param locale A locale, for which a resource bundle shall be retrieved. Must not be null.
	 * @return A resource bundle for the given locale. May be null, if no such bundle exists.
	 */
	ResourceBundle getResourceBundle(Locale locale);
}
