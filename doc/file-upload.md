# File uploads

File uploads are handled transparently by the library. The only things you must specify are:

- Enctype attribute of the form element in the template: enctype="multipart/form-data".
- Properties to which the uploaded files are bound must be of type UploadedFile or collections/arrays of UploadedFile(s). Implementations of UploadedFile are serializable (can be stored in HTTP session when the form has some validation errors, so the uploaded files need not to be uploaded again).
- Remember to call deleteTempFile method on the UploadedFile instance after the file is finally processed, otherwise the temporarily stored file is deleted later when the JVM garbage collector removes no longer referenced instance of UploadedFile from the memory.

If you want to have indexed list of uploaded files with possible gaps (no file is uploaded for some indexes), you can define property of type List<UploadedFileWrapper> and define nested list mapping for it. Let's assume the property for nested list mapping has name myAttachment, field names myAttachment[0]-file, myAttachment[1]-file, ... are then available in corresponding FormFields of your filled form. UploadedFileWrapper contains property "file" of type UploadedFile. You should adhere to this convention if form field names are hardcoded in template.

File uploads are internally implemented using [Apache Commons FileUpload](http://commons.apache.org/proper/commons-fileupload/) library.

Parameters of file uploads can be configured via ServletRequestParams or PortletRequestParams class that is used to preprocess request parameters. Overloaded constructor of this class accepts the following parameterization:

- defaultEncoding - encoding of header and request parameters
- tempDir - temporary directory to store files bigger than specified size threshold; defaults to new File(System.getProperty("java.io.tmpdir"))
- sizeThreshold - maximum size of a file (in bytes) that is loaded into the memory and not yet temporarily stored to disk
- totalSizeMax - maximum allowed size of the whole request in bytes; if this limit is exceeded, library generates appropriate global validation message
- singleFileSizeMax - maximum allowed size of a single uploaded file in bytes; if this limit is exceeded, library generates appropriate form field validation message
