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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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

import net.formio.FormElement;
import net.formio.Forms;
import net.formio.ReflectionException;
import net.formio.binding.HumanReadableType;
import net.formio.binding.ParseError;
import net.formio.internal.FormUtils;
import net.formio.upload.MaxFileSizeExceededError;
import net.formio.upload.MaxRequestSizeExceededError;
import net.formio.validation.constraints.NotEmpty;

/**
 * Object validator using {@link ValidatorFactory}.
 *
 * @author Radek Beran
 */
public class ValidationApiBeanValidator implements BeanValidator {
	
	private final ValidatorFactory validatorFactory;
	private final String messageBundleName;
	
	public ValidationApiBeanValidator(ValidatorFactory validatorFactory, String messageBundleName) {
		if (validatorFactory == null) throw new IllegalArgumentException("validatorFactory cannot be null");
		if (messageBundleName == null || messageBundleName.isEmpty()) throw new IllegalArgumentException("messageBundleName cannot be null or empty");
		this.validatorFactory = validatorFactory;
		this.messageBundleName = messageBundleName;
	}
	
	public ValidationApiBeanValidator(ValidatorFactory validatorFactory) {
		this(validatorFactory, ResBundleMessageInterpolator.DEFAULT_VALIDATION_MESSAGES);
	}
	
	@Override
	public <T> ValidationResult validate(T inst, 
		String propPrefix, 
		List<? extends InterpolatedMessage> customMessages,
		Locale locale,
		Class<?>... groups) {
		if (inst == null) {
			throw new IllegalArgumentException("ValidationContext object cannot be null");
		}
		MessageInterpolator msgInterpolator = createMessageInterpolator(this.validatorFactory, this.messageBundleName, locale);
		Validator validator = createValidator(this.validatorFactory, msgInterpolator);
		
		// Unfortunately, implementation of bean validation API can return violations 
		// in nondeterministic order as a HashSet (Hibernate validator)
		final Set<ConstraintViolation<T>> violations = validator.validate(inst, groups);
		final List<ConstraintViolation<T>> violationsList = new ArrayList<ConstraintViolation<T>>(violations);
		Collections.sort(violationsList, constraintViolationComparator);
		return buildReport(msgInterpolator, violationsList, customMessages, propPrefix, locale);
	}
	
	@Override
	public <T> ValidationResult validate(T inst, Locale locale, Class<?> ... groups) {
		return this.validate(inst, (String)null, Collections.<InterpolatedMessage>emptyList(), locale, groups);
	}
	
	@Override
	public <T> ValidationResult validate(T inst, Class<?> ... groups) {
		return this.validate(inst, Locale.getDefault(), groups);
	}
	
	@Override
	public boolean isRequired(Class<?> cls, FormElement<?> element) {
		if (element.getPropertyName().equals(Forms.AUTH_TOKEN_FIELD_NAME)) {
			return false; // handled specially
		}
		boolean required = false;
		try {
			if (cls != null) {
				final Field fld = cls.getDeclaredField(element.getPropertyName());
				if (fld != null && isRequiredByAnnotations(fld.getAnnotations(), 0)) {
					// isRequiredByAnnotations is intentionally checked first because this
					// also checks if the field exists and throws exception in time of form definition
					// building if not.
					required = true;
				}
			}
			if (element.isRequired()) {
				required = true;
			}
		} catch (NoSuchFieldException ex) {
			throw new ReflectionException("Error while checking if property " + element.getPropertyName() + 
				" of class " + (cls != null ? cls.getName() : "<no class>") + 
				" is required, the corresponding field does not exist: " + ex.getMessage(), ex);
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
	
	protected void processInterpolatedMessages(
		MessageInterpolator msgInterpolator,
		List<? extends InterpolatedMessage> interpolatedMessages,
		String propPrefix,
		Map<String, List<ConstraintViolationMessage>> fieldMessages,
		List<ConstraintViolationMessage> globalMessages,
		Locale locale) {
		for (InterpolatedMessage im : interpolatedMessages) {
			if (im != null) {
				if (im instanceof MaxFileSizeExceededError) {
					MaxFileSizeExceededError mfs = (MaxFileSizeExceededError)im;
					List<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, mfs.getFieldName());
					msgs.add(createConstraintViolationMessage(im, msgInterpolator, locale));
					fieldMessages.put(mfs.getFieldName(), msgs);
				} else if (im instanceof MaxRequestSizeExceededError && ValidationUtils.isTopLevelMapping(propPrefix)) {
					globalMessages.add(createConstraintViolationMessage(im, msgInterpolator, locale));
				} else if (ValidationUtils.isTopLevelMapping(propPrefix)) {
					globalMessages.add(createConstraintViolationMessage(im, msgInterpolator, locale));
				} else if (im.getPropertyName() != null && !im.getPropertyName().isEmpty()) {
					String formPrefixedPropName = pathPrefixedName(propPrefix, im.getPropertyName());
					List<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, formPrefixedPropName);
					msgs.add(createConstraintViolationMessage(im, msgInterpolator, locale));
					fieldMessages.put(formPrefixedPropName, msgs);
				}
			}
		}
	}
	
	private ConstraintViolationMessage createConstraintViolationMessage(
		InterpolatedMessage message, MessageInterpolator msgInterpolator, Locale locale) {
		return new ConstraintViolationMessage(message.getSeverity(), 
			interpolateMessage(msgInterpolator, message, locale),
			ValidationUtils.removeBraces(message.getMessageKey()),
			message.getMessageParameters());
	}
	
	private String interpolateMessage(MessageInterpolator msgInterpolator, InterpolatedMessage msg, Locale locale) {
		Map<String, Serializable> params = new LinkedHashMap<String, Serializable>();
		params.putAll(msg.getMessageParameters());
		if (msg instanceof ParseError) {
			ParseError parseError = (ParseError)msg;
			params.put("humanReadableTargetType", interpolateMessage(msgInterpolator, 
				humanReadableTypeToMsgTpl(parseError.getHumanReadableTargetType()), 
				Collections.<String, Serializable>emptyMap(), locale));
		}
		return ValidationUtils.removeBraces(interpolateMessage(msgInterpolator, msg.getMessageKey(), params, locale));
	}
	
	private String humanReadableTypeToMsgTpl(HumanReadableType hrt) {
		return "{type." + hrt.name() + "}";
	}
	
	private List<ConstraintViolationMessage> getOrCreateFieldMessages(Map<String, List<ConstraintViolationMessage>> fieldMsgs, String fieldName) {
		List<ConstraintViolationMessage> msgs = fieldMsgs.get(fieldName);
		if (msgs == null) {
			msgs = new ArrayList<ConstraintViolationMessage>();
		}
		return msgs;
	}
	
	private <T> ValidationResult buildReport(
		MessageInterpolator msgInterpolator, 
		List<ConstraintViolation<T>> violations, 
		List<? extends InterpolatedMessage> customMessages,
		String propPrefix,
		Locale locale) {
		Map<String, List<ConstraintViolationMessage>> fieldMessages = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		List<ConstraintViolationMessage> globalMessages = new ArrayList<ConstraintViolationMessage>();
		
		// processing parse errors and request processing errors and other custom errors (in addition to bean validation API violations)
		processInterpolatedMessages(msgInterpolator, customMessages, propPrefix, fieldMessages, globalMessages, locale);
		
		for (ConstraintViolation<T> v : violations) {
			// Needed data should be taken from javax.ConstraintViolation, 
			// ConstraintViolationMessage should be javax.validation API independent
			String formElementName = constructFormElementName(propPrefix, v);
			ConstraintViolationMessage msg = new ConstraintViolationMessage(v);
			if (formElementName.length() == 0 || !formElementName.contains(Forms.PATH_SEP)) {
				globalMessages.add(msg);
			} else {
				appendFieldMsg(fieldMessages, formElementName, msg);
			}
		}
		return new ValidationResult(fieldMessages, globalMessages);
	}

	private <T> String constructFormElementName(String propPrefix, ConstraintViolation<T> v) {
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
		return FormUtils.removeTrailingBrackets(nodePath.toString());
	}
	
	private void appendFieldMsg(Map<String, List<ConstraintViolationMessage>> fieldMessages, String fieldName, ConstraintViolationMessage msg) {
		if (fieldMessages.containsKey(fieldName)) {
			fieldMessages.get(fieldName).add(msg);
		} else {
			List<ConstraintViolationMessage> msgs = new ArrayList<ConstraintViolationMessage>();
			msgs.add(msg);
			fieldMessages.put(fieldName, msgs);
		}
	}
	
	private String pathPrefixedName(String pathPrefix, String name) {
		if (name == null) return null;
		if (pathPrefix == null || pathPrefix.isEmpty()) return name;
		return pathPrefix + Forms.PATH_SEP + name;
	}
	
	private boolean isRequiredByAnnotations(Annotation[] annots, int level) {
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
						break;
					} else if (ann instanceof NotEmpty) {
						required = true;
						break;
					} else {
						if (isRequiredByAnnotations(ann.annotationType().getAnnotations(), level + 1)) {
							required = true;
							break;
						}
					}
				}
			}
		}
		return required;
	}
	
	private static final ConstraintViolationComparator constraintViolationComparator = new ConstraintViolationComparator(); 
}
