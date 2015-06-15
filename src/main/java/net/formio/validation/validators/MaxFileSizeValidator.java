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
import java.util.List;

import net.formio.upload.UploadedFile;
import net.formio.validation.Arg;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.ValidationContext;
import net.formio.validation.constraints.MaxFileSize;
import net.formio.validation.constraints.MaxFileSizeValidation;

/**
 * Maximum file size validator.
 * @author Radek Beran
 */
public class MaxFileSizeValidator extends AbstractValidator<UploadedFile> {
	
	protected static final String MAX_ARG = "max";
	private final String maxFileSizeStr;
	
	/**
	 * @param maxFileSizeStr Max file size e.g. "2MB", "1.2GB"
	 * @return
	 */
	public static MaxFileSizeValidator getInstance(String maxFileSizeStr) {
		return new MaxFileSizeValidator(maxFileSizeStr); 
	}
	
	private MaxFileSizeValidator(String maxFileSizeStr) {
		this.maxFileSizeStr = maxFileSizeStr;
	}

	@Override
	public <U extends UploadedFile> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			if (!MaxFileSizeValidation.isValid(ctx.getValidatedValue().getSize(), maxFileSizeStr)) {
				msgs.add(error(ctx.getElementName(), MaxFileSize.MESSAGE, 
					new Arg(MAX_ARG, maxFileSizeStr)));
			}
		}
		return msgs;
	}
	
	public String getMaxFileSizeStr() {
		return maxFileSizeStr;
	}
}
