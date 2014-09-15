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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a component where the xml source from the SimpleComponent
 * has been deserialized into content and metadata.
 * 
 * @author bjornl
 * 
 */
public interface GenericComponent extends Component {

	public enum ComponentType {
		Normal, Multimedia
	}

	/**
	 * Set the metadata
	 */
	public void setMetadata(Map<String, Field> metadata);
	/**
	 * Get the metadata
	 * 
	 * @return a map of field objects representing the metadata
	 */
	public Map<String, Field> getMetadata();

	/**
	 * Set the content
	 */
	public void setContent(Map<String, Field> content);

	/**
	 * Get the content
	 * 
	 * @return a map of field objects representing the content
	 */
	public Map<String, Field> getContent();

	/**
	 * Get the component type
	 * 
	 * @return the component type
	 */
	public ComponentType getComponentType();

	/**
	 * Set the component type
	 * 
	 * @param componentType
	 */
	public void setComponentType(ComponentType componentType);

	/**
	 * Get the multimedia object
	 * 
	 * @return the multimedia object
	 */
	public Multimedia getMultimedia();

	/**
	 * Set the multimedia object
	 */
	public void setMultimedia(Multimedia multimedia);

	public void setCategories(List<Category> categories);

	public List<Category> getCategories();
	
	public int getVersion();
	

	Date getLastPublishedDate();

	void setLastPublishedDate(Date date);

	Date getRevisionDate();

	void setRevisionDate(Date date);	
}