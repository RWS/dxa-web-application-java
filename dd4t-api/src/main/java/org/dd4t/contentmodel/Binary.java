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
public interface Binary extends PublishedItem {
	/**
	 * Get the schema used by the binary
	 * 
	 * @return the schema
	 */
	public Schema getSchema();

	/**
	 * Set the schema
	 * 
	 * @param schema
	 */
	public void setSchema(Schema schema);

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
	 * Get the alt text of the binary
	 * 
	 * @return the alt text
	 */
	public String getAlt();

	/**
	 * Set the alt text
	 * 
	 * @param alt
	 */
	public void setAlt(String alt);
	
	/**
	 * Get the mime type of the binary
	 */
	public String getMimeType();

	/**
	 * Set the mime type of the binary
	 * 
	 * @param mimeType (String)
	 */
	public void setMimeType(String mimeType);
}
