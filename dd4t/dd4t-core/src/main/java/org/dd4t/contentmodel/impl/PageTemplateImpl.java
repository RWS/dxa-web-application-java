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
package org.dd4t.contentmodel.impl;

import java.util.Date;
import java.util.Map;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.core.util.DateUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;


public class PageTemplateImpl extends BaseRepositoryLocalItem implements
		PageTemplate, HasMetadata {

	@Element(name = "fileExtension")
	private String fileExtension;
	@ElementMap(name = "metadata", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> metadata;
	@Element(name = "lastPublishedDate", required = false)
	protected String lastPublishedDateAsString;
	@Element(name = "revisionDate", required = false)
	protected String revisionDateAsString;		

	
	@Override
	public Date getRevisionDate() {
		if (revisionDateAsString == null || revisionDateAsString.equals(""))
			return new Date();
		return DateUtils.convertStringToDate(revisionDateAsString);
	}
	
	@Override
	public void setRevisionDate(Date date) {
		this.revisionDateAsString = date.toGMTString();		
	}	
	/**
	 * Get the file extension
	 */
	@Override
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Set the file extension
	 */
	@Override
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Set the metadata
	 */
	public void setMetadata(Map<String, Field> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get the metadata as a map of fields
	 */
	public Map<String, Field> getMetadata() {
		return metadata;
	}

}
