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
package net.formio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.formio.common.FormUtils;
import net.formio.data.RequestContext;
import net.formio.servlet.ServletRequestParams;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

// TODO RBe: Another type parameter for element of list (this is new parameter) and for the list itself = T.
class BasicListFormMapping<T> extends BasicFormMapping<T> {
	
	/**
	 * Mappings for individual elements in list of edited objects.
	 */
	private final List<FormMapping<T>> listOfMappings;
	
	/**
	 * Construct the mapping from given builder.
	 * @param builder
	 */
	BasicListFormMapping(BasicFormMappingBuilder<T> builder) {
		super(builder);
		this.listOfMappings = new ArrayList<FormMapping<T>>(builder.listOfMappings);
	}
	
	/**
	 * Returns copy with given path prefix prepended.
	 * @param src
	 * @param pathPrefix
	 */
	BasicListFormMapping(BasicListFormMapping<T> src, String pathPrefix) {
		super(src, pathPrefix);
		this.listOfMappings = new ArrayList<FormMapping<T>>(src.listOfMappings);
	}
	
	/**
	 * Returns copy of this mapping with new path that contains index after given path prefix.
	 * Given index is applied to all nested mappings recursively.
	 * @param src
	 * @param index
	 * @param pathPrefix
	 */
	BasicListFormMapping(BasicListFormMapping<T> src, int index, String pathPrefix) {
		super(src, index, pathPrefix);
		this.listOfMappings = new ArrayList<FormMapping<T>>(src.listOfMappings);
	}
	
	/**
	 * Returns copy with given config.
	 * @param src
	 * @param config
	 * @param required
	 */
	BasicListFormMapping(BasicListFormMapping<T> src, Config config, boolean required) {
		super(src, config, required);
		this.listOfMappings = new ArrayList<FormMapping<T>>(src.listOfMappings);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, Class<?> ... validationGroups) {
		return bind(paramsProvider, locale, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, RequestContext ctx, Class<?> ... validationGroups) {
		return bind(paramsProvider, locale, (T)null, ctx, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, T instance, Class<?>... validationGroups) {
		return bind(paramsProvider, locale, instance, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, T instance, RequestContext ctx, Class<?>... validationGroups) {
		if (ctx == null && paramsProvider instanceof ServletRequestParams) {
			// fallback to ctx retrieved from ServletRequestParams, so the user need not to specify ctx explicitly for bind method
			ctx = ((ServletRequestParams)paramsProvider).getRequestContext();
		}
		
		// Finding how many parameters are in the request - check for max. index available in request params name, 
		// according to this mapping path
		int maxIndex = FormUtils.findMaxIndex(paramsProvider.getParamNames(), this.path);
		
		// Constructing mappings for each index up to max. index.
		// Nested mapping of this list mapping will become nested mappings of each
		// index-related mapping.
		List<FormMapping<T>> listMappings = new ArrayList<FormMapping<T>>();
		for (int index = 0; index <= maxIndex; index++) {
			String indexedPath = getIndexedPath(index);
			// constructing single mapping for index:
			// fields with indexed names
			final Map<String, FormField> formFields = fieldsWithIndexBeforeLastProperty(this.fields, index);
			BasicFormMappingBuilder<T> builder = null;
			if (this.secured) {
				builder = Forms.basicSecured(getDataClass(), indexedPath, getInstantiator(), MappingType.SINGLE)
					.fields(formFields);
			} else {
				builder = Forms.basic(getDataClass(), indexedPath, getInstantiator(), MappingType.SINGLE)
					.fields(formFields);
			}
			
			// Nested mappings must have index appended to their path (and to their fields)
			Map<String, FormMapping<?>> nestedForIndex = new LinkedHashMap<String, FormMapping<?>>();
			for (Map.Entry<String, FormMapping<?>> e : this.nested.entrySet()) {
				nestedForIndex.put(e.getKey(), e.getValue().withIndexAfterPathPrefix(index, this.path));
			}
			builder.nested = nestedForIndex;
			
			ValidationResult res = null;
			if (this.getValidationResult() != null) {
				res = new ValidationResult(
					new LinkedHashMap<String, Set<ConstraintViolationMessage>>(this.getValidationResult().getFieldMessages()),
					new LinkedHashSet<ConstraintViolationMessage>(this.getValidationResult().getGlobalMessages()));
			}
			builder.validationResult = res;
			builder.mappingType = MappingType.SINGLE;
			// no filledObject - already loading data from request in the following code 
			builder.userDefinedConfig = this.userDefinedConfig;
			listMappings.add(builder.build(this.getConfig()));
		}
		
		// Loading data for constructed mappings for individual indexes
		// Tie these nested objects together to a list
		List<T> data = new ArrayList<T>();
		Map<String, Set<ConstraintViolationMessage>> fieldMsgs = new LinkedHashMap<String, Set<ConstraintViolationMessage>>();
		Set<ConstraintViolationMessage> globalMsgs = new LinkedHashSet<ConstraintViolationMessage>();
		for (int index = 0; index < listMappings.size(); index++) {
			FormMapping<T> m = listMappings.get(index);
			T instanceForIndex = null;
			if (instance instanceof List && index < ((List<T>)instance).size()) {
				instanceForIndex = ((List<T>)instance).get(index);
			}
			FormData<T> formData = m.bind(paramsProvider, locale, instanceForIndex, ctx, validationGroups);
			data.add(formData.getData());
			fieldMsgs.putAll(formData.getValidationResult().getFieldMessages());
			globalMsgs.addAll(formData.getValidationResult().getGlobalMessages());
		}
		
		// Must be executed after processing of nested mappings
		verifyAuthTokenIfSecured(paramsProvider, ctx, true);
		
		ValidationResult validationRes = new ValidationResult(fieldMsgs, globalMsgs);
		FormData<List<T>> formData = new FormData<List<T>>(data, validationRes);
		
		return (FormData<T>)formData;
	}
	
	@Override
	BasicFormMappingBuilder<T> fillInternal(FormData<T> editedObj, Locale locale, RequestContext ctx) {
		List<FormMapping<T>> newMappings = new ArrayList<FormMapping<T>>();
		Set<String> propNames = FormUtils.getPropertiesFromFields(this.fields);
		int index = 0;
		for (T dataAtIndex : (List<T>)editedObj.getData()) {
			FormData<T> formDataAtIndex = new FormData<T>(dataAtIndex, editedObj.getValidationResult());
			
			// Create filled nested mappings for current list index (data at current index)
			Map<String, FormMapping<?>> newNestedMappings = indexAndFillNestedMappings(index, formDataAtIndex, locale, ctx);
			
			// Prepare values for mapping that is constructed for current list index.
			// Previously created filled nested mappings will be assigned to mapping for current list index.
			Map<String, Object> propValues = gatherPropertyValues(dataAtIndex, propNames, ctx);
			
			// Fill the fields of this mapping with prepared values for current list index
			Map<String, FormField> filledFields = fillFields(propValues, index, locale);
			
			// Returning copy of this mapping (for current index) that is filled with form data,
			// but with single mapping type (for an index) and now without list mappings
			String indexedPath = getIndexedPath(index);
			BasicFormMappingBuilder<T> builder = null;
			if (this.secured) {
				builder = Forms.basicSecured(getDataClass(), indexedPath, getInstantiator(), MappingType.SINGLE)
					.fields(filledFields);
			} else {
				builder = Forms.basic(getDataClass(), indexedPath, getInstantiator(), MappingType.SINGLE)
					.fields(filledFields);
			}
			
			builder.nested = newNestedMappings;
			builder.validationResult = formDataAtIndex.getValidationResult();
			builder.filledObject = formDataAtIndex.getData();
			builder.mappingType = MappingType.SINGLE;
			builder.userDefinedConfig = this.userDefinedConfig;
			newMappings.add(builder.build(this.getConfig()));
			index++;
		}
		// unindexed fields (that are only recipes for indexed fields) will not be part of filled form
		// as well as unindexed nested mappings -> empty maps are used:
		Map<String, FormField> emptyFields = Collections.unmodifiableMap(Collections.<String, FormField>emptyMap());
		BasicFormMappingBuilder<T> builder = null;
		if (this.secured) {
			builder = Forms.basicSecured(getDataClass(), this.path, getInstantiator(), MappingType.LIST).fields(emptyFields);
		} else {
			builder = Forms.basic(getDataClass(), this.path, getInstantiator(), MappingType.LIST).fields(emptyFields);
		}
		
		builder.nested = Collections.unmodifiableMap(Collections.<String, FormMapping<?>>emptyMap());
		builder.validationResult = editedObj.getValidationResult();
		builder.listOfMappings = newMappings;
		builder.filledObject = editedObj.getData();
		return builder;
	}
	
	@Override
	public List<FormMapping<T>> getList() {
		List<FormMapping<T>> ret = new ArrayList<FormMapping<T>>();
		for (FormMapping<T> m : this.listOfMappings) {
			ret.add(m);
		}
		return Collections.unmodifiableList(ret);
	}
	
	@Override
	public BasicListFormMapping<T> withIndexAfterPathPrefix(int index, String prefix) {
		return new BasicListFormMapping<T>(this, index, prefix);
	}
	
	@Override
	public BasicListFormMapping<T> withPathPrefix(String pathPrefix) {
		return new BasicListFormMapping<T>(this, pathPrefix);
	}
	
	@Override
	public BasicListFormMapping<T> withConfig(Config config, boolean required) {
		return new BasicListFormMapping<T>(this, config, required);
	}
	
	Map<String, FormMapping<?>> indexAndFillNestedMappings(int index, FormData<T> editedObj, Locale locale, RequestContext ctx) {
		Map<String, FormMapping<?>> newNestedMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : this.nested.entrySet()) {
			// nested data - nested object or list of nested objects in case of mapping to list
			Object data = nestedData(e.getKey(), editedObj.getData());
			FormData formData = new FormData<Object>(data, editedObj.getValidationResult()); // the outer report is propagated to nested
			
			// Path of this mapping is for e.g. registration-collegues
			// and nested mapping already has path registration-collegues-regDate.
			// We need take path of this mapping (without index) (registration-collegues),
			// add the index to it and add the rest of current path (-regDate)
			FormMapping newMapping = e.getValue().withIndexAfterPathPrefix(index, this.path);
			newNestedMappings.put(e.getKey(), newMapping.fill(formData, locale, ctx));
		}
		return newNestedMappings;
	}
	
	Map<String, FormField> fieldsWithIndexBeforeLastProperty(Map<String, FormField> fields, int index) {
		final Map<String, FormField> flds = new LinkedHashMap<String, FormField>();
		for (Map.Entry<String, FormField> e : fields.entrySet()) {
			FormField srcFld = e.getValue();
			String indexedName = FormUtils.pathWithIndexBeforeLastProperty(srcFld.getName(), index);
			final FormFieldImpl f = FormFieldImpl.getInstance(indexedName, 
				srcFld.getType(), srcFld.getPattern(), srcFld.getFormatter(), srcFld.isRequired());
			flds.put(e.getKey(), f);
		}
		return flds;
	}
	
	private String getIndexedPath(int index) {
		return this.path + "[" + index + "]";
	}
}
