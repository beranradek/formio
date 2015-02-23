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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.formio.data.RequestContext;
import net.formio.internal.FormUtils;
import net.formio.servlet.ServletRequestParams;
import net.formio.upload.MaxSizeExceededError;
import net.formio.upload.RequestProcessingError;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Implementation of {@link FormMapping} that is expanded to list of indexed mappings 
 * when filled with data (list of objects). Immutable when not filled.
 * After the filling, new instance of mapping is created and its immutability 
 * depends on the character of filled data.
 * 
 * @author Radek Beran
 */
public class BasicListFormMapping<T> extends BasicFormMapping<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	// make another type parameter for element of list (this is new parameter) and for the list itself = T?
	
	/**
	 * Mappings for individual elements in list of edited objects.
	 */
	private final List<FormMapping<T>> listOfMappings;
	
	/**
	 * Construct the mapping from given builder.
	 * @param builder
	 * @param simpleCopy true if simple copy of builder's data should be constructed, otherwise propagation
	 * of parent mapping into fields and nested mappings is processed
	 */
	BasicListFormMapping(BasicFormMappingBuilder<T> builder, boolean simpleCopy) {
		super(builder, simpleCopy);
		this.listOfMappings = newListOfMappings(builder.listOfMappings);
	}
	
	/**
	 * Returns copy with given order (called when appending this nested mapping to outer builder).
	 * @param src
	 * @param order
	 */
	BasicListFormMapping(BasicListFormMapping<T> src, int order) {
		super(src, order);
		this.listOfMappings = newListOfMappings(src.listOfMappings);
	}
	
	/**
	 * Returns copy with given parent and config.
	 * @param src
	 * @param parent
	 * @param required
	 */
	BasicListFormMapping(BasicListFormMapping<T> src, FormMapping<?> parent) {
		super(src, parent);
		this.listOfMappings = newListOfMappings(src.listOfMappings);
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
	public FormData<T> bind(final RequestParams paramsProvider, final Locale locale, final T instance, final RequestContext context, final Class<?>... validationGroups) {
		RequestContext ctx = context;
		if (ctx == null && paramsProvider instanceof ServletRequestParams) {
			// fallback to ctx retrieved from ServletRequestParams, so the user need not to specify ctx explicitly for bind method
			ctx = ((ServletRequestParams)paramsProvider).getRequestContext();
		}
		
		final RequestProcessingError error = paramsProvider.getRequestError();
		
		// Finding how many parameters are in the request - check for max. index available in request params name, 
		// according to this mapping path
		int maxIndex = FormUtils.findMaxIndex(paramsProvider.getParamNames(), getName());
		
		// Constructing mappings for each index up to max. index.
		// Nested mapping of this list mapping will become nested mappings of each
		// index-related mapping.
		List<FormMapping<T>> listMappings = new ArrayList<FormMapping<T>>();
		for (int index = 0; index <= maxIndex; index++) {
			ValidationResult res = null;
			if (this.getValidationResult() != null) {
				res = new ValidationResult(
					new LinkedHashMap<String, List<ConstraintViolationMessage>>(this.getValidationResult().getFieldMessages()),
					new ArrayList<ConstraintViolationMessage>(this.getValidationResult().getGlobalMessages()));
			}
			
			// constructing single mapping for index:
			BasicFormMappingBuilder<T> builder = new BasicFormMappingBuilder<T>(this, this.fields, this.nested)
				.index(Integer.valueOf(index))
				.order(index)
				.validationResult(res);
			builder.mappingType = MappingType.SINGLE;
			listMappings.add(builder.build(getConfig()));
		}
		
		// Loading data for constructed mappings for individual indexes
		// Tie these nested objects together to a list
		List<T> data = new ArrayList<T>();
		Map<String, List<ConstraintViolationMessage>> fieldMsgs = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		List<ConstraintViolationMessage> globalMsgs = new ArrayList<ConstraintViolationMessage>();
		for (int index = 0; index < listMappings.size(); index++) {
			FormMapping<T> m = listMappings.get(index);
			T instanceForIndex = null;
			if (instance instanceof List) {
				List<T> listInstance = (List<T>)instance;
				if (index < listInstance.size()) {
					instanceForIndex = listInstance.get(index);
				}
			}
			FormData<T> formData = m.bind(paramsProvider, locale, instanceForIndex, ctx, validationGroups);
			data.add(formData.getData());
			fieldMsgs.putAll(formData.getValidationResult().getFieldMessages());
			globalMsgs.addAll(formData.getValidationResult().getGlobalMessages());
		}
		
		if (!(error instanceof MaxSizeExceededError)) {
			// Must be executed after processing of nested mappings
			if (this.secured && isRootMapping()) {
				throw new UnsupportedOperationException("Verification of authorization token is not supported "
					+ "in root list mapping. Please create SINGLE root mapping with nested list mapping.");
			}
			if (this.secured) {
				AuthTokens.verifyAuthToken(ctx, getConfig().getTokenAuthorizer(), getRootMappingPath(), paramsProvider, isRootMapping());
			}
		}
		
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
			Map<String, FormMapping<?>> filledIndexedNestedMappings = indexAndFillNestedMappings(formDataAtIndex, locale, ctx);
			
			// Prepare values for mapping that is constructed for current list index.
			// Previously created filled nested mappings will be assigned to mapping for current list index.
			Map<String, Object> propValues = gatherPropertyValues(dataAtIndex, propNames, ctx);
			
			// Fill the fields of this mapping with prepared values for current list index
			Map<String, FormField<?>> filledFields = fillFields(
				propValues, 
				editedObj.getValidationResult() != null ?
					editedObj.getValidationResult().getFieldMessages() : new LinkedHashMap<String, List<ConstraintViolationMessage>>(),
				index, 
				locale);
			
			// Returning copy of this mapping (for current index) that is filled with form data,
			// but with single mapping type (for an index) and now without list mappings
			BasicFormMappingBuilder<T> builder = new BasicFormMappingBuilder<T>(this, filledFields, filledIndexedNestedMappings)
				.index(Integer.valueOf(index))
				.order(index)
				.validationResult(formDataAtIndex.getValidationResult())
				.filledObject(formDataAtIndex.getData());
			builder.mappingType = MappingType.SINGLE;
			newMappings.add(builder.build(getConfig()));
			index++;
		}
		// unindexed fields (that are only recipes for indexed fields) will not be part of filled form
		// as well as unindexed nested mappings -> empty maps are used:
		BasicFormMappingBuilder<T> builder = new BasicFormMappingBuilder<T>(this, 
			Collections.unmodifiableMap(Collections.<String, FormField<?>>emptyMap()), 
			Collections.unmodifiableMap(Collections.<String, FormMapping<?>>emptyMap()))
			.validationResult(editedObj.getValidationResult())
			.filledObject(editedObj.getData());
		builder.listOfMappings = Collections.unmodifiableList(newMappings);
		return builder;
	}
	
	@Override
	public List<FormMapping<T>> getList() {
		return this.listOfMappings;
	}
	
	@Override
	public BasicListFormMapping<T> withOrder(int order) {
		return new BasicListFormMapping<T>(this, order);
	}
	
	@Override
	public BasicListFormMapping<T> withParent(FormMapping<?> parent) {
		return new BasicListFormMapping<T>(this, parent);
	}
	
	@Override
	ValidationResult validate(Locale locale, Class<?> ... validationGroups) {
		Collection<ValidationResult> validationResults = new ArrayList<ValidationResult>();
		List<FormMapping<T>> listMappings = getList();
		for (int index = 0; index < listMappings.size(); index++) {
			validationResults.add(((BasicFormMapping<?>)listMappings.get(index)).validate(locale, validationGroups));
		}
		return Clones.mergedValidationResults(validationResults);
	}
	
	Map<String, FormMapping<?>> indexAndFillNestedMappings(FormData<T> editedObj, Locale locale, RequestContext ctx) {
		Map<String, FormMapping<?>> newNestedMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : this.nested.entrySet()) {
			// nested data - nested object or list of nested objects in case of mapping to list
			Object data = nestedData(e.getKey(), editedObj.getData());
			// the outer report is propagated to nested
			FormData formData = new FormData<Object>(data, editedObj.getValidationResult());
			newNestedMappings.put(e.getKey(), e.getValue().fill(formData, locale, ctx));
		}
		return newNestedMappings;
	}
	
	private List<FormMapping<T>> newListOfMappings(List<FormMapping<T>> listOfMappings) {
		return Collections.unmodifiableList(new ArrayList<FormMapping<T>>(listOfMappings));
	}
}
