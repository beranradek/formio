package org.twinstone.formio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.twinstone.formio.binding.BoundValuesInfo;
import org.twinstone.formio.binding.FilledData;
import org.twinstone.formio.upload.RequestProcessingError;
import org.twinstone.formio.upload.UploadedFile;
import org.twinstone.formio.validation.ConstraintViolationMessage;
import org.twinstone.formio.validation.ValidationReport;

/**
 * Default implementation of {@link FormMapping}. Immutable.
 * 
 * @author Radek Beran
 */
class BasicFormMapping<T> implements FormMapping<T> {

	private final String path;
	private final Class<T> objectClass;
	
	/** Mapping simple property names to fields. */
	private final Map<String, FormField> fields;
	/** Mapping simple property names to nested mappings. Property name is a part of full path of nested mapping. */
	private final Map<String, BasicFormMapping<?>> nestedMappings;
	private final ValidationReport validationReport;
	private final Config config;
	private final boolean userDefinedConfig;

	BasicFormMapping(BasicFormMappingBuilder<T> builder) {
		this.path = builder.path;
		this.objectClass = builder.objectClass;
		this.fields = Collections.unmodifiableMap(builder.fields);
		this.validationReport = builder.validationReport;
		this.config = builder.config;
		this.userDefinedConfig = builder.userDefinedConfig;
		if (builder.userDefinedConfig) {
			// propagate user defined config to nested mappings
			Map<String, BasicFormMapping<?>> newNestedMappings = new LinkedHashMap<String, BasicFormMapping<?>>();
			for (Map.Entry<String, BasicFormMapping<?>> e : builder.nestedMappings.entrySet()) {
				Config nestedMappingConfig = chooseConfigForNestedMapping(e.getValue(), builder.config);
				newNestedMappings.put(e.getKey(), e.getValue().withConfig(nestedMappingConfig));
			}
			this.nestedMappings = Collections.unmodifiableMap(newNestedMappings);
		} else {
			this.nestedMappings = Collections.unmodifiableMap(builder.nestedMappings);
		}
	}
	
	/**
	 * Returns copy with given path prefix prepended.
	 * @param src
	 * @param pathPrefix
	 * @param config
	 */
	BasicFormMapping(BasicFormMapping<T> src, String pathPrefix) {
		if (pathPrefix == null) throw new IllegalArgumentException("pathPrefix cannot be null");
		String newMappingPath = null;
		if (!pathPrefix.isEmpty()) {
			newMappingPath = pathPrefix + Forms.PATH_SEP + src.path;
		} else {
			newMappingPath = src.path;
		}
		this.path = newMappingPath;
		this.objectClass = src.objectClass;
		Map<String, FormField> newFields = new LinkedHashMap<String, FormField>();
		for (Map.Entry<String, FormField> e : src.fields.entrySet()) {
			// copy of field with given prefix prepended
			FormField field = new FormFieldImpl(e.getValue(), pathPrefix);
			if (!field.getName().startsWith(newMappingPath + Forms.PATH_SEP))
				throw new IllegalStateException("Field name '" + field.getName() + "' must start with prefix '" + newMappingPath + ".'");
			newFields.put(e.getKey(), field); // key must be a simple property name (it is not changing)
		}
		this.fields = newFields;
		Map<String, BasicFormMapping<?>> newNestedMappings = new LinkedHashMap<String, BasicFormMapping<?>>();
		for (Map.Entry<String, BasicFormMapping<?>> e : src.nestedMappings.entrySet()) {
			String mappingPath = e.getValue().getPath();
			String newPathPrefix = pathPrefix;
			if (!mappingPath.startsWith(this.path + Forms.PATH_SEP)) {
				newPathPrefix = pathPrefix + Forms.PATH_SEP + this.path;
			}
			newNestedMappings.put(e.getKey(), e.getValue().withPathPrefix(newPathPrefix));
		}
		this.nestedMappings = newNestedMappings;
		this.validationReport = src.validationReport;
		this.config = src.config;
		this.userDefinedConfig = src.userDefinedConfig;
	}
	
	BasicFormMapping(BasicFormMapping<T> src, Config config) {
		if (config == null) throw new IllegalArgumentException("config cannot be null");
		this.path = src.path;
		this.objectClass = src.objectClass;
		this.fields = src.fields;
		this.validationReport = src.validationReport;
		this.config = config;
		this.userDefinedConfig = true;
		Map<String, BasicFormMapping<?>> newNestedMappings = new LinkedHashMap<String, BasicFormMapping<?>>();
		for (Map.Entry<String, BasicFormMapping<?>> e : src.nestedMappings.entrySet()) {
			Config nestedMappingConfig = chooseConfigForNestedMapping(e.getValue(), config);
			newNestedMappings.put(e.getKey(), e.getValue().withConfig(nestedMappingConfig));
		}
		this.nestedMappings = Collections.unmodifiableMap(newNestedMappings);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Class<T> getDataClass() {
		return objectClass;
	}

	@Override
	public Map<String, FormField> getFields() {
		return fields;
	}

	@Override
	public ValidationReport getValidationReport() {
		return validationReport;
	}
	
	@Override
	public Map<String, FormMapping<?>> getNestedMappings() {
		Map<String, FormMapping<?>> mappingsMap = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, BasicFormMapping<?>> entry : nestedMappings.entrySet()) {
			mappingsMap.put(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(mappingsMap);
	}
	
	@Override
	public FormMapping<T> fill(FormData<T> editedObj) {
		return fillInternal(editedObj).build(this.config);
	}

	@Override
	public FormData<T> loadData(ParamsProvider paramsProvider) {
		if (paramsProvider == null) throw new IllegalArgumentException("paramsProvider cannot be null");
		final RequestProcessingError error = paramsProvider.getRequestError();
		Map<String, BoundValuesInfo> values = prepareBoundValues(paramsProvider);
		
		Map<String, FormData<?>> nestedFormData = loadDataForMappings(nestedMappings, paramsProvider);
		for (Map.Entry<String, FormData<?>> e : nestedFormData.entrySet()) {
			values.put(e.getKey(), BoundValuesInfo.getInstance(new Object[] { e.getValue().getData() } , null, config.getLocale()));
		}
		
		FilledData<T> filledObject = config.getBinder().bindToNewInstance(objectClass, values);
		List<RequestProcessingError> requestFailures = new ArrayList<RequestProcessingError>();
		requestFailures.add(error);
		ValidationReport validationRep = config.getBeanValidator().validate(
			filledObject.getData(), 
			getPath(), 
			requestFailures, 
			flatten(filledObject.getPropertyBindErrors().values()));
		Map<String, Set<ConstraintViolationMessage>> fieldMsgs = cloneFieldMessages(validationRep.getFieldMessages());
		
		// gather validation messages from nested mappings
		Set<ConstraintViolationMessage> globalMsgs = new LinkedHashSet<ConstraintViolationMessage>(validationRep.getGlobalMessages());
		for (FormData<?> formData : nestedFormData.values()) {
			fieldMsgs.putAll(formData.getValidationReport().getFieldMessages());
			globalMsgs.addAll(formData.getValidationReport().getGlobalMessages());
		}
		return new FormData<T>(filledObject.getData(), new ValidationReport(fieldMsgs, globalMsgs));
	}
	
	Map<String, FormData<?>> loadDataForMappings(Map<String, BasicFormMapping<?>> mappings, ParamsProvider paramsProvider) {
		Map<String, FormData<?>> dataMap = new LinkedHashMap<String, FormData<?>>();
		for (Map.Entry<String, BasicFormMapping<?>> e : mappings.entrySet()) {
			dataMap.put(e.getKey(), e.getValue().loadData(paramsProvider));
		}
		return dataMap;
	}
	
	BasicFormMappingBuilder<T> fillInternal(FormData<T> editedObj) {
		List<BasicFormMappingBuilder<?>> newNestedMappingBuilders = new ArrayList<BasicFormMappingBuilder<?>>();
		Map<String, BasicFormMapping<?>> newNestedMappings = new LinkedHashMap<String, BasicFormMapping<?>>();
		for (Map.Entry<String, BasicFormMapping<?>> e : this.nestedMappings.entrySet()) {
			Object data = nestedData(e.getKey(), editedObj.getData());
			FormData formData = new FormData<Object>(data, editedObj.getValidationReport()); // the outer report is propagated to nested
			BasicFormMappingBuilder<?> mb = e.getValue().fillInternal(formData);
			newNestedMappingBuilders.add(mb);
			newNestedMappings.put(e.getKey(), mb.build());
		}
		
		// Preparing values
		Map<String, Object> propValues = config.getBeanExtractor().extractBean(editedObj.getData(), getAllowedProperties(fields));

		// Add values to form fields
		Map<String, FormField> filledFields = new HashMap<String, FormField>(fields);
		for (Map.Entry<String, Object> entry : propValues.entrySet()) {
			// find field by property name:
			String propertyName = entry.getKey();
			final FormField field = filledFields.get(propertyName); 
			final FormField filledField = FormFieldImpl.getFilledInstance(
				field.getName(), field.getPattern(), 
				fieldValues(entry.getValue()), config.getLocale(), config.getFormatters());
			filledFields.put(propertyName, filledField);
		}
		filledFields = Collections.unmodifiableMap(filledFields);

		// Returning copy of this form that is filled with form data
		BasicFormMappingBuilder<T> builder = new BasicFormMappingBuilder<T>(getDataClass(), getPath())
			.fields(filledFields);
		builder.nestedMappings = newNestedMappings;
		builder.validationReport = editedObj.getValidationReport();
		return builder;
	}

	Set<String> getAllowedProperties(Map<String, FormField> fields) {
		Set<String> props = new LinkedHashSet<String>();
		for (FormField field : fields.values()) {
			// name of field is a full path, already prefixed with form name
			String propName = FormUtils.fieldNameToPropertyName(field.getName());
			props.add(propName);
		}
		return props;
	}
	
	Config getConfig() {
		return this.config;
	}
	
	/**
	 * Returns copy of this mapping with new path that has given prefix prepended.
	 * Given prefix is applied to all nested mappings.
	 * @param pathPrefix
	 * @return
	 */
	BasicFormMapping<T> withPathPrefix(String pathPrefix) {
		return new BasicFormMapping<T>(this, pathPrefix);
	}
	
	BasicFormMapping<T> withConfig(Config config) {
		return new BasicFormMapping<T>(this, config);
	}
	
	private Config chooseConfigForNestedMapping(BasicFormMapping<?> mapping, Config outerConfig) {
		Config nestedMappingConfig = mapping.config;
		if (!mapping.userDefinedConfig && outerConfig != null) {
			// config for nested mapping was not explicitly defined, we will pass outer config to nested mapping
			nestedMappingConfig = outerConfig;
		}
		return nestedMappingConfig;
	}
	
	private <U> List<U> flatten(Collection<List<U>> collOfLists) {
		List<U> res = new ArrayList<U>();
		for (List<U> l : collOfLists) {
			res.addAll(l);
		}
		return res;
	}
	
	private Object nestedData(String propName, T data) {
		Map<String, Object> props = config.getBeanExtractor().extractBean(data, Collections.singleton(propName));
		return props.get(propName); // can be null if nested object is not required
	}
	
	private List<Object> fieldValues(Object value) {
		List<Object> values = new ArrayList<Object>();
		if (value instanceof Iterable) {
			for (Object v : ((Iterable<?>)value)) {
				values.add(v);
			}
		} else if (value != null && value.getClass().isArray()) {
			fillValuesFromArrayValue(value, values);
	    } else {
			values.add(value);
		}
		return values;
	}

	private void fillValuesFromArrayValue(Object array, List<Object> values) {
		if (array.getClass().equals(boolean[].class)) {
			boolean[] arr = (boolean[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Boolean.valueOf(arr[i]));
		} else if (array.getClass().equals(byte[].class)) {
			byte[] arr = (byte[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Byte.valueOf(arr[i]));
		} else if (array.getClass().equals(short[].class)) {
			short[] arr = (short[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Short.valueOf(arr[i]));
		} else if (array.getClass().equals(int[].class)) {
			int[] arr = (int[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Integer.valueOf(arr[i]));
		} else if (array.getClass().equals(long[].class)) {
			long[] arr = (long[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Long.valueOf(arr[i]));
		} else if (array.getClass().equals(float[].class)) {
			float[] arr = (float[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Float.valueOf(arr[i]));
		} else if (array.getClass().equals(double[].class)) {
			double[] arr = (double[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Double.valueOf(arr[i]));
		} else if (array.getClass().equals(char[].class)) {
			char[] arr = (char[])array;
			for (int i = 0; i < arr.length; i++)
				values.add(Character.valueOf(arr[i]));
		}
	}

	private Map<String, BoundValuesInfo> prepareBoundValues(ParamsProvider paramsProvider) {
		Map<String, BoundValuesInfo> values = new HashMap<String, BoundValuesInfo>();
		// Get values for each defined field
		for (Map.Entry<String, FormField> e : fields.entrySet()) {
			FormField field = e.getValue();
			String formPrefixedName = field.getName(); // already prefixed with form name
			if (!formPrefixedName.startsWith(getPath() + Forms.PATH_SEP)) {
				throw new IllegalStateException("Field name '"
						+ formPrefixedName + "' not prefixed with path '"
						+ getPath() + "'");
			}
			
			Object[] paramValues = null;
			UploadedFile[] files = paramsProvider.getUploadedFiles(formPrefixedName);
			if (files == null) files = paramsProvider.getUploadedFiles(formPrefixedName + "[]");
			if (files != null) {
				paramValues = files;
			} else {
				String[] strValues = paramsProvider.getParamValues(formPrefixedName);
				if (strValues == null) strValues = paramsProvider.getParamValues(formPrefixedName + "[]");
				if (config.isInputTrimmed()) {
					strValues = trimValues(strValues);
				}
				paramValues = strValues;
			}
			String propertyName = e.getKey();
			values.put(propertyName, BoundValuesInfo.getInstance(
			  paramValues, field.getPattern(), config.getLocale()));
		}
		return values;
	}

	private String[] trimValues(String[] strValues) {
		if (strValues == null) return null;
		String[] trimmed = new String[strValues.length];
		for (int i = 0; i < strValues.length; i++) {
			trimmed[i] = strValues[i] != null ? strValues[i].trim() : null;
		}
		return trimmed;
	}
	
	private Map<String, Set<ConstraintViolationMessage>> cloneFieldMessages(Map<String, Set<ConstraintViolationMessage>> fieldMsgs) {
	  Map<String, Set<ConstraintViolationMessage>> fieldMsgCopy = new LinkedHashMap<String, Set<ConstraintViolationMessage>>();
	  for (Map.Entry<String, Set<ConstraintViolationMessage>> entry : fieldMsgs.entrySet()) {
		  fieldMsgCopy.put(entry.getKey(), new LinkedHashSet<ConstraintViolationMessage>(entry.getValue()));	
	  }
	  return fieldMsgCopy;
	}

}
