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

import java.util.List;

public interface Field {

	public enum FieldType {
		Text, MultiLineText, Xhtml, Keyword, Embedded, MultiMediaLink, ComponentLink, ExternalLink, Number, Date
	}

	/**
	 * Get the values of the field.
	 * 
	 * @return a list of objects, where the type is depending of the field type.
	 *         Never returns null.
	 */
	public List<Object> getValues();
	
	/**
	 * Get the name of the field.
	 * 
	 * @return the name of the field
	 */
	public String getName();

	/**
	 * Set the name of the field
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * Get the xPath of the field (used for SiteEdit)
	 * 
	 * @return the xPath of the field
	 */
	public String getXPath();

	/**
	 * Set the xPath of the field (used for SiteEdit)
	 * 
	 * @param name
	 */
	public void setXPath(String xPath);
	/**
	 * Set the field type
	 * 
	 * @param fieldType
	 */
	public void setFieldType(FieldType fieldType);

	/**
	 * Get the field type
	 * 
	 * @return the field type
	 */
	public FieldType getFieldType();
}