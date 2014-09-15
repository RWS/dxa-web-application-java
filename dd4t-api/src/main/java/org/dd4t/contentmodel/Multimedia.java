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
package org.dd4t.contentmodel;

import com.tridion.data.BinaryData;

/**
 * Interface for a binary items i.e. images and pdfs.
 * 
 * @author bjornl
 * 
 */
public interface Multimedia {

	/**
	 * Return the binary data
	 * 
	 * @return the tridion native binary data
	 */
	public BinaryData getBinaryData();

	/**
	 * Set the binary data
	 * 
	 * @param binaryData
	 */
	public void setBinaryData(BinaryData binaryData);

	/**
	 * Get the height of the binary
	 * 
	 * @return the height
	 */
	public int getHeight();

	/**
	 * Set the height of the binary
	 * 
	 * @param height
	 */
	public void setHeight(int height);

	/**
	 * Get the width of the binary
	 * 
	 * @return
	 */
	public int getWidth();

	/**
	 * Set the width of the binary
	 * 
	 * @param width
	 */
	public void setWidth(int width);

	/**
	 * Get the size of the binary
	 * 
	 * @return Size in bytes
	 */
	public int getSize();

	/**
	 * Set the size of the binary
	 * 
	 * @param Size
	 *            in bytes
	 */
	public void setSize(int size);

	/**
	 * Get the alt text of the binary
	 * 
	 * @return the alt text
	 */
	public String getAlt();

	/**
	 * Set the al text
	 * 
	 * @param alt
	 */
	public void setAlt(String alt);

	/**
	 * Get the URL of the binary
	 * 
	 * @return the URL
	 */
	public String getUrl();

	/**
	 * Set the URL
	 * 
	 * @param URL
	 */
	public void setUrl(String url);

	/**
	 * Get the mime type of the binary
	 * 
	 * @return the mime type
	 */
	public String getMimeType();

	/**
	 * Set the mime type
	 * 
	 * @param mime
	 *            type
	 */
	public void setMimeType(String mimeType);

	/**
	 * Get the file extension
	 * 
	 * @return the file extension
	 */
	public String getFileExtension();

	/**
	 * Set the file extension
	 * 
	 * @param fileExtension
	 */
	public void setFileExtension(String fileExtension);

	/**
	 * Get the file name
	 * 
	 * @return the file name
	 */
	public String getFileName();

	/**
	 * Set the file name
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName);

}
