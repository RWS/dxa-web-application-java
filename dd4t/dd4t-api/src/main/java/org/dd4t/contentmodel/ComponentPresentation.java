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


public interface ComponentPresentation {

	/**
	 * Get the component
	 * 
	 * @return
	 */
	public GenericComponent getComponent();

	/**
	 * Set the component
	 * 
	 * @param component
	 */
	public void setComponent(GenericComponent component);

	/**
	 * Get the component template
	 * 
	 * @return
	 */
	public ComponentTemplate getComponentTemplate();

	/**
	 * Set the component template
	 * 
	 * @param componentTemplate
	 */
	public void setComponentTemplate(ComponentTemplate componentTemplate);

	/**
	 * Set the rendered content
	 * 
	 * @param renderedContent
	 */
	public void setRenderedContent(String renderedContent);

	/**
	 * Get the rendered content
	 */ 
	public String getRenderedContent();
	

	/**
	 * Return true if the component presentation is dynamic (i.e. available in the broker database as a separate item)
	 * @return
	 */
	public boolean isDynamic();
}
