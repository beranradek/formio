/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.utils;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;

/**
 * Utilities for tests.
 * @author Radek Beran
 */
public class TestUtils {

	/**
	 * Opens given URL in default browser of operating system.
	 * @param uri
	 */
	public static void openInBrowser(String uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(uri));
	            } catch (Exception ex) {
	            	ex.printStackTrace();
	            }
			}
		}
	}
	
	/**
	 * Saves given text content to given file.
	 * @param file
	 * @param content
	 * @param encoding
	 * @throws IOException
	 */
	public static void saveContentToTextFile(File file, String content, String encoding) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), encoding));
			bw.write(content);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (bw != null) {
				try {
					bw.flush();
					bw.close();
				} catch (IOException ignored) {
					// ignored
				}
			}
		}
	}
	
	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
	
	private TestUtils() {
	}
}
