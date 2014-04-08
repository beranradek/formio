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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic {@link InterpolatedMessage} that has no parameters.
 * @author Radek Beran
 */
public class DefaultInterpolatedMessage implements InterpolatedMessage, Serializable {
	private static final long serialVersionUID = 4956507046430112207L;

	/**
	 * Message key for translation file.
	 * @return
	 */
	@Override
	public String getMessageKey() {
		// interpolated message key must be enclosed in braces otherwise it will not be translated
		return "{" + this.getClass().getSimpleName() + ".message}";
	}
	
	/**
	 * Message parameters for translation file.
	 * @return
	 */
	@Override
	public Map<String, Serializable> getMessageParameters() {
		return new HashMap<String, Serializable>();
	}
}
