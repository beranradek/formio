package org.twinstone.formio;

import java.util.Map;

import org.twinstone.formio.validation.ValidationReport;

/**
 * A form for editing object (form data) of type T.
 * All implementations are immutable, so the instance of this form definition
 * can be freely shared and cached.
 * 
 * @author Radek Beran
 */
public interface FormMapping<T> {
	
	/**
	 * Returns path to this mapping - names separated by dot: form mapping name, nested form mapping name and so on
	 * up to this mapping name (including).
	 * @return
	 */
	String getPath();
	
	/**
	 * Class of edited object (form data).
	 * @return
	 */
	Class<T> getDataClass();
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. 
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData);
	
	/**
	 * Loads filled and validated data from the form.
	 * @param paramsProvider provider of request parameters
	 * @return
	 */
	FormData<T> loadData(ParamsProvider paramsProvider);
	
	/**
	 * Returns report with validation messages, {@code null} if form data was not validated yet.
	 * @return
	 */
	ValidationReport getValidationReport();
	
	/**
	 * Returns form fields. Can be used in template to construct markup of form fields.
	 * @return
	 */
	Map<String, FormField> getFields();
	
	/**
	 * Returns nested mapping for nested complex objects.
	 * @return
	 */
	Map<String, FormMapping<?>> getNestedMappings();
}
