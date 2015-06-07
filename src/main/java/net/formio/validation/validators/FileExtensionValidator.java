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
package net.formio.validation.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;
import net.formio.validation.constraints.FileExtension;
import net.formio.validation.constraints.FileExtensionValidation;

/**
 * File extension validator.
 * @author Radek Beran
 */
public class FileExtensionValidator extends AbstractValidator<String> {
	
	protected static final String ALLOWED_EXTENSIONS_ARG = "allowedExtensions";
	
	private final String[] allowedExtensions;
	private final boolean ignoreCase;
	
	public static FileExtensionValidator getInstance(String[] allowedExtensions, boolean ignoreCase) {
		return new FileExtensionValidator(allowedExtensions, ignoreCase); 
	}
	
	private FileExtensionValidator(String[] allowedExtensions, boolean ignoreCase) {
		this.allowedExtensions = allowedExtensions;
		this.ignoreCase = ignoreCase;
	}

	@Override
	public List<InterpolatedMessage> validate(ValidationContext<String> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null && !ctx.getValidatedValue().isEmpty()) {
			if (!FileExtensionValidation.hasFileExtension(ctx.getValidatedValue(), allowedExtensions, ignoreCase)) {
				msgs.add(error(ctx.getElementName(), FileExtension.MESSAGE, 
					new Arg(VALUE_ARG, ctx.getValidatedValue()),
					new Arg(ALLOWED_EXTENSIONS_ARG, extensionsToString(allowedExtensions))));
			}
		}
		return msgs;
	}
	
	public String[] getAllowedExtensions() {
		return allowedExtensions;
	}
	
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	
	private String extensionsToString(String[] exts) {
		String str = exts != null ? Arrays.toString(exts) : "";
		if (str != null && !str.isEmpty()) {
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}
}
