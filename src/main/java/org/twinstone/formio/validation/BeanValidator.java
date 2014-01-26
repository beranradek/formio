package org.twinstone.formio.validation;

import java.util.List;

import org.twinstone.formio.binding.ParseError;
import org.twinstone.formio.upload.RequestProcessingError;


/**
 * Bean validator.
 * @author Radek Beran
 */
public interface BeanValidator {

	/**
	 * Validates object and returns report with validation errors.
	 * @param <T> 
	 * @param inst filled object
	 * @param propPrefix path to validated object (properties of validated object should be prefixed by this path
	 * when constructing resulting messages)
	 * @param requestFailures request processing errors that should be translated to field or global messages
	 * @param bindingErrors parse errors that should be translated to field messages
	 * @param groups possible list of groups that should be validated
	 * @return validation report with validation errors
	 */
	<T> ValidationReport validate(
		T inst, 
		String propPrefix, 
		List<RequestProcessingError> requestFailures, 
		List<ParseError> bindingErrors, 
		Class<?>... groups);
}
