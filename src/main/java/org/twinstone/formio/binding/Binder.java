package org.twinstone.formio.binding;

import java.util.Map;

/**
 * Binds given values to new/existing instance of class.
 * @author Radek Beran
 */
public interface Binder {
	
	/**
	 * Binds given values to new instance of class.
	 * @param objClass
	 * @param values
	 * @return
	 */
	<T> FilledData<T> bindToNewInstance(Class<T> objClass, Map<String, BoundValuesInfo> values);
}
