package org.twinstone.formio.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.twinstone.formio.binding.collection.BasicCollectionBuilders;
import org.twinstone.formio.binding.collection.CollectionBuilders;
import org.twinstone.formio.binding.collection.CollectionSpec;
import org.twinstone.formio.binding.collection.ItemsOrder;
import org.twinstone.formio.text.BasicFormatters;
import org.twinstone.formio.text.StringParseException;
import org.twinstone.formio.text.Formatters;

/**
 * Binds given values to new/existing instance of class.
 * 
 * @author Radek Beran
 */
public class DefaultBinder implements Binder {
	
	private final Formatters stringParser;
	private final ArgumentNameResolver argNameResolver;
	private final CollectionBuilders collBuilders;
	private final PropertyMethodRegex setterRegex;
	
	/**
	 * Default regular expression for matching name of setter of a property and property name within it.
	 */
	public static final PropertyMethodRegex defaultSetterRegex = new PropertyMethodRegex("set([_a-zA-Z][_a-zA-Z0-9]*)", 1);
	
	public DefaultBinder(
		Formatters stringParser, 
		CollectionBuilders collBuilders, 
		ArgumentNameResolver argNameResolver, 
		PropertyMethodRegex setterRegex) {
		if (stringParser == null) throw new IllegalArgumentException("stringParser cannot be null");
		if (argNameResolver == null) throw new IllegalArgumentException("argNameResolver cannot be null");
		if (collBuilders == null) throw new IllegalArgumentException("collBuilders cannot be null");
		if (setterRegex == null) throw new IllegalArgumentException("setterRegex cannot be null");
		this.stringParser = stringParser;
		this.argNameResolver = argNameResolver;
		this.collBuilders = collBuilders;
		this.setterRegex = setterRegex;
	}
	
	public DefaultBinder(Formatters stringParser, CollectionBuilders collBuilders, ArgumentNameResolver argNameResolver) {
		this(stringParser, collBuilders, argNameResolver, defaultSetterRegex);
	}
	
	public DefaultBinder(Formatters stringParser, CollectionBuilders collBuilders) {
		this(stringParser, collBuilders, new DefaultAnnotationArgumentNameResolver());
	}
	
	public DefaultBinder(Formatters stringParser) {
		this(stringParser, new BasicCollectionBuilders());
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
	 * @param values values to bind; specify only values that must be bound
	 * @return new instance of given class filled with bound values
	 * @throws BindingException if construction of new instance or binding failed or some
	 * value cannot be bound to created instance (parameter name with the value is present but 
	 * there is no way to bind it to the instance) - better to have reliable strict binding!
	 */
	@Override
	public <T> FilledData<T> bindToNewInstance(Class<T> objClass, Map<String, BoundValuesInfo> values) {
		Map<String, List<ParseError>> propertyBindErrors = new HashMap<String, List<ParseError>>();
		if (values == null) throw new IllegalArgumentException("values cannot be null");
		Set<String> notBoundYetParamNames = values.keySet();
		BoundConstructor<T> c = selectConstructorWithAnnotatedArguments(objClass);
		T obj = null;
		if (c != null) {
			// Binding values to a constructor with some parameters...
			List<String> argNames = c.getArgNames();
			Class<?>[] argTypes = c.getArgTypes();
			Type[] genericParamTypes = c.getConstructor().getGenericParameterTypes();
			Object[] args = new Object[argNames.size()];
			for (int i = 0; i < argNames.size(); i++) {
				BoundValuesInfo valueInfo = values.get(argNames.get(i));
				if (valueInfo == null) throw new BindingException("Property '" + argNames.get(i) + 
					"' required by the constructor of form data object could not be bound. Value to bind was not found. " + 
					"The appropriate field was probably not declared.");
				ParsedValue parsedValue = convertToValue(argNames.get(i), valueInfo, argTypes[i], genericParamTypes[i]);
				args[i] = parsedValue.getValue();
				if (!parsedValue.isSuccessfullyParsed()) {
					addParseError(propertyBindErrors, argNames.get(i), parsedValue.getParseErrors());
				}
			}
			obj = ReflectionUtils.requireNewInstance(c.getConstructor(), args);
			notBoundYetParamNames.removeAll(argNames);
		} else {
			// Using default constructor without parameters for construction
			obj = requireNewInstance(objClass);
		}
		// Using setters for the rest of values
		for (String paramName : notBoundYetParamNames) {
			BoundValuesInfo valueInfo = values.get(paramName);
			if (valueInfo == null) throw new BindingException("Property '" + paramName + 
				" could not be bound. Value to bind was not found. " + 
				"The appropriate field was probably not declared.");
			updatePropertyValue(obj, paramName, valueInfo, propertyBindErrors);
			// notBoundYetParamNames cannot be reduced here in cycle (ConcurrentModificationException)
		}
		return new FilledData<T>(obj, propertyBindErrors);
	}
	
	protected boolean isPropertySetter(Method method, String propertyName) {
		return setterRegex.matchesPropertyMethod(method.getName(), propertyName) && method.getParameterTypes().length == 1;
	}

	/**
	 * Converts form field values to one value (single value of collection/array of values)
	 * with possible parse errors (when a string value cannot be converted properly).
	 * @param propertyName
	 * @param valueInfo
	 * @param targetClass
	 * @param genericParamType
	 * @return
	 */
	private ParsedValue convertToValue(String propertyName, BoundValuesInfo valueInfo, Class<?> targetClass, Type genericParamType) {
		List<ParseError> parseErrors = new ArrayList<ParseError>();
		ParsedValue parsedValue = null;
		// TODO: Configurable prefered items order (collection type), linear as default
		CollectionSpec<?> collSpec = CollectionSpec.getInstance(targetClass, ItemsOrder.LINEAR);
		if (collBuilders.canHandle(collSpec)) {
			// binding to collection
			// we will return empty collection if values are empty
			Class<?> itemClass = itemTypeFromGenericCollType(genericParamType);
			List resultItems = new ArrayList<Object>();
			if (valueInfo != null && valueInfo.getValues() != null) {
				for (Object formValue : valueInfo.getValues()) {
					resultItems.add(convertOneFormValue(propertyName, formValue, parseErrors, 
						itemClass, valueInfo.getPattern(), valueInfo.getLocale()));
				}
			}
			Object resultValue = collBuilders.buildCollection(collSpec, itemClass, resultItems);
			parsedValue = new ParsedValue(resultValue, parseErrors);
		} else {
			if (valueInfo == null || valueInfo.getValues() == null || valueInfo.getValues().length == 0) {
				parsedValue = new ParsedValue(null, new ArrayList<ParseError>());
			} else {
				Object formValue = valueInfo.getValues()[0];
				Object resultValue = convertOneFormValue(propertyName, formValue, parseErrors, 
					targetClass, valueInfo.getPattern(), valueInfo.getLocale());
				parsedValue = new ParsedValue(resultValue, parseErrors);
			}
		}
		return parsedValue;
	}
	
	private Class<?> itemTypeFromGenericCollType(Type genericParamType) {
		Type ret = null;
		Type[] typeParams = ReflectionUtils.getTypeParameters(genericParamType);
		if (typeParams != null && typeParams.length > 0) {
			ret = typeParams[0];
		}
		if (ret == null) {
			if (genericParamType.equals(boolean[].class)) ret = boolean.class;
			else if (genericParamType.equals(byte[].class)) ret = byte.class;
			else if (genericParamType.equals(short[].class)) ret = short.class;
			else if (genericParamType.equals(int[].class)) ret = int.class;
			else if (genericParamType.equals(long[].class)) ret = long.class;
			else if (genericParamType.equals(float[].class)) ret = float.class;
			else if (genericParamType.equals(double[].class)) ret = double.class;
			else if (genericParamType.equals(char[].class)) ret = char.class;
		}
		return (Class<?>)ret;
	}

	private Object convertOneFormValue(String propertyName, Object formValue, List<ParseError> parseErrors, Class<?> targetClass, String pattern, Locale locale) {
		Object resultValue = null;
		if (!canBeImplicitlyConverted(formValue.getClass(), targetClass) 
			&& formValue instanceof String && !targetClass.isInstance(formValue)) {
			// Convert from the String to targetClass
			String strValue = (String)formValue;
			try {
				// Throws StringParseException also when parsing empty String to some primitive type
				resultValue = stringParser.parseFromString(targetClass, strValue, pattern, locale);
			} catch (StringParseException ex) {
				resultValue = null;
				parseErrors.add(new ParseError(propertyName, targetClass, strValue));
			}
		} else {
			// if value is instanceof UploadedFile, it is automatically
			// set to property of compatible type
			resultValue = formValue;
		}
		return resultValue;
	}

	private <T> T requireNewInstance(Class<T> objClass) {
		T obj = null;
		try {
			obj = objClass.newInstance();
		} catch (IllegalAccessException ex) {
			throw new BindingException(
					"Default (nullary) constructor for class "
							+ objClass.getSimpleName()
							+ " not found or not accessible.", ex);
		} catch (InstantiationException ex) {
			throw new BindingException(
					"Default (nullary) constructor for class "
							+ objClass.getSimpleName()
							+ " not found or class is not instantiable with default constructor" 
							+ " or another constructor (with annotated arguments - all arguments must"
							+ " be annotated if constructor should be used).", ex);
		}
		return obj;
	}

	/**
	 * Returns constructor with all the arguments annotated with
	 * {@link ArgumentName}, or {@code null}.
	 * 
	 * @param objClass
	 * @return
	 */
	private <T> BoundConstructor<T> selectConstructorWithAnnotatedArguments(Class<T> objClass) {
		for (Constructor<?> c : objClass.getConstructors()) {
			Class<?>[] argTypes = c.getParameterTypes();
			Annotation[][] ann = c.getParameterAnnotations();
			Map<String, Integer> ordByArgName = new HashMap<String, Integer>();
			List<String> argNames = new ArrayList<String>();

			// For each constructor parameter
			for (int i = 0; i < argTypes.length; i++) {
				String argName = argNameResolver.getArgumentName(argTypes[i], ann[i]);
				if (argName == null) {
					// Constructor contains argument
					// that cannot be bound and used in construction
					ordByArgName.clear();
					argNames.clear();
					break;
				}
				if (ordByArgName.containsKey(argName))
					throw new IllegalStateException("Duplicit argument name\""
							+ argName + "\" of constructor in "
							+ objClass.getSimpleName());
				ordByArgName.put(argName, Integer.valueOf(i));
				argNames.add(argName);
			}
			if (!argNames.isEmpty())
				return new BoundConstructor<T>((Constructor<T>)c, argNames, argTypes);
		}
		return null;
	}

	/**
	 * Updates given property of given object to given value.
	 * @param obj object with property
	 * @param propertyName name of property (without set, get or is - according to JavaBeans convention)
	 * @param propertyValue value to set for the property
	 * @param propertyBindErrors bind errors that can be filled
	 * @throws BindingException if setter was not found or some other error occurred
	 */
	private void updatePropertyValue(Object obj, String propertyName,
		BoundValuesInfo propertyValueInfo, Map<String, List<ParseError>> propertyBindErrors) {
		if (propertyName == null || propertyName.isEmpty()) {
			throw new IllegalArgumentException("Name of property is missing.");
		}
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
				ParsedValue parsedValue = convertToValue(propertyName, propertyValueInfo, methodParamClass, genericParamType);
				Object propertyValue = parsedValue.getValue();
				if (!parsedValue.isSuccessfullyParsed()) {
					addParseError(propertyBindErrors, propertyName, parsedValue.getParseErrors());
				}
				if (propertyValue == null || methodParamClass.isAssignableFrom(propertyValue.getClass()) 
					|| canBeImplicitlyConverted(propertyValue.getClass(), methodParamClass)) {
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
		if (!propertySet) {
			throw new BindingException("Setter for property " + propertyName
					+ " was not found in " + obj.getClass().getSimpleName() + ".");
		}
	}

	private boolean canBeImplicitlyConverted(
			Class<? extends Object> fromClass, Class<?> toClass) {
		// Convertible from wrapper class (fromClass) to primitive class (toClass)
		return PrimitiveType.byClasses(toClass, fromClass) != null;
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
