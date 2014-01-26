package org.twinstone.formio;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.twinstone.formio.validation.ValidationReport;

/**
 * Basic builder of {@link FormMapping}.
 * @author Radek Beran
 */
public class BasicFormMappingBuilder<T> {

	/** name prefixing all names of the fields in this mapping; 
	 * it contains complete path to this nested mapping, if this is a nested mapping. */
	String path;
	Class<T> objectClass;
	/** Mapping simple property names to fields. */
	Map<String, FormField> fields = new HashMap<String, FormField>();
	ValidationReport validationReport;
	/** Mapping simple property names to nested mappings. Property name is a part of full path of nested mapping. */
	Map<String, BasicFormMapping<?>> nestedMappings = new LinkedHashMap<String, BasicFormMapping<?>>();
	Config config;
	boolean userDefinedConfig;

	/**
	 * Can be constructed only from the same package/subclass.
	 * Should be constructed only via {@link Forms} entry point of API.
	 */
	BasicFormMappingBuilder(Class<T> objectClass, String formName) {
		if (objectClass == null) throw new IllegalArgumentException("objectClass must be filled");
		if (formName == null || formName.isEmpty()) throw new IllegalArgumentException("form name must be filled");
		this.objectClass = objectClass;
		this.path = formName;
	}
	
	public BasicFormMappingBuilder<T> field(String name, String pattern) {
		String formPrefixedName = formPrefixedName(name);
		fields.put(name, FormFieldImpl.getInstance(formPrefixedName, pattern));
		return this;
	}
	
	public BasicFormMappingBuilder<T> field(String name) {
		return field(name, null);
	}
	
	public BasicFormMappingBuilder<T> fields(String ... fieldNames) {
		for (int i = 0; i < fieldNames.length; i++) {
			field(fieldNames[i]);
		}
		return this;
	}
	
	/**
	 * Registers form mapping for nested object in form data.
	 * @param nestedMapping nested mapping - with class of nested object and form name that is
	 * equal to name of property in outer mapped object
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> nested(BasicFormMapping<?> nestedMapping) {
		// nested mapping is defined with path that is one simple name that corresponds to the name of the property
		String propertyName = getFirstPathName(nestedMapping.getPath());
		
		// Name of nested mapping and names of its fields must be prefixed by path of this mapping
		// (and recursively - nested mapping can have its own nested mappings).
		this.nestedMappings.put(propertyName, nestedMapping.withPathPrefix(this.path));
		
		// should return BasicFormMappingBuilder, not ConfigurableBasicFormMappingBuilder
		// all configurable dependencies are taken from outer mapping
		return this;
	}
	
	/**
	 * Registers form mappings for nested objects in form data.
	 * @param nestedMappings nested mappings
	 * @return
	 */
	public BasicFormMappingBuilder<T> nestedMappings(BasicFormMapping<?> ... nestedMappings) {
		if (nestedMappings != null) {
			for (BasicFormMapping<?> b : nestedMappings) {
				nested(b);
			}
		}
		return this;
	}

	public BasicFormMapping<T> build() {
		return buildInternal(
			Forms.config()
				.messageBundleName(objectClass.getName().replace(".", "/"))
				.build(), false
		); // default config
	}
	
	public BasicFormMapping<T> build(Config config) {
		return buildInternal(config, true);
	}
	
	BasicFormMapping<T> buildInternal(Config config, boolean userDefinedConfig) {
		if (config == null) throw new IllegalArgumentException("config cannot be null");
		this.userDefinedConfig = userDefinedConfig;
		this.config = config;
		return new BasicFormMapping<T>(this);
		
	}
	
	/** Adding multiple form fields. Operation for internal use only. */
	BasicFormMappingBuilder<T> fields(Map<String, FormField> fields) {
		if (fields == null) throw new IllegalArgumentException("fields cannot be null");
		Map<String, FormField> flds = new HashMap<String, FormField>();
		for (Map.Entry<String, FormField> e : fields.entrySet()) {
			FormField f = e.getValue();
			if (!f.getName().startsWith(path + Forms.PATH_SEP)) {
				throw new IllegalStateException("Field name is not prefixed with form name '" + path + "'!");
			}
			flds.put(e.getKey(), f);
		}
		this.fields.putAll(flds);
		return this;
	}
	
	private String getFirstPathName(String path) {
		String name = null;
		int indexOfSep = path.indexOf(Forms.PATH_SEP);
		if (indexOfSep < 0) {
			name = path;
		} else {
			name = path.substring(0, indexOfSep);
		}
		return name;
	}
	
	private String formPrefixedName(String name) {
		if (name == null) return null;
		return path + Forms.PATH_SEP + name;
	}
}

