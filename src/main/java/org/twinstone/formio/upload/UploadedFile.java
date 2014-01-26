package org.twinstone.formio.upload;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

/**
 * Data of uploaded file.
 * @author Radek Beran
 */
public interface UploadedFile {

	/**
	 * Returns data of the file.
	 * @return
	 * @throws IOException
	 */
	ReadableByteChannel getContent() throws IOException;
	
	/**
	 * Deletes uploaded file from temporary directory
	 * (should be called after the file is processed/stored
	 * permanently).
	 */
	void deleteTempFile();
	
	/**
	 * Returns the file name.
	 * @return String
	 */
	String getFileName();

	/**
	 * Returns the MIME content type of underlying data.
	 * @return String
	 */
	String getContentType();

	/**
	 * Returns the size of underlying data in bytes.
	 * @return long
	 */
	long getSize();
}
