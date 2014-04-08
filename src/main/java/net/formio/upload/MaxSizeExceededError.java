/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.upload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Some maximum allowed size was exceeded.
 * @author Radek Beran
 */
public class MaxSizeExceededError extends RequestProcessingError {
	private static final long serialVersionUID = -887092648112789389L;
	private final long currentSize;
	private final long maxSize;

	public MaxSizeExceededError(String message, Throwable cause, long currentSize, long maxSize) {
		super(message, cause);
		this.currentSize = currentSize;
		this.maxSize = maxSize;
	}
	
	/**
	 * Message parameters for translation file.
	 * @return
	 */
	@Override
	public Map<String, Serializable> getMessageParameters() {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("currentSize", humanReadableByteCount(getCurrentSize()));
		params.put("maxSize", humanReadableByteCount(getMaxSize()));
		return params;
	}

	/**
	 * Actual size.
	 * @return
	 */
	public long getCurrentSize() {
		return currentSize;
	}

	/**
	 * Maximum allowed size.
	 * @return
	 */
	public long getMaxSize() {
		return maxSize;
	}
	
	/**
	 * Returns human readable form of byte count.
	 * @param bytes
	 * @return
	 */
	protected String humanReadableByteCount(long bytes) {
	    int unit = 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = "KMGTPE".charAt(exp-1) + "";
	    return String.format("%.1f %sB", Double.valueOf(bytes / Math.pow(unit, exp)), pre);
	}

}
