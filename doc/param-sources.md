# Sources of parameters

Formio is not limited to processing parameters only from HttpServletRequest. Other sources of parameters can be implemented. Data binding (bind method) works with interface **RequestParams** - preprocessor of form parameters. This interface can be implemented to accept parameters from various environments. Available implementations are:

- **ServletRequestParams** - extracts parameters from HttpServletRequest,
- **PortletRequestParams** - extracts parameters from from javax.portlet.ActionRequest.

## Forms in portlets

For portlets, PortletRequestParams is available and you can use it in the same way as ServletRequestParams. It supports also file uploads. Of course you must have [portlet-api](http://search.maven.org/#search%7Cga%7C1%7Cjavax.portlet) in the classpath of your project.

Binding and validating parameters from javax.portlet.ActionRequest:

```java
FormData<Person> personData = personForm.bind(new PortletRequestParams(request));
if (personData.isValid()) {
  // save the person: formData.getData()
} else {
  // show again the invalid form with validation 
  // messages: personForm.fill(formData) ...
}
```