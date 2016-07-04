package net.formio.validation.constraints;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.formio.validation.constraints.NotEmptyValidation;

/**
 * Constraint validating any non-empty field of list of fields
 * 
 * @author Petr Kalivoda
 *
 */
public class AnyNotEmptyValidator implements ConstraintValidator<AnyNotEmpty, Object> {

	private List<String> fieldNames;

	@Override
	public void initialize(AnyNotEmpty constraintAnnotation) {
		fieldNames = Arrays.asList(constraintAnnotation.fields());
	}

	@Override
	public boolean isValid(Object input, ConstraintValidatorContext context) {
		for (String fieldName : fieldNames) {
			try {
				Object value = getPropertyValue(input, fieldName);
				if (NotEmptyValidation.isNotEmpty(value)) {
					return true;
				}
				
			} catch (Exception ignore) {}
		}
		return false;
	}

	/**
	 * Returns value of property on given bean.
	 * 
	 * @param bean
	 * @param property
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IntrospectionException
	 */
	private Object getPropertyValue(Object bean, String property)
			throws InvocationTargetException, IllegalAccessException, IllegalArgumentException, IntrospectionException {
		return getReadMethod(bean.getClass(), property).invoke(bean);
	}

	/**
	 * Returns getter for appropriate bean class.
	 * 
	 * @param beanClass
	 * @param property
	 * @return
	 * @throws IntrospectionException
	 */
	private Method getReadMethod(Class<?> beanClass, String property) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			if (pd.getName().equals(property) && pd.getReadMethod() != null) {
				return pd.getReadMethod();
			}
		}

		throw new IllegalArgumentException(
				"No getter available for property '" + property + "' in '" + beanClass + "'.");
	}

}
