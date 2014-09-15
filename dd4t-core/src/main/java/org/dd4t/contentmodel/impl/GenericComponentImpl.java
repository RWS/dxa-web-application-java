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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.HasContent;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.HasMultimedia;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.core.util.DateUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;


public class GenericComponentImpl extends BaseComponent implements
		GenericComponent, HasContent, HasMetadata, HasMultimedia {

	@ElementMap(name = "metadata", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> metadata;
	@ElementMap(name = "fields", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> content;
	@Element(name = "componentType", required = false)
	protected ComponentType componentType;
	@Element(name = "multimedia", required = false)
	private Multimedia multimedia;
	@Element(name = "lastPublishedDate", required = false)
	protected String lastPublishedDateAsString;
	@Element(name = "revisionDate", required = false)
	protected String revisionDateAsString;		
	
	@Override
	public Date getLastPublishedDate() {
		if (lastPublishedDateAsString == null || lastPublishedDateAsString.equals(""))
			return new Date();
		return DateUtils.convertStringToDate(lastPublishedDateAsString);
	}
	


	@Override
	public void setLastPublishedDate(Date date) {
		this.lastPublishedDateAsString = date.toGMTString();		
	}	
	
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
	
	@ElementList(name = "categories", required = false, type = CategoryImpl.class)
	private List<Category> categories;
        @Element(name = "version")
        protected int version;

        public int getVersion() {
            
            return version;
        }

        public void setVersion(int version) {
            
            this.version = version;
        }
        
	/**
	 * Set the metadata
	 */
	public void setMetadata(Map<String, Field> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get the metadata
	 * 
	 * @return a map of field objects representing the metadata
	 */
	public Map<String, Field> getMetadata() {
		if(metadata == null){
			metadata = new HashMap<String, Field>();
		}
		return metadata;
	}

	/**
	 * Set the content
	 */
	public void setContent(Map<String, Field> content) {
		this.content = content;
	}

	/**
	 * Get the content
	 * 
	 * @return a map of field objects representing the content
	 */
	public Map<String, Field> getContent() {
		if(content == null){
			content = new HashMap<String, Field>();
		}		return content;
	}

	/**
	 * Get the component type
	 * 
	 * @return the component type
	 */
	public ComponentType getComponentType() {
		return componentType;
	}

	/**
	 * Set the component type
	 * 
	 * @param componentType
	 */
	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	/**
	 * Get the multimedia object
	 * 
	 * @return the multimedia object
	 */
	@Override
	public Multimedia getMultimedia() {
		return multimedia;
	}

	/**
	 * Set the multimedia object
	 */
	@Override
	public void setMultimedia(Multimedia multimedia) {
		this.multimedia = multimedia;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}
}