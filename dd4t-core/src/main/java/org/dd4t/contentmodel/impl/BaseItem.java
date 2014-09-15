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

import org.dd4t.contentmodel.Item;
import org.simpleframework.xml.Element;


/**
 * Base class for all tridion items
 * 
 * @author Quirijn Slings
 * 
 */
public abstract class BaseItem implements Item {

	@Element(name = "id")
	private String id;
	@Element(name = "title")
	private String title;


	private HashMap<String, Object> customProperties = new HashMap<String, Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<String, Object> getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(HashMap<String, Object> customProperties) {
		this.customProperties = customProperties;
	}
	
	public void addCustomProperty(String key, Object value){
		customProperties.put(key, value);
	}
	
	public Object getCustomProperty(String key){
		return customProperties.get(key);
	}
}