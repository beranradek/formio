# Formio and AJAX

## Plain JavaScript solution

```javascript
function ajaxHandleForm(formElement, formClass, formData) {
  const searchParams = new URLSearchParams(formData);
  // Submission of form data:
  fetch(formElement.getAttribute('action'), {
      method: formElement.getAttribute('method'),
      body: searchParams
  })
  .then(res => {
    // Parse response from server as JSON
    var responseJson = res.json();
    return responseJson;
  })
  .then(data => {
      if (data.redirectUri) {
        window.location.href = data.redirectUri; // Redirect to the URI if provided
      } else if (data.htmlContent) {
          const ajaxContentElement = document.querySelector('.' + formClass + '-ajax-content');
          if (ajaxContentElement) {
              ajaxContentElement.innerHTML = data.htmlContent;
          } else {
            console.error('Element to replace with AJAX data was not found.');
          }
      }
  })
  .catch(error => console.error('Error:', error));
}
```

## Formio & Twinstone TDI

Formio comes tightly integrated with [Twinstone TDI](https://twinstone.org/projects/tdi). Both Formio and TDI are non-invasive server-centered libraries that fit well together, see [demo](http://formio-demo.herokuapp.com/dynamic.html) and [code](https://github.com/beranradek/formio-demo/blob/master/src/main/java/net/formio/demo/forms/CarForm.java). AJAX requests work with data on the server and can easily fallback to rendering of whole page when AJAX (JavaScript) is not available.

Using TDI brings no additional Java dependency to your classpath, instead, you must include required JavaScripts in your pages: jQuery and TDI bundle (if you want to use such AJAX-powered forms, this is not required for Formio in general). See [TDI Download and Setup](https://wiki.twinstone.org/display/TDI/Download+and+Setup) for details.

On the form fields, you can define AjaxAction(s) for many JavaScript events. AjaxAction will receive parameters from AJAX request (in case some JavaScript event occured), it is responsible for updating state of the form on the server and for returning AJAX response. AjaxAction interface is defined like this:

```java
public interface AjaxAction<T> {
	AjaxResponse<T> apply(AbstractRequestParams requestParams);
}
```

### Constructing AJAX response

AjaxResponse holds updated form state (form object) and the constructed AJAX response itself (as a String containing XML) - this should be written to the servlet or portlet response (with Content-Type: application/xml HTTP header). AJAX response takes form of an XML document which consists from [TDI instructions](https://wiki.twinstone.org/display/TDI/Infusing+Protocol). These instructions can update defined parts of the page (of course the form fields and other nested parts of forms like nested form mappings) and can optionally execute also some JavaScript or change CSS classes as a part of AJAX response. Also new markup (not only updated one) can be inserted into the page.

#### Let's automate this

The good news are, rendering of TDI response can be as easy as saying "update this filled form field of form mapping for me" by using TdiResponseBuilder with support of FormRenderer (automatic generating of form markup). Form renderer knows how to render markup for your form fields and mappings and TdiResponseBuilder builds the resulting XML response from specified instructions that contain markup to update or insert. The most common instruction is "update of markup" with given id of surrounding element on the page. If you are using form renderer, you do not need to render markup manually (by calling some reusable template for given part of the form), but the markup for specified field or mapping is constructed automatically for you (this rendering can still be customized in many ways).

AjaxAction can be defined for form field or form mapping using dataAjaxActions method in definition:

```java
Forms.field("brand", Field.DROP_DOWN_CHOICE)
	.required(true)
	.choices(carService.findCarBrands())
	.chooseOptionDisplayed(true) // first "Choose One" option is displayed in select 
	.chooseOptionTitle("Choose car brand") // but with "Choose car brand" title 
	.dataAjaxActions(new JsEventToAction<Car>(brandChanged()))
```

where brandChanged() defines handling AjaxAction:

```java
private AjaxAction<Car> brandChanged() {
	return new FormStateAjaxAction<Car>(formStateHandler) {
		@Override
		public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
			TdiResponseBuilder rb = formRenderer.ajaxResponse();
			
			// Update form state with incomming AJAX request parameters
			FormData<Car> formData = definition(formState).bind(requestParams);
			formState.setBrand(formData.getData().getBrand());
			
			// Dependent "model" field should be refreshed using AJAX response 
			// (models specific for selected car brand are offered)
			FormMapping<Car> filledCarMapping = definition(formState).fill(new FormData<>(formData.getData()));
			// we should refresh also the car brand field itself, so the required validation message is updated
			return new AjaxResponse<>(
				rb.update(filledCarMapping.getField(CarBrand.class, "brand"))
				.update(filledCarMapping.getField(CarModel.class, "model"))
				.focusForName(requestParams.getTdiAjaxSrcElementName()) // set focus on brand select
				.asString(), // resulting AJAX XML response as a String 
				formState); // updated form state (also new instance can be constructed)
		}
	};
}
```

FormStateAjaxAction is special kind of AjaxAction that automatically stores updated form state to HTTP session after the AJAX action is processed. During handling of AJAX request, we have updated form state on the server, refreshed two form fields and and set focus back to the car brand select. We did not need to bother ourselves with the format of resulting XML response, it was automatically generated using two update TDI instructions and focusForName instruction. Markup for update instructions was generated by form renderer.

We could also specify custom JavaScript event that will fire AJAX request:

```java
new JsEventToAction<>(JsEvent.BLUR, brandChanged())
```

When JsEvent is not specified, TDI JavaScript bundle uses default firing events: Change in selects and checkboxes/radios, pressing Enter in regular text fields. You can easily change this for example to blur events (fired when the form field is left).

### Finding and running AjaxAction

Nice we have our AJAX action defined, but how it is found and executed? In Formio, TDI AJAX request comes with query parameters like:

- _infuse - identifies that this is TDI AJAX request,
- _src - name of form element that fired the JavaScript event handled by AJAX,
- _event - type of JavaScript event that was used.

Forms.findAjaxAction will help us to get the right AJAX action, we can then execute it and write the result to the response:

```java
protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	ServletRequestParams params = new ServletRequestParams(request);
	Car formState = carForm.getFormStateHandler().findFormState(params);
	FormData<Car> formData = new FormData<>(formState);
	if (params.isTdiAjaxRequest()) {
		AjaxAction<Car> action = Forms.findAjaxAction(params, carForm.definition(formState).fill(formData));
		if (action == null) {
			ServletResponses.notFound(response);
		} else {
			// Executes the action, stores the updated form state (in HTTP session) and writes 
			// resulting XML to the response with Content-Type: application/xml 
			ServletResponses.ajaxResponse(params, response, action, carForm.getFormStateHandler());
		}
	} else {
		// No (AJAX) action to process, just rendering the whole form
		renderWholeForm(request, response, formData);
	}
}
```

## Other AJAX solutions

Library can also be used within general purpose web frameworks and adapt their AJAX solutions. It is AJAX compatible in general:

- You can expose server methods/API for update of state and call these methods from the client. In Formio, state is represented by object FormData that carries edited object and its validation messages.
- On the server, form definition (typically only part of it - some nested mapping) can be filled with current data stored on the server which were updated with client data from AJAX request.
- Filled nested mapping (including its validation result) can be sent back to the client in the AJAX response and appropriate part of the page can be refreshed with this data.

Cooperating AJAX frameworks like [jQuery](https://api.jquery.com/jQuery.ajax/) can be used.

