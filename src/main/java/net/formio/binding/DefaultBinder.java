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
package net.formio.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.formio.Forms;
import net.formio.binding.collection.BasicCollectionBuilders;
import net.formio.binding.collection.CollectionBuilders;
import net.formio.binding.collection.CollectionSpec;
import net.formio.binding.collection.ItemsOrder;
import net.formio.format.Location;
import net.formio.format.BasicFormatters;
import net.formio.format.Formatter;
import net.formio.format.Formatters;
import net.formio.format.StringParseException;
import net.formio.upload.UploadedFile;

/**
 * Binds given values to new/existing instance of class.
 * 
 * @author Radek Beran
 */
public class DefaultBinder implements Binder {
	
	private final Formatters formatters;
	private final ArgumentNameResolver argNameResolver;
	private final CollectionBuilders collectionBuilders;
	private final PropertyMethodRegex setterRegex;
	
	/**
	 * Default regular expression for matching name of setter of a property and property name within it.
	 */
	public static final PropertyMethodRegex DEFAULT_SETTER_REGEX = new PropertyMethodRegex("set([_a-zA-Z][_a-zA-Z0-9]*)", 1);
	
	public DefaultBinder(
		Formatters formatters, 
		CollectionBuilders collectionBuilders, 
		ArgumentNameResolver argNameResolver, 
		PropertyMethodRegex setterRegex) {
		if (formatters == null) throw new IllegalArgumentException("formatters cannot be null");
		if (argNameResolver == null) throw new IllegalArgumentException("argNameResolver cannot be null");
		if (collectionBuilders == null) throw new IllegalArgumentException("collectionBuilders cannot be null");
		if (setterRegex == null) throw new IllegalArgumentException("setterRegex cannot be null");
		this.formatters = formatters;
		this.argNameResolver = argNameResolver;
		this.collectionBuilders = collectionBuilders;
		this.setterRegex = setterRegex;
	}
	
	public DefaultBinder(Formatters formatters, CollectionBuilders collBuilders, ArgumentNameResolver argNameResolver) {
		this(formatters, collBuilders, argNameResolver, DEFAULT_SETTER_REGEX);
	}
	
	public DefaultBinder(Formatters formatters, CollectionBuilders collBuilders) {
		this(formatters, collBuilders, new AnnotationArgumentNameResolver());
	}
	
	public DefaultBinder(Formatters formatters) {
		this(formatters, new BasicCollectionBuilders());
	}
	
	public DefaultBinder() {
		this(new BasicFormatters());
	}

	/**
	 * Returns new instance of given class created via binding values to
	 * constructor arguments annotated by {@link ArgumentName} and binding rest
	 * of values to setters.
	 * 
	 * @param objClass class of new instance
	 * @param instantiator instantiator of class T, {@code null} for default instantiator
	 * @param values values to bind; specify only values that must be bound
	 * @return new instance of given class filled with bound values
	 * @throws BindingException if construction of new instance or binding failed or some
	 * value cannot be bound to created instance (parameter name with the value is present but 
	 * there is no way to bind it to the instance) - better to have reliable strict binding!
	 */
	@Override
	public <T> BoundData<T> bindToNewInstance(Class<T> objClass, Instantiator instantiator, Map<String, BoundValuesInfo> values) {
		Map<String, List<ParseError>> propertyBindErrors = new LinkedHashMap<String, List<ParseError>>();
		if (instantiator == null) throw new IllegalArgumentException("instantiator cannot be null");
		if (values == null) throw new IllegalArgumentException("values cannot be null");
		Set<String> notBoundYetParamNames = values.keySet();
		
		ConstructionDescription cd = instantiator.getDescription(objClass, this.argNameResolver);
		if (cd == null) throw new IllegalStateException("No usable construction method of " + objClass.getName() + " was found."); 
		// Preparing arguments of construction method
		Object[] args = buildConstructionArguments(cd, values, propertyBindErrors);
		notBoundYetParamNames.removeAll(cd.getArgNames());
		T obj = instantiator.instantiate(objClass, cd, args);
		
		// Using setters for the rest of values
		for (String paramName : notBoundYetParamNames) {
			BoundValuesInfo valueInfo = values.get(paramName);
			if (valueInfo == null) throw new BindingException("Property '" + paramName + 
				" could not be bound. Value to bind was not found. " + 
				"The appropriate field was probably not declared.");
			boolean clientProvidedInstance = instantiator instanceof InstanceHoldingInstantiator;
			propertyBindErrors.putAll(updatePropertyValue(objClass, obj, paramName, valueInfo, clientProvidedInstance));
			// notBoundYetParamNames cannot be reduced here in cycle (ConcurrentModificationException)
		}
		return new BoundData<T>(obj, propertyBindErrors);
	}
	
	protected CollectionBuilders getCollectionBuilders() {
		return collectionBuilders;
	}
	
	protected Formatters getFormatters() {
		return formatters;
	}
	
	protected PropertyMethodRegex getSetterRegex() {
		return setterRegex;
	}
	
	protected ArgumentNameResolver getArgNameResolver() {
		return argNameResolver;
	}
	
	protected boolean isPropertySetter(Method method, String propertyName) {
		return setterRegex.matchesPropertyMethod(method.getName(), propertyName) && method.getParameterTypes().length == 1;
	}
	
	protected Object[] buildConstructionArguments(ConstructionDescription cd,
		Map<String, BoundValuesInfo> values,
		Map<String, List<ParseError>> propertyBindErrors) {
		List<String> argNames = cd.getArgNames();
		Object[] args = new Object[argNames.size()];
		for (int i = 0; i < argNames.size(); i++) {
			String argName = argNames.get(i);
			BoundValuesInfo valueInfo = values.get(argName);
			if (valueInfo == null) throw new BindingException("Property '" + argName + 
				"' required by the constructor of form data object could not be bound. Value to bind was not found. " + 
				"The appropriate field was probably not declared.");
			ParsedValue parsedValue = convertToValue(cd.getConstructedClass(), argName, valueInfo, cd.getArgTypes()[i], cd.getGenericParamTypes()[i]);
			args[i] = parsedValue.getValue();
			if (!parsedValue.isSuccessfullyParsed()) {
				addParseError(propertyBindErrors, argName, parsedValue.getParseErrors());
			}
		}
		return args;
	}
	
	/**
	 * Updates given property of given object to given value.
	 * @param parentClass name of class for which the value of its property is converted
	 * @param obj object with property
	 * @param propertyName name of property (without set, get or is - according to JavaBeans convention)
	 * @param propertyValueInfo value to set for the property
	 * @param clientProvidedInstance flag that client provided own instance that should be filled
	 * @return binding errors
	 * @throws BindingException if setter was not found or some other error occurred
	 */
	protected Map<String, List<ParseError>> updatePropertyValue(
		Class<?> parentClass,
		Object obj, 
		String propertyName,
		BoundValuesInfo propertyValueInfo, 
		boolean clientProvidedInstance) {
		if (propertyName == null || propertyName.isEmpty()) {
			throw new IllegalArgumentException("Name of property is missing.");
		}
		final Map<String, List<ParseError>> propertyBindErrors = new LinkedHashMap<String, List<ParseError>>();
		boolean propertySet = false;
		String setterName = null;
		try {
			Method[] objMethods = obj.getClass().getMethods();
			for (Method objMethod : objMethods) {
				if (!isPropertySetter(objMethod, propertyName)) {
					continue;
				}
				setterName = objMethod.getName();
				Class<?> methodParamClass = objMethod.getParameterTypes()[0];
				Type genericParamType = objMethod.getGenericParameterTypes()[0];
				ParsedValue parsedValue = convertToValue(parentClass, propertyName, propertyValueInfo, methodParamClass, genericParamType);
				Object propertyValue = parsedValue.getValue();
				if (!parsedValue.isSuccessfullyParsed()) {
					addParseError(propertyBindErrors, propertyName, parsedValue.getParseErrors());
				}
				if (propertyValue == null || canBeImplicitlyConverted(propertyValue, methodParamClass)) {
					if (PrimitiveType.isPrimitiveType(methodParamClass) && propertyValue == null) {
						// Using initial value for primitive type
						propertyValue = PrimitiveType.byPrimitiveClass(methodParamClass).getInitialValue(); 
					}
					objMethod.invoke(obj, propertyValue);
					propertySet = true;
					break;
				}
			}
		} catch (Exception ex) {
			throw new BindingException("Invoking setter " + setterName
				+ " of class " + obj.getClass().getSimpleName()
				+ " failed: " + ex.getMessage(), ex);
		}
		// client-provided instance need not to use all the values from the form,
		// because it can have constructor arguments already set to some different values
		if (!clientProvidedInstance && !Forms.AUTH_TOKEN_FIELD_NAME.equals(propertyName) && !propertySet) {
			throw new BindingException("Setter for property " + propertyName
					+ " was not found in " + obj.getClass().getSimpleName());
		}
		return propertyBindErrors;
	}

	/**
	 * Converts form field string value(s) to one typed value (single value or collection/array of values)
	 * with possible parse errors (when a string value cannot be converted properly).
	 * @param parentClass name of class for which the value of its property is converted
	 * @param propertyName
	 * @param valueInfo
	 * @param targetClass
	 * @param genericParamType
	 * @return
	 */
	protected ParsedValue convertToValue(
		Class<?> parentClass,
		String propertyName, 
		BoundValuesInfo valueInfo, 
		Class<?> targetClass, 
		Type genericParamType) {
		List<ParseError> parseErrors = new ArrayList<ParseError>();
		ParsedValue parsedValue = null;
		// TODO: Configurable prefered items order (collection type), linear as default
		CollectionSpec<?> collSpec = CollectionSpec.getInstance(targetClass, ItemsOrder.LINEAR);
		if (getCollectionBuilders().canHandle(collSpec)) {
			// binding to collection
			Object resultValue = null;
			if (valueInfo.getValues() != null && valueInfo.getValues().length == 1 && valueInfo.getValues()[0] instanceof List) {
				// already one list value (from list mapping)
				resultValue = valueInfo.getValues()[0];
			} else {
				resultValue = convertFormValueToCollection(
					propertyName, 
					valueInfo, 
					collSpec,
					getCollectionBuilders().getItemClass(parentClass, propertyName, genericParamType),
					parseErrors);
			}
			parsedValue = new ParsedValue(resultValue, parseErrors);
		} else {
			if (valueInfo == null || valueInfo.getValues() == null || valueInfo.getValues().length == 0) {
				parsedValue = new ParsedValue(null, new ArrayList<ParseError>());
			} else {
				Object formValue = valueInfo.getValues()[0];
				Object resultValue = convertOneFormValue(propertyName, formValue, 
					targetClass, valueInfo.getFormatter(), valueInfo.getPattern(), valueInfo.getLocation(), parseErrors);
				parsedValue = new ParsedValue(resultValue, parseErrors);
			}
		}
		return parsedValue;
	}
	
	/**
	 * Parses the value from string with given user-defined formatter of with other suitable formatter
	 * found in inner formatters.
	 * @param strValue
	 * @param targetClass
	 * @param formatter
	 * @param pattern
	 * @param loc
	 * @return
	 */
	protected Object parseFromString(String strValue, Class<?> targetClass,
		Formatter<Object> formatter, String pattern, Location loc) {
		Object resultValue;
		if (formatter != null) {
			// user defined formatter
			resultValue = formatter.parseFromString(strValue, (Class<Object>)targetClass, pattern, loc);
		} else {
			resultValue = getFormatters().parseFromString(strValue, targetClass, pattern, loc);
		}
		return resultValue;
	}
	
	protected boolean canBeImplicitlyConverted(Object fromValue, Class<?> toClass) {
		// Convertible from wrapper class (fromClass) to primitive class (toClass)
		return PrimitiveType.byClasses(toClass, fromValue.getClass()) != null
			|| toClass.isAssignableFrom(fromValue.getClass())
			|| toClass.isInstance(fromValue);
	}
	
	protected void checkInvalidStringValueForUploadedFile(String propertyName, Class<?> targetClass) {
		if (UploadedFile.class.isAssignableFrom(targetClass)) {
			throw new IllegalStateException("Invalid String value for property '" + propertyName + "' of type " + 
				UploadedFile.class.getSimpleName() + ". Did you forget to use POST method for the form with an uploaded file?");
		}
	}

	/**
	 * Converts the value from request parameters to target collection type.
	 * @param propertyName
	 * @param valueInfo
	 * @param collSpec
	 * @param itemClass
	 * @param parseErrors
	 * @return
	 */
	protected <C, I> C convertFormValueToCollection(
		String propertyName,
		BoundValuesInfo valueInfo, 
		CollectionSpec<C> collSpec,
		Class<I> itemClass,
		List<ParseError> parseErrors) {
		// we will return empty collection if values are empty
		List<I> resultItems = new ArrayList<I>();
		if (valueInfo != null && valueInfo.getValues() != null) {
			for (Object formValue : valueInfo.getValues()) {
				Object value = convertOneFormValue(propertyName, formValue, 
					itemClass, valueInfo.getFormatter(), valueInfo.getPattern(), valueInfo.getLocation(), parseErrors);
				resultItems.add((I)value);
			}
		}
		return getCollectionBuilders().buildCollection(collSpec, itemClass, resultItems);
	}

	/**
	 * Converts the value from request parameters (form value) to value of given target type. 
	 * @param propertyName
	 * @param formValue
	 * @param parseErrors
	 * @param targetClass
	 * @param formatter
	 * @param pattern
	 * @param loc
	 * @return
	 */
	protected Object convertOneFormValue(
		String propertyName, 
		Object formValue, 
		Class<?> targetClass, 
		Formatter<Object> formatter, 
		String pattern, 
		Location loc,
		List<ParseError> parseErrors) {
		
		Object resultValue = null;
		if (formValue instanceof String && !canBeImplicitlyConverted(formValue, targetClass)) {
			// We must parse the value from string (using formatters)
			checkInvalidStringValueForUploadedFile(propertyName, targetClass);
			
			// Convert from the String to targetClass
			String strValue = (String)formValue;
			try {
				// Throws StringParseException also when parsing empty String to some primitive type
				if (strValue.isEmpty() && !String.class.equals(targetClass)) {
					// resultValue remains null
					// for e.g. transformation of "" to Date will return null
				} else {
					resultValue = parseFromString(strValue, targetClass, formatter, pattern, loc);
				}
			} catch (StringParseException ex) {
				resultValue = null;
				parseErrors.add(new ParseError(propertyName, targetClass, strValue));
			}
		} else {
			// if form value is instanceof UploadedFile, it is automatically
			// set to property of compatible type
			// also list of values from list mapping is directly returned
			resultValue = formValue;
		}
		return resultValue;
	}
	
	private void addParseError(Map<String, List<ParseError>> parseErrors, String propName, List<ParseError> errsToAdd) {
		List<ParseError> errors = parseErrors.get(propName);
		if (errors == null) {
			errors = new ArrayList<ParseError>();
		}
		errors.addAll(errsToAdd);
		parseErrors.put(propName, errors);
	}
}
