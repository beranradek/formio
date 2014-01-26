package org.twinstone.formio;

import java.util.List;

/**
 * Form field representation. All implementations are immutable.
 * @author Radek Beran
 */
public interface FormField {

	/**
	 * Name of field.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Single/first value of this field.
	 * 
	 * @return
	 */
	String getValue();
	
	/**
	 * Values of this field.
	 * 
	 * @return
	 */
	List<Object> getValues();

	/**
	 * Formatting pattern.
	 * 
	 * @return
	 */
	String getPattern();

}
