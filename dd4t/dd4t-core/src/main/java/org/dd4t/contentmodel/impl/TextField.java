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

import java.util.LinkedList;
import java.util.List;

import org.dd4t.contentmodel.Field;
import org.simpleframework.xml.Attribute;


public class TextField extends BaseField implements Field {
	@Attribute(required = false)
	private String categoryId;
	@Attribute(required = false)
	private String categoryName;

	public TextField(){
		setFieldType(FieldType.Text);
	}
	
	@Override
	public List<Object> getValues() {
		List<String> textValues = getTextValues();
		List<Object> l = new LinkedList<Object>();
		for (String s : textValues) {
			l.add(s);
		}
		return l;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

}
