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
import java.util.ArrayList;
import java.util.Collections;
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

import net.formio.BasicListFormMapping;
import net.formio.Config;
import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.binding.BeanExtractor;
import net.formio.binding.HumanReadableType;
import net.formio.binding.ParseError;
import net.formio.internal.FormUtils;
import net.formio.upload.MaxRequestSizeExceededError;

/**
 * Object validation using {@link ValidatorFactory} (bean validation API).
 *
 * @author Radek Beran
 */
public class DefaultBeanValidator implements BeanValidator {
	
	private final ValidatorFactory validatorFactory;
	private final BeanExtractor beanExtractor;
	private final String messageBundleName;
	
	public DefaultBeanValidator(ValidatorFactory validatorFactory, BeanExtractor beanExtractor, String messageBundleName) {
		if (validatorFactory == null) throw new IllegalArgumentException("validatorFactory cannot be null");
		if (beanExtractor == null) throw new IllegalArgumentException("beanExtractor cannot be null");
		if (messageBundleName == null || messageBundleName.isEmpty()) throw new IllegalArgumentException("messageBundleName cannot be null or empty");
		this.validatorFactory = validatorFactory;
		this.beanExtractor = beanExtractor;
		this.messageBundleName = messageBundleName;
	}
	
	public DefaultBeanValidator(ValidatorFactory validatorFactory, BeanExtractor beanExtractor) {
		this(validatorFactory, beanExtractor, ResBundleMessageInterpolator.DEFAULT_VALIDATION_MESSAGES);
	}
	
	@Override
	public <T> ValidationResult validate(
		T mappingBoundValue,
		String propPrefix,
		FormMapping<T> mapping,
		List<? extends InterpolatedMessage> customMessages, 
		Locale locale,
		Class<?>... groups) {
		if (mappingBoundValue == null) {
			throw new IllegalArgumentException("Validated object cannot be null");
		}
		MessageInterpolator msgInterpolator = createMessageInterpolator(this.validatorFactory, this.messageBundleName, locale);
		Validator beanValidator = createValidator(this.validatorFactory, msgInterpolator);
		
		// Unfortunately, implementation of bean validation API can return violations 
		// in nondeterministic order as a HashSet (Hibernate validator)
		final Set<ConstraintViolation<T>> violations = beanValidator.validate(mappingBoundValue, groups);
		final List<ConstraintViolation<T>> violationsList = new ArrayList<ConstraintViolation<T>>(violations);
		Collections.sort(violationsList, constraintViolationComparator);
		
		List<InterpolatedMessage> allCustomMessages = new ArrayList<InterpolatedMessage>();
		allCustomMessages.addAll(customMessages);
		
		String pathSep = null;
		if (mapping != null && !(mapping instanceof BasicListFormMapping<?>) && mapping.isVisible() && mapping.isEnabled()) {
			pathSep = mapping.getConfig().getPathSeparator();
			
			// Validate all nested elements
			Map<String, Object> beanProperties = null;
			for (FormElement<?> el : mapping.getElements()) {
				if (el.getValidators() != null && !el.getValidators().isEmpty()) { // to avoid unnecessary visible/enabled checks
					if (!(el instanceof BasicListFormMapping<?>) && el.isVisible() && el.isEnabled()) {
						if (beanProperties == null) {
							beanProperties = beanExtractor.extractBean(mappingBoundValue, gatherPropertyNames(mapping.getElements()));
						}
						Object elementValue = beanProperties.get(el.getPropertyName());
						allCustomMessages.addAll(validateFormElement((FormElement<Object>)el, elementValue));
					}
				}
			}
			if (mapping.isRootMapping()) {
				// validate also the root mapping (run global validators added to the root mapping itself) 
				for (net.formio.validation.Validator<T> validator : mapping.getValidators()) {
					allCustomMessages.addAll(validator.validate(
						new ValidationContext<T>(mapping.getName(), mappingBoundValue)));
				}
			}
		} else {
			pathSep = Config.DEFAULT_PATH_SEP;
		}
		
		return buildReport(msgInterpolator, violationsList, allCustomMessages, propPrefix, pathSep, locale);
	}

	@Override
	public <T> ValidationResult validate(T inst, 
		String propPrefix, 
		List<? extends InterpolatedMessage> customMessages,
		Locale locale,
		Class<?>... groups) {
		return validate(inst, propPrefix, (FormMapping<T>)null, customMessages, locale, groups);
	}
	
	@Override
	public <T> ValidationResult validate(T inst, Locale locale, Class<?> ... groups) {
		return this.validate(inst, (String)null, Collections.<InterpolatedMessage>emptyList(), locale, groups);
	}
	
	@Override
	public <T> ValidationResult validate(T inst, Class<?> ... groups) {
		return this.validate(inst, Locale.getDefault(), groups);
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
		String pathSep,
		Map<String, List<ConstraintViolationMessage>> fieldMessages,
		List<ConstraintViolationMessage> globalMessages,
		Locale locale) {
		for (InterpolatedMessage im : interpolatedMessages) {
			if (im != null) {
				if (im instanceof MaxRequestSizeExceededError) {
					if (ValidationUtils.isTopLevelMapping(propPrefix, pathSep)) {
						globalMessages.add(createConstraintViolationMessage(im, msgInterpolator, locale));
					}
				} else if (im instanceof ParseError) {
					ParseError parseMsg = (ParseError)im;
					String formPrefixedPropName = pathPrefixedName(propPrefix, parseMsg.getPropertyName(), pathSep);
					List<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, formPrefixedPropName);
					msgs.add(createConstraintViolationMessage(im, msgInterpolator, locale));
					fieldMessages.put(formPrefixedPropName, msgs);
				} else if (im.getElementName() != null && !im.getElementName().isEmpty()) {
					// Also MaxFileSizeExceededError is processed here
					List<ConstraintViolationMessage> msgs = getOrCreateFieldMessages(fieldMessages, im.getElementName());
					msgs.add(createConstraintViolationMessage(im, msgInterpolator, locale));
					fieldMessages.put(im.getElementName(), msgs);
				} else {
					globalMessages.add(createConstraintViolationMessage(im, msgInterpolator, locale));
				}
			}
		}
	}
	
	private <T, U> List<InterpolatedMessage> validateFormElement(FormElement<T> element, T elementValue) {
		List<InterpolatedMessage> messages = new ArrayList<InterpolatedMessage>();
		for (net.formio.validation.Validator<T> validator : element.getValidators()) {
			messages.addAll(validator.validate(new ValidationContext<T>(element.getName(), elementValue)));
		}
		return messages;
	}
	
	private ConstraintViolationMessage createConstraintViolationMessage(
		InterpolatedMessage message, MessageInterpolator msgInterpolator, Locale locale) {
		return new ConstraintViolationMessage(message.getSeverity(), 
			interpolateMessage(msgInterpolator, message, locale),
			ValidationUtils.removeBraces(message.getMessageKey()),
			message.getMessageParameters());
	}
	
	private String interpolateMessage(MessageInterpolator msgInterpolator, InterpolatedMessage msg, Locale locale) {
		if (msg.getMessageText() != null) {
			return msg.getMessageText(); //Message already contains localized text 
		}
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
		String pathSep,
		Locale locale) {
		Map<String, List<ConstraintViolationMessage>> fieldMessages = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		List<ConstraintViolationMessage> globalMessages = new ArrayList<ConstraintViolationMessage>();
		
		// processing parse errors and request processing errors and other custom errors (in addition to bean validation API violations)
		processInterpolatedMessages(msgInterpolator, customMessages, propPrefix, pathSep, fieldMessages, globalMessages, locale);
		
		for (ConstraintViolation<T> v : violations) {
			// Needed data should be taken from javax.ConstraintViolation, 
			// ConstraintViolationMessage should be javax.validation API independent
			String formElementName = constructFormElementName(propPrefix, v, pathSep);
			ConstraintViolationMessage msg = new ConstraintViolationMessage(v);
			if (formElementName.length() == 0 || !formElementName.contains(pathSep)) {
				globalMessages.add(msg);
			} else {
				appendFieldMsg(fieldMessages, formElementName, msg);
			}
		}
		return new ValidationResult(fieldMessages, globalMessages);
	}

	private <T> String constructFormElementName(String propPrefix, ConstraintViolation<T> v, String pathSep) {
		Path path = v.getPropertyPath();
		StringBuilder nodePath = new StringBuilder();
		if (propPrefix != null) {
			nodePath.append(propPrefix);
		}
		for (Node node : path) {
			if (node.getName() != null) {
				if (nodePath.length() > 0) {
					nodePath.append(pathSep);
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
	
	private String pathPrefixedName(String pathPrefix, String name, String pathSep) {
		if (name == null) return null;
		if (pathPrefix == null || pathPrefix.isEmpty()) return name;
		return pathPrefix + pathSep + name;
	}
	
	private Set<String> gatherPropertyNames(List<FormElement<?>> elements) {
		Set<String> propertyNames = new LinkedHashSet<String>();
		for (FormElement<?> el : elements) {
			propertyNames.add(el.getPropertyName());
		}
		return propertyNames;
	}
	
	private static final ConstraintViolationComparator constraintViolationComparator = new ConstraintViolationComparator();
}
