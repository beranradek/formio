package org.twinstone.formio.upload;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.fileupload.FileItem;

/**
 * Implementation of {@link UploadedFile} that uses {@link FileItem} from commons-fileupload library.
 * @author Radek Beran
 */
public class RequestUploadedFile extends AbstractUploadedFile {
	private static final long serialVersionUID = 4928481456790370482L;
	protected FileItem fileItem;

	public RequestUploadedFile(String fileName, String contentType, long size, FileItem fileItem) {
		super(fileName, contentType, size);
		this.fileItem = fileItem;
	}
	
	@Override
	public ReadableByteChannel getContent() throws IOException {
		assertNotCleared();
		return Channels.newChannel(fileItem.getInputStream());
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		deleteTempFile();
	}

	@Override
	public void deleteTempFile() {
		if (fileItem != null) {
			fileItem.delete();
			fileItem = null;
		}
	}
	
	@Override
	public String toString() {
		return "File " + getFileName() + ", size=" + getSize()+", type=" + getContentType();
	}
	
	private void assertNotCleared() {
		if (fileItem == null) throw new IllegalStateException("file item has been already cleared");
	}

}
