/*
 * Created on 8.12.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package org.twinstone.formio.validation;

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

import org.twinstone.formio.FormUtils;
import org.twinstone.formio.Forms;
import org.twinstone.formio.binding.ParseError;
import org.twinstone.formio.upload.MaxFileSizeExceededError;
import org.twinstone.formio.upload.RequestProcessingError;

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
	public <T> ValidationReport validate(T inst, 
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
	 * @param annotationParameters
	 * @param locale
	 * @return
	 */
	protected String interpolateMessage(MessageInterpolator msgInterpolator, String message, Map<String, Object> parameters, Locale locale) {
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
					interpolateMsg(msgInterpolator, error)));	
				fieldMessages.put(err.getFieldName(), msgs);
			} else if (error != null && isTopLevelMapping(propPrefix)) {
				// other request processing errors
				globalMessages.add(new ConstraintViolationMessage(Severity.ERROR, 
					interpolateMsg(msgInterpolator, error)));
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
			msgs.add(new ConstraintViolationMessage(Severity.ERROR, interpolateMsg(msgInterpolator, err)));
			fieldMessages.put(formPrefixedPropName, msgs);
		}
	}
	
	private String interpolateMsg(MessageInterpolator msgInterpolator, InterpolatedMessage msg) {
		return removeBraces(interpolateMessage(msgInterpolator, msg.getMessageKey(), msg.getMessageParameters(), this.locale));
	}
	
	private Set<ConstraintViolationMessage> getOrCreateFieldMessages(Map<String, Set<ConstraintViolationMessage>> fieldMsgs, String fieldName) {
		Set<ConstraintViolationMessage> msgs = fieldMsgs.get(fieldName);
		if (msgs == null) {
			msgs = new LinkedHashSet<ConstraintViolationMessage>();
		}
		return msgs;
	}
	
	private <T> ValidationReport buildReport(MessageInterpolator msgInterpolator, Set<ConstraintViolation<T>> violations, 
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
							nodePath.append('.');
						}
						nodePath.append(node.getName());
					}
				}
				String fieldName = FormUtils.removeTrailingBrackets(nodePath.toString());
				// Needed data should be taken from javax.ConstraintViolation, 
				// ConstraintViolationMessage should be javax.validation API independent
				ConstraintViolationMessage msg = new ConstraintViolationMessage(Severity.fromViolation(v), getMsgText(v));
				if (fieldName.length() == 0 || !fieldName.contains(Forms.PATH_SEP)) {
					// only prefix without property name
					globalMessages.add(msg);
				} else {
					appendFieldMsg(fieldMessages, fieldName, msg);
				}
			}
		}
		return new ValidationReport(fieldMessages, globalMessages);
	}

	private String getMsgText(ConstraintViolation<?> v) {
		String msgText = null;
		if (v.getMessage() != null && !v.getMessage().isEmpty()) {
			msgText = v.getMessage();
		} else {
			msgText = v.getMessageTemplate();
		}
		return removeBraces(msgText);
	}
	
	private String removeBraces(String msgTemplate) {
		if (msgTemplate == null) return null;
		if (msgTemplate.startsWith("{") && msgTemplate.endsWith("}")) {
			return msgTemplate.substring(1, msgTemplate.length() - 1);
		}
		return msgTemplate;
	}

	private boolean isTopLevelMapping(String propPrefix) {
		return propPrefix == null || !propPrefix.contains(Forms.PATH_SEP);
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
}
