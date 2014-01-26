package org.twinstone.formio.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic {@link InterpolatedMessage} that has no parameters.
 * @author Radek Beran
 */
public class DefaultInterpolatedMessage implements InterpolatedMessage {
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
	public Map<String, Object> getMessageParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		return params;
	}
}
