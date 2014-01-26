package org.twinstone.formio.validation;

import java.util.Map;

import javax.validation.MessageInterpolator;

/**
 * Message that can be localized using {@link MessageInterpolator}.
 * @author Radek Beran
 */
public interface InterpolatedMessage {
	/**
	 * Message key for translation file.
	 * @return
	 */
	String getMessageKey();
	
	/**
	 * Message parameters for translation file.
	 * @return
	 */
	Map<String, Object> getMessageParameters();
}
