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
/*
 * Created on 8.12.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package net.formio.validation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.formio.FormUtils;
import net.formio.Forms;
import net.formio.ReflectionException;
import net.formio.binding.HumanReadableType;
import net.formio.binding.ParseError;
import net.formio.upload.MaxFileSizeExceededError;
import net.formio.upload.RequestProcessingError;

/**
 * Object validator using {@link ValidatorFactory}.
 *
 * @author Radek Beran
 * @author Jan Simek
 */
public class ValidationApiBeanValidator implements BeanValidator {
	
	private final ValidatorFactory validatorFactory;
	private final String messageBundleName;
	private final Locale locale;
	
	public ValidationApiBeanValidator(ValidatorFactory validatorFactory, String messageBundleName, Locale locale) {
		if (validatorFactory == null) throw new IllegalArgumentException("validatorFactory cannot be null");
		if (messageBundleName == null || messageBundleName.isEmpty()) throw new IllegalArgumentException("messageBundleName cannot be null or empty");
		if (locale == null) throw new IllegalArgumentException("locale cannot be null");
		this.validatorFactory = validatorFactory;
		this.messageBundleName = messageBundleName;
		this.locale = locale;
	}
	
	@Override
	public <T> ValidationResult validate(T inst, 
		String propPrefix, 
		List<RequestProcessingError> requestFailures,
		List<ParseError> parseErrors, 
		Class<?>... groups) {
		if (inst == null) {
			throw new IllegalArgumentException("Validated object cannot be null");
		}
		MessageInterpolator msgInterpolator = createMessageInterpolator(this.validatorFactory, this.messageBundleName, this.locale);
		Validator validator = createValidator(this.validatorFactory, msgInterpolator);
		Set<ConstraintViolation<T>> violations = validator.validate(inst, groups);
		return buildReport(msgInterpolator, violations, requestFailures, parseErrors, propPrefix);
	}
	
	@Override
	public boolean isRequired(Class<?> cls, String propertyName) {
		boolean required = false;
		try {
			final Field fld = cls.getDeclaredField(propertyName);
			if (fld != null && isRequired(fld.getAnnotations(), 0)) {
				required = true;
			}
		} catch (NoSuchFieldException ex) {
			throw new ReflectionException("Error while checking if property " + propertyName + " of class " + cls.getName() + " is required: " + ex.getMessage(), ex);
		}
		return required;
	}
	
	/**
	 * Returns message interpolator used in validation.
	 * Can be overriden in subclasses.
	 * @param validatorFactory
	 * @param locale
	 * @return
	 */
	protected MessageInterpolator createMessageInterpolator(ValidatorFactory validatorFactory, String messageBundleName, Locale locale) {
		return new ResBundleMessageInterpolator(new PlatformResBundleLocator(messageBundleName), locale, true);
	}
	
	/**
	 * Translates given message by given created message interpolator, using given parameters
	 * and locale.
	 * @param msgInterpolator
	 * @param message
	 * @param parameters
	 * @param locale
	 * @return
	 */
	protected String interpolateMessage(MessageInterpolator msgInterpolator, String message, Map<String, Serializable> parameters, Locale locale) {
		return ((ResBundleMessageInterpolator)msgInterpolator).interpolateMessage(message, parameters, locale);
	}
	
	protected Validator createValidator(ValidatorFactory validatorFactory, MessageInterpolator msgInterpolator) {
		// for using specified locale
		return validatorFactory
			.usingContext()
			.messageInterpolator(msgInterpolator)
			.getValidator();
	}
	
	protected void processRequestErrors(
			MessageInterpolator msgInterpolator,
			List<RequestProcessingError> requestFailures, String propPrefix,
			Map<String, Set<ConstraintViolationMessage>> fieldMessages,
			Set<ConstraintViolationMessage> globalMessages) {
		for (RequestProcessingError error : requestFailures) {
			if (error instanceof MaxFileSizeExceededError) {
				MaxFileSizeExceededError err = (MaxFileSizeExceededError)error;
				Set<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, err.getFieldName());
				msgs.add(new ConstraintViolationMessage(Severity.ERROR, 
					interpolateMsg(msgInterpolator, error),
					error.getMessageKey(),
					error.getMessageParameters()));	
				fieldMessages.put(err.getFieldName(), msgs);
			} else if (error != null && ValidationUtils.isTopLevelMapping(propPrefix)) {
				// other request processing errors
				globalMessages.add(new ConstraintViolationMessage(Severity.ERROR, 
					interpolateMsg(msgInterpolator, error),
					ValidationUtils.removeBraces(error.getMessageKey()),
					error.getMessageParameters()));
			}
		}
	}
	
	protected void processParseErrors(
			MessageInterpolator msgInterpolator,
			List<ParseError> parseErrors,
			String propPrefix,
			Map<String, Set<ConstraintViolationMessage>> fieldMessages) {
		for (ParseError err : parseErrors) {
			String formPrefixedPropName = pathPrefixedName(propPrefix, err.getPropertyName());
			Set<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, formPrefixedPropName);
			msgs.add(new ConstraintViolationMessage(
				Severity.ERROR, 
				interpolateParseErrorMsg(msgInterpolator, err), 
				err.getMessageKey(),
				err.getMessageParameters()));
			fieldMessages.put(formPrefixedPropName, msgs);
		}
	}
	
	private String interpolateParseErrorMsg(MessageInterpolator msgInterpolator, ParseError msg) {
		HumanReadableType hrt = msg.getHumanReadableTargetType();
		String humanReadableTargetType = interpolateMessage(msgInterpolator, humanReadableTypeToMsgTpl(hrt), Collections.<String, Serializable>emptyMap(), this.locale);
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.putAll(msg.getMessageParameters());
		params.put("humanReadableTargetType", humanReadableTargetType);
		return ValidationUtils.removeBraces(interpolateMessage(msgInterpolator, msg.getMessageKey(), params, this.locale));
	}
	
	private String interpolateMsg(MessageInterpolator msgInterpolator, InterpolatedMessage msg) {
		return ValidationUtils.removeBraces(interpolateMessage(msgInterpolator, msg.getMessageKey(), msg.getMessageParameters(), this.locale));
	}
	
	private String humanReadableTypeToMsgTpl(HumanReadableType hrt) {
		return "{type." + hrt.name() + "}";
	}
	
	private Set<ConstraintViolationMessage> getOrCreateFieldMessages(Map<String, Set<ConstraintViolationMessage>> fieldMsgs, String fieldName) {
		Set<ConstraintViolationMessage> msgs = fieldMsgs.get(fieldName);
		if (msgs == null) {
			msgs = new LinkedHashSet<ConstraintViolationMessage>();
		}
		return msgs;
	}
	
	private <T> ValidationResult buildReport(MessageInterpolator msgInterpolator, Set<ConstraintViolation<T>> violations, 
		List<RequestProcessingError> requestFailures,
		List<ParseError> parseErrors,	
		String propPrefix) {
		Map<String, Set<ConstraintViolationMessage>> fieldMessages = new LinkedHashMap<String, Set<ConstraintViolationMessage>>();
		Set<ConstraintViolationMessage> globalMessages = new LinkedHashSet<ConstraintViolationMessage>();
		
		// request processing errors
		processRequestErrors(msgInterpolator, requestFailures, propPrefix, fieldMessages, globalMessages);
		
		// processing parse errors (in addition to validation errors)
		processParseErrors(msgInterpolator, parseErrors, propPrefix, fieldMessages);
		
		if (!violations.isEmpty()) {
			for (ConstraintViolation<T> v : violations) {
				Path path = v.getPropertyPath();
				StringBuilder nodePath = new StringBuilder();
				if (propPrefix != null) {
					nodePath.append(propPrefix);
				}
				for (Node node : path) {
					if (node.getName() != null) {
						if (nodePath.length() > 0) {
							nodePath.append(Forms.PATH_SEP);
						}
						nodePath.append(node.getName());
					}
				}
				String fieldName = FormUtils.removeTrailingBrackets(nodePath.toString());
				// Needed data should be taken from javax.ConstraintViolation, 
				// ConstraintViolationMessage should be javax.validation API independent
				ConstraintViolationMessage msg = new ConstraintViolationMessage(v);
				if (fieldName.length() == 0 || !fieldName.contains(Forms.PATH_SEP)) {
					// only prefix without property name
					globalMessages.add(msg);
				} else {
					appendFieldMsg(fieldMessages, fieldName, msg);
				}
			}
		}
		return new ValidationResult(fieldMessages, globalMessages);
	}
	
	private void appendFieldMsg(Map<String, Set<ConstraintViolationMessage>> fieldMessages, String fieldName, ConstraintViolationMessage msg) {
		if (fieldMessages.containsKey(fieldName)) {
			fieldMessages.get(fieldName).add(msg);
		} else {
			Set<ConstraintViolationMessage> msgSet = new LinkedHashSet<ConstraintViolationMessage>();
			msgSet.add(msg);
			fieldMessages.put(fieldName, msgSet);
		}
	}
	
	private String pathPrefixedName(String pathPrefix, String name) {
		if (name == null) return null;
		return pathPrefix + Forms.PATH_SEP + name;
	}
	
	private boolean isRequired(Annotation[] annots, int level) {
		boolean required = false;
		if (level < 2) {
			if (annots != null) {
				for (Annotation ann : annots) {
					if (ann instanceof Size) {
						Size s = (Size) ann;
						if (s.min() > 0) {
							required = true;
							break;
						}
					} else if (ann instanceof NotNull) {
						required = true;
					} else {
						if (isRequired(ann.annotationType().getAnnotations(), level + 1)) {
							required = true;
							break;
						}
					}
				}
			}
		}
		return required;
	}
}
