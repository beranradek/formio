# Formio

## Form definition & binding library for Java platform

📎 [Download (Maven)](http://search.maven.org/#search%7Cga%7C1%7Cnet.formio)  
📎 [Changelog](https://github.com/beranradek/formio/blob/master/changelog.txt)  
📎 [Issues](https://github.com/beranradek/formio/issues)  

📎 Documentation:
  * [Get started](get-started.md)
  * [Why to use Formio?](formio-why-use.md)
  * Reference documentation
    * [Introduction](introduction.md)
    * [Form validation](form-validation.md)
    * [Configuration](config.md)
    * [File uploads](file-upload.md)
    * [Security](security.md)
    * [Formio and AJAX](ajax.md)
    * [Sources of parameters, forms in portlets](param-sources.md)

---

## Need a form?

Formio can do the repetitive hard work for you. With passion for simplicity.

---

## Features

- Easy-to-use configurable handy tool.
- Automatic bidirectional binding, even to immutable objects, primitives, collections and arrays, nested objects and lists of them.
- Support for (non)default constructors, static factory methods.
- Validation of form data (both bean validation API annotations and `net.formio.validation.Validator` can be used).
- Seamless support for file uploads and configurable max. request/file size.
- Form definitions are immutable, composable, self-contained, can be easily shared and cached.
- Automatic generating of form markup (or its parts) can be optionally used.
- One simple entry point to API: `Forms` class.
- Non-invasive – easy integration with frameworks, minimum dependencies.
- Usable with various template frameworks, with servlets, portlets or even in [desktop applications](https://github.com/beranradek/formio-swing).
- Simply unit testable forms.
- Good test coverage (about 70% of lines).
- Protection against CSRF attacks using authorization tokens in forms.
- Inspired mainly by well-designed [Play! framework](https://www.playframework.com/).

---

## License

📄 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) – allows usage also in commercial projects.

---

## Three Steps to Get Started

### 1. Prepare form definition (optional automatic mapping of properties):

```java
private static final FormMapping<Person> personForm =
    Forms.automatic(Person.class, "person").build();
```

### 2. Fill it with data:

```java
FormData<Person> formData = new FormData<>(
    person, ValidationResult.empty);
FormMapping<Person> filledForm = 
    personForm.fill(formData);
// Push the filled form into a template, 
// use its properties to render it; or use 
// FormRenderer to generate form markup
// automatically
```

### 3. Bind data from request back into an object:

```java
FormData<Person> formData = personForm.bind(
    new ServletRequestParams(request));
if (formData.isValid()) {
    // save the person: formData.getData()
} else {
    // show again the invalid form with validation 
    // messages: personForm.fill(formData) ...
}
```
