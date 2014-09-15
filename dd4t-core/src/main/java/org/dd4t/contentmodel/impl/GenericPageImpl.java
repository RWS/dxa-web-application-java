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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.StructureGroup;
import org.dd4t.core.util.DateUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;


@Root(name = "page")
public class GenericPageImpl extends BasePage implements GenericPage, HasMetadata {

	@ElementMap(name = "metadata", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> metadata;

	@ElementList(name = "categories", required = false, type = CategoryImpl.class)
	private List<Category> categories;

	@Element(name = "fileName") 
	protected String fileName;
	@Element(name = "pageTemplate")
	protected PageTemplate pageTemplate;
	@ElementList(name = "componentPresentations", required = false)
	protected List<ComponentPresentation> componentPresentations;
	@Element(name = "structureGroup", required = false)
	protected StructureGroup structureGroup;
	@Element(name = "version")
	protected int version;
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
	 * Get the metadata as a map of fields
	 */
	public Map<String, Field> getMetadata() {
		if(metadata == null){
			metadata = new HashMap<String, Field>();
		}
		return metadata;
	}
	
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}
	public PageTemplate getPageTemplate() {
		return pageTemplate;
	}

	public void setPageTemplate(PageTemplate pageTemplate) {
		this.pageTemplate = pageTemplate;
	}

	public List<ComponentPresentation> getComponentPresentations() {
		if(componentPresentations == null){
			componentPresentations = new ArrayList<ComponentPresentation>();
		}
		return componentPresentations;
	}

	public void setComponentPresentations(
			List<ComponentPresentation> componentPresentations) {
		this.componentPresentations = componentPresentations;
	}

	/**
	 * Get the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Set the file extension. It sets the file extension on the page template
	 * because that is were the extension is determined.
	 */
	public void setFileExtension(String fileExtension) {
		if (this.getPageTemplate() != null) {
			this.getPageTemplate().setFileExtension(fileExtension);
		}
	}

	/**
	 * Get the file extension of the page (this is actually determined by the
	 * page template but also set here for clarity).
	 */
	public String getFileExtension() {
		if (this.getPageTemplate() != null) {
			return this.getPageTemplate().getFileExtension();
		} else {
			return "";
		}
	}

	public StructureGroup getStructureGroup() {
		return structureGroup;
	}

	public void setStructureGroup(StructureGroup structureGroup) {
		this.structureGroup = structureGroup;
	}


}