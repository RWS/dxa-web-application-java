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

import java.util.HashMap;
import java.util.Map;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.Keyword;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;


public class KeywordImpl extends BaseItem implements Keyword, HasMetadata {
	@Attribute(name = "key", required = false)
	private String key;

	@Attribute(name = "description", required = false)
	private String description;

	@Attribute(name = "taxonomyId")
	private String taxonomyId;
	
	@Attribute(name = "path")
	private String path;

	@ElementMap(name = "metadata", keyType = String.class, valueType = Field.class, entry = "item", required = false)
	private Map<String, Field> metadata;

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String getTaxonomyId() {
		return taxonomyId;
	}

	@Override
	public void setTaxonomyId(String taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Set the metadata
	 */
	@Override
	public void setMetadata(Map<String, Field> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get the metadata as a map of fields
	 */
	@Override
	public Map<String, Field> getMetadata() {
		if(metadata == null){
			metadata = new HashMap<String, Field>();
		}
		return metadata;
	}

}
