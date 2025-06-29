# Why use Formio?

Why should you prefer Formio library over other frameworks that allow forms processing? It is really simple to use, non-invasive and easy-integrable tool that can be used almost everywhere (in servlets, portlets, but also [in component frameworks](https://github.com/beranradek/formio-swing)). It is useful lightweight library rather than a complex framework with its own lifecycle and more or less complicated widgets/components.

## Comparison with form processing in Spring MVC

### Formio Pros:

- Even easier to learn and use than form processing in Spring.
- Functional style of form processing and immutable/composable/reusable form definitions can more easily avoid accidental introduction of errors.
- Immutable domain objects can be used as form backing objects (data can be bound via constructors, static factory methods) – no need for custom Spring MVC WebArgumentResolver(s) or HttpMessageConverter(s).
- Minimum of dependencies (bean validation API validator, fileupload, optional dependencies are servlet-api or portlet-api).
- Message bundle specific for form data class is used by default, with fallback to common ValidationMessages. Custom message bundle can be easily specified.
- Not bound to any particular UI architecture like MVC, but fully usable in Spring MVC or other frameworks.
- Automatic construction of form field names/paths that works seamlessly also for nested objects/lists and compact self-contained form definition can be used to further automatize rendering and processing of forms.
- Form markup can be (semi)automatically generated using FormRenderer class or its extension, if desired. This can be especially helpful for bigger applications with consistent style of forms (much writing of markup can be spared).
- Formio is not limited to servlet API with HttpServletRequest. Other implementations of preprocessing of the request (ParamsProvider) can be used. For e.g. PortletRequestParams is available, [SwingRequestParams](https://github.com/beranradek/formio-swing/blob/master/src/main/java/net/formio/swing/SwingRequestParams.java) can be implemented, ...

### Spring MVC Pros:

- Spring is widely used and understood and offers usable solution in Spring MVC, natural for Spring environment.
