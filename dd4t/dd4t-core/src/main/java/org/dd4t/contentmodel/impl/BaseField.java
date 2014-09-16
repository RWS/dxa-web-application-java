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

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Keyword;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;


public abstract class BaseField implements Field {
	@Element(name = "name")
	private String name;
	@ElementList(name = "textValues", required = false)
	private List<String> textValues;
	@ElementList(name = "numericValues", required = false)
	private List<Double> numericValues;
	@ElementList(name = "dateTimeValues", required = false)
	private List<String> dateValues;
	@ElementList(name = "linkedComponentValues", required = false)
	private List<Component> componentLinkValues;
	@ElementList(name = "keywords", type = KeywordImpl.class, required = false)
	private List<Keyword> keywordValues;	
	@ElementList(name = "embeddedValues", type = FieldSetImpl.class, required = false)
	private List<FieldSet> embeddedValues;
	@Attribute(required = false)
	private FieldType fieldType;
	@Attribute(required = false)
	private String xPath;

	/**
	 * Note: it seems like simplexml does this automatically so it is not needed.
	 * This method is executed after deserialization and unescapes all text values.
	 */
	/*@Commit
	public void unescapeTextValues() {
		if (FieldType.Text.equals(fieldType)) {
			List<String> textValuesList = getTextValues();
			List<String> unescapedList = new LinkedList<String>();
			for (String textValue : textValuesList) {
				unescapedList.add(StringEscapeUtils.unescapeXml(textValue));
			}
			this.setTextValues(unescapedList);
		}
	}*/

	/**
	 * Get the values of the field.
	 * 
	 * @return a list of objects, where the type is depending of the field type.
	 *         Never returns null.
	 */
	public abstract List<Object> getValues();

	/**
	 * Get the numeric field values
	 * 
	 * @return numericValues
	 */	
	public List<Double> getNumericValues() {
		return numericValues != null ? numericValues : new LinkedList<Double>();
	}

	/**
	 * Set the numeric field values
	 * 
	 * @param numericValues
	 */
	public void setNumericValues(List<Double> numericValues) {
		this.numericValues = numericValues;
	}

	/**
	 * Get the date time values
	 * 
	 * @return
	 */
	public List<String> getDateTimeValues() {
		return dateValues != null ? dateValues : new LinkedList<String>();
	}

	/**
	 * Set the date time field values
	 * 
	 * @param dateTimeValues
	 */
	public void setDateTimeValues(List<String> dateTimeValues) {
		this.dateValues = dateTimeValues;
	}
	
	

	/**
	 * Get the linked component values
	 * 
	 * @return
	 */
	public List<Component> getLinkedComponentValues() {
		return componentLinkValues != null ? componentLinkValues
				: new LinkedList<Component>();
	}

	/**
	 * Set the linked component field values
	 * 
	 * @param linkedComponentValues
	 */
	public void setLinkedComponentValues(List<Component> linkedComponentValues) {
		this.componentLinkValues = linkedComponentValues;
	}

	/**
	 * Get the name of the field.
	 * 
	 * @return the name of the field
	 */
	public String getName() {
		return name != null ? name : "";
	}

	/**
	 * Set the name of the field
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get xPath value for the field
	 * @return
	 */
	@Override
	public String getXPath() {
		return xPath;
	}

	/**
	 * Set xPath value for the field
	 * @param xPath
	 */
	@Override
	public void setXPath(String xPath) {
		this.xPath = xPath;		
	}
	
	/**
	 * Set the field type
	 * 
	 * @param fieldType
	 */
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * Get the field type
	 * 
	 * @return the field type
	 */
	public FieldType getFieldType() {
		return fieldType;
	}

	/**
	 * Set the text field values
	 * 
	 * @param testValues
	 */
	public void setTextValues(List<String> textValues) {
		this.textValues = textValues;
	}

	/**
	 * Get the text field values
	 * 
	 * @return a list of text values
	 */
	public List<String> getTextValues() {
		return textValues != null ? textValues : new LinkedList<String>();
	}

	/**
	 * Get the embedded values
	 * @return the embedded values as a map
	 */
	public List<FieldSet> getEmbeddedValues() {
		return embeddedValues  != null ? embeddedValues : new LinkedList<FieldSet>();
	}

	/**
	 * Set the embedded values
	 * @param embeddedValues
	 */
	public void setEmbeddedValues(List<FieldSet>  embeddedValues) {
		this.embeddedValues = embeddedValues;
	}
	
	public List<Keyword> getKeywordValues(){
		return keywordValues != null ? keywordValues : new LinkedList<Keyword>();
	}
	
	public void setKeywordvalues(List<Keyword> keywordValues){
		this.keywordValues = keywordValues;
	}
}