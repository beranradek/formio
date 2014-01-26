package org.twinstone.formio.binding;

import java.util.Map;
import java.util.Set;

/**
 * Extracts values from bean's properties.
 * @author Radek Beran
 */
public interface BeanExtractor {

	/**
	 * Extracts values from given bean.
	 * @param bean extracted bean
	 * @param allowedProperties names of properties that should be extracted (whitelist)
	 * @return values by property names
	 */
	Map<String, Object> extractBean(Object bean, Set<String> allowedProperties);
	
	/**
	 * Returns names and types of properties of given class.
	 * @param beanClass
	 * @param allowedProperties names of properties that should be extracted (whitelist)
	 * @return
	 */
	Map<String, Class<?>> getPropertyClasses(Class<?> beanClass, Set<String> allowedProperties);
}
