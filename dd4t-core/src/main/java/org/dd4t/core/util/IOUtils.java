/**
 *  Copyright 2011 Capgemini & SDL
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.core.util;

import org.springframework.core.io.FileSystemResource;

import java.io.*;

public class IOUtils {

	private IOUtils(){

	}

	public static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static String convertFileToString(String filePath) {
		FileSystemResource resource = new FileSystemResource(filePath);
		File file = resource.getFile();
		byte[] buffer = new byte[(int) file.length()];

		try {
			resource.getInputStream().read(buffer);
			resource.getInputStream().close();
			return new String(buffer);
		} catch (IOException e) {
			return null;
		}
	}
}
