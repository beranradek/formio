# Best practices in forms creation

If you want to build a form (not only in Java), you should be aware of the following rules that can make the user experience with your forms better or just protect your system from possible issues. Remember, the forms are input gates to your application and these should be protected carefully.

## Do not preselect values for mandatory fields

You should display first empty option that selects nothing. Otherwise the user can inadvertently confirm the preselected value and there need not to be a way back. User should select the mandatory value consciously. If no value is selected, there should be of course an appropriate validation message displayed.

## Validate the inputs appropriately

The inputs should ideally be validated and a feedback returned back to the user. Also errors from conversion of the input strings to concrete data types should be translated to validation messages. If your application is monitored, errors from the invalid inputs can easily cause unnecessary panic or just make your logs difficult to read. If you do not implement appropriate validation, you should at least choose lower logging priority such as a warning. You can also return HTTP status 400 (Bad Request) if your user is not a real person but a cooperating system that is posting the data.

## Ensure good visibility of validation messages

Validation messages should be easily visible, best beside the fields that contain invalid inputs. There should be also a clearly visible area with global validation messages on the eyes of the user when the invalid form is submitted and the page reloaded.

## Distinguish readonly and disabled fields

A readonly field is not editable, but behaves like any other field - gets sent when the form is submitted and can be validated. A readonly field can get a focus while a disabled cannot. A disabled field is not editable and is not sent on the form submission and should not be validated.

## Redirect to another page after form submission

Otherwise the user can submit the form which he still sees more than once.

## Disable submit button after the user clicks it

To avoid multiple clicks on form submit button and duplicate submissions.

## Communicate successful form submission

This makes user more confident with done action.

## Make main submission button clearly visible

So the user can see the final required step easily. Differentiate other form actions using another design of buttons (size, colors).

## Limit maximum size of a request

Otherwise an uninformed user can upload very large files and cause unexpected processing or even site availability issues.

## Delete temporarily uploaded files

As soon as the uploaded files are finally processed after a successful form submission, the temporary files used during processing of file upload should be deleted.

## Escape strings that come from the users

This is necessary to protect your system from the malicious inputs. The issues can be caused when the values entered by the users are directly printed on the website ([Cross-Site Scripting](http://en.wikipedia.org/wiki/Cross-Site_Scripting)) or passed as the parameters to the database queries ([SQL Injection](http://en.wikipedia.org/wiki/SQL_Injection)) without any protection. The input must not be interpreted as valid or invalid markup elements or keywords of a database query.

## Protect all important forms against CSRF attacks

[Cross-site request forgery (CSRF or XSRF)](http://cs.wikipedia.org/wiki/Cross-site_request_forgery) is one of the most common web applications vulnerabilities. It can result in remote code execution with privileges of an application user and damage user's data - for example influence his financial transactions.

The attacker can explore URLs and request bodies that change state of your application. User can click on some unrelated malicious link in the attacker's site or e-mail while his browser still holds a valid session to your application (that is used for example in another browser tab) and the action of attacker's link can execute a request targeted to your application under the privileges of logged user with his active session.

## Consider using captcha for important publicly accessible forms

For example for a registration form. If you have some sofisticated spam filter behind the scene, it is better not to use captcha at all because it is difficult to fill it in and it can discourage the user from filling the form. You can also use some other protection techniques, such as requiring some hidden (or visually hidden) field to remain blank (unfilled). The field can have an attractive name such as "comment".

Or use CSRF protection that could be overcomed (for the purpose of captcha protection) only when the spam bot itself loads the form page with a protection token in a hidden form field before it crafts the form submission, and then fills and submits the form while preserving the token unchanged (generic malicious form submission cannot be prepared in advance or tried blindly, but must be constructed knowingly in the fly). This can be combined with a spam filter.

## Use HTTPS protocol when collecting sensitive information

HTTPS uses SSL secured (encrypted) data transfer.

## Use helpful form library

Use some library that solves common problems for you and makes construction of the forms an easy task. Formio library ensures easy to use bean validation, file uploads, allows to limit maximum size of a request or of one uploaded file, allows quick deletion of temporary data after processing of file uploads and can protect your forms against CSRF attacks.
