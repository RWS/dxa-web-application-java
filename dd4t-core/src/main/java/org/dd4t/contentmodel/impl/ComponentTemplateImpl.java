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

import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.util.DateUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;


public class ComponentTemplateImpl extends BaseRepositoryLocalItem implements ComponentTemplate {
	
	@Element(name = "outputFormat", required = false)
	private String outputFormat;
	@ElementMap(name = "metadata", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> metadata;
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
	@Override
	public Map<String, Field> getMetadata() {
		return this.metadata;
	}

	@Override
	public void setMetadata(Map<String, Field> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get the output format
	 * @return 
	 */
	public String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * Set the output format
	 * @param outputFormat
	 */
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}	
}
