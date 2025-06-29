# Get Started

## Content

- [Web application project](#web-application-project)
- [Formio dependencies](#formio-dependencies)
- [Person editing controller](#person-editing-controller)
- [Class for edited person](#class-for-edited-person)
- [Form definition](#form-definition)
- [Prepopulating the form with data](#prepopulating-the-form-with-data)
- [Rendering the form](#rendering-the-form)
- [Binding data from request (form submission)](#binding-data-from-request-form-submission)
- [Validation constraints](#validation-constraints)
- [Conclusion](#conclusion)

## Web application project

As an example we will create a simple web application with a person editing form.
The `Person` object will have `firstName`, `lastName`, `email`, and a nested
`Address` object (which is immutable).

We will use Maven, but Gradle or SBT can also be used.

## Formio dependencies

Add Formio dependency to `pom.xml`:

```xml
<dependency>
  <groupId>net.formio</groupId>
  <artifactId>formio</artifactId>
  <version>1.6.0</version>
</dependency>
```

Add Hibernate Validator for Bean Validation:

```xml
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.2.4.Final</version> 
</dependency>
```

## Person editing controller

Create servlet `PersonController`:

```java
@WebServlet("/")
public class PersonController extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
  }
}
```

Basic `index.jsp`:

```html
<html>
  <body>
    <h1>Formio Getting Started</h1>
  </body>
</html>
```

Run the app and open `http://localhost:8080/formio-getstarted/`.

## Class for edited person

Create a `Person` class:

```java
public class Person {
  private String firstName;
  private String lastName;
  private String email;
  private Address contactAddress;

  // Getters and setters...
}
```

Immutable `Address` class:

```java
public class Address {
  private final String street;
  private final String city;
  private final Integer zipCode;

  public Address(@ArgumentName("street") String street, 
                 @ArgumentName("city") String city, 
                 @ArgumentName("zipCode") Integer zipCode) {
    this.street = street;
    this.city = city;
    this.zipCode = zipCode;
  }

  // Getters...
}
```

## Form definition

```java
private static final FormMapping<Person> personForm = 
  Forms.automatic(Person.class, "person").build();
```

## Prepopulating the form with data

```java
protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  response.setContentType("text/html;charset=UTF-8");
  Person person = new Person();
  person.setFirstName("John");
  FormData<Person> formData = new FormData<>(person, ValidationResult.empty);
  FormMapping<Person> filledForm = personForm.fill(formData);
  request.setAttribute("personForm", filledForm);
  request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
}
```

## Rendering the form

Add JSTL to `pom.xml`:

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>jstl</artifactId>
  <version>1.2</version>
</dependency>
```

Basic form field rendering (use tag libraries, EL, and localization bundle):

```properties
person-firstName=First name
person-lastName=Last name
person-email=E-mail
person-contactAddress=Contact address
person-contactAddress-street=Street
person-contactAddress-city=City
person-contactAddress-zipCode=ZIP code
```

Automatic rendering via:

```java
String formMarkup = new FormRenderer(new RenderContext(locale)).renderElement(filledForm);
```

## Binding data from request (form submission)

```java
FormData<Person> formData = null;
if (request.getParameter("submit") != null) {
  RequestParams params = new ServletRequestParams(request);
  formData = personForm.bind(params);
  if (formData.isValid()) {
    Person person = formData.getData();
    response.sendRedirect(request.getContextPath() + "/?success=1");
    return;
  }
} else {
  Person person = new Person();
  person.setFirstName("John");
  formData = new FormData<>(person, ValidationResult.empty);
}
FormMapping<Person> filledForm = personForm.fill(formData);
request.setAttribute("personForm", filledForm);
request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
```

## Validation constraints

Use Formio's `@Email` annotation:

```java
@Email
private String email;
```

Validation messages in:

```properties
constraints.Email.message=Invalid e-mail
```

Rendered via JSP tag iteration:

```xml
<c:forEach var="message" items="${fields.email.validationMessages}">
  <div class="${message.severity.styleClass}">${message.text}</div>
</c:forEach>
```

## Conclusion

- Prepare form definition (automatic or manual).
- Fill it with data and push to template.
- Render using template system or `FormRenderer`.
- Bind submitted data and retrieve validated object.
- Use message bundles for localization.
- Immutable form definitions and automatic binding simplify logic.

Explore more in the [Formio demo](https://github.com/beranradek/formio-demo) or subclass `FormRenderer` for custom output.