package org.twinstone.formio;

import org.twinstone.formio.upload.RequestProcessingError;
import org.twinstone.formio.upload.UploadedFile;

/**
 * Provides values for form request parameters.
 * @author Radek Beran
 */
public interface ParamsProvider {
	
	/**
	 * Returns {@code null} if parameter with given name does not exist;
	 * empty array, if the parameter is known but no values are specified;
	 * array of values otherwise.
	 * @param paramName
	 * @return
	 */
	String[] getParamValues(String paramName);
	
	/**
	 * Returns {@code null} if parameter with given name does not exist or no file was uploaded;
	 * uploaded file otherwise.
	 * @param paramName
	 * @return
	 */
	UploadedFile[] getUploadedFiles(String paramName);
	
	/**
	 * Returns serious error that was cought when processing the request, or {@code null} if there was no error.
	 * @return
	 */
	RequestProcessingError getRequestError();
}
