/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.serializers.impl.json.FieldTypeConverter;

import java.util.LinkedList;
import java.util.List;


@JsonIgnoreProperties (value = {"Value"}, ignoreUnknown = true)
public abstract class BaseField implements Field {

    @JsonProperty ("Name")
    private String name;

    @JsonProperty ("Values")
    private List<String> textValues;

    @JsonProperty ("NumericValues")
    private List<Double> numericValues;

    @JsonProperty ("DateTimeValues")
    private List<String> dateValues;

    @JsonProperty ("LinkedComponentValues")
    @JsonDeserialize (contentAs = ComponentImpl.class)
    private List<Component> componentLinkValues;

    @JsonDeserialize (contentAs = KeywordImpl.class)
    private List<Keyword> keywordValues;

    @JsonProperty ("EmbeddedValues")
    @JsonDeserialize (contentAs = FieldSetImpl.class)
    private List<FieldSet> embeddedValues;

    @JsonProperty ("FieldType")
    @JsonDeserialize (converter = FieldTypeConverter.class)
    private FieldType fieldType;

    @JsonProperty ("XPath")
    private String xPath;

    /**
     * Get the values of the field.
     *
     * @return a list of objects, where the type is depending of the field type.
     * Never returns null.
     */
    @Override
    public abstract List<Object> getValues ();

    /**
     * Get the numeric field values
     *
     * @return numericValues
     */
    public List<Double> getNumericValues () {
        return numericValues == null ? new LinkedList<Double>() : numericValues;
    }

    /**
     * Set the numeric field values
     *
     * @param numericValues
     */
    public void setNumericValues (List<Double> numericValues) {
        this.numericValues = numericValues;
    }

    /**
     * Get the date time values
     *
     * @return
     */
    public List<String> getDateTimeValues () {
        return dateValues == null ? new LinkedList<String>() : dateValues;
    }

    /**
     * Set the date time field values
     *
     * @param dateTimeValues
     */
    public void setDateTimeValues (List<String> dateTimeValues) {
        this.dateValues = dateTimeValues;
    }

    /**
     * Get the linked component values
     *
     * @return
     */
    public List<Component> getLinkedComponentValues () {
        return componentLinkValues != null ? componentLinkValues : new LinkedList<Component>();
    }

    /**
     * Set the linked component field values
     *
     * @param linkedComponentValues
     */
    public void setLinkedComponentValues (List<Component> linkedComponentValues) {
        this.componentLinkValues = linkedComponentValues;
    }

    /**
     * Get the name of the field.
     *
     * @return the name of the field
     */
    @Override
    public String getName () {
        return name == null ? "" : name;
    }

    /**
     * Set the name of the field
     *
     * @param name
     */
    @Override
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Get xPath value for the field
     *
     * @return
     */
    @Override
    public String getXPath () {
        return xPath;
    }

    /**
     * Set xPath value for the field
     *
     * @param xPath
     */
    @Override
    public void setXPath (String xPath) {
        this.xPath = xPath;
    }

    /**
     * Get the field type
     *
     * @return the field type
     */
    @Override
    public FieldType getFieldType () {
        return fieldType;
    }

    /**
     * Set the field type
     *
     * @param fieldType
     */
    @Override
    public void setFieldType (FieldType fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Get the text field values
     *
     * @return a list of text values
     */
    public List<String> getTextValues () {
        return textValues == null ? new LinkedList<String>() : textValues;
    }

    /**
     * Set the text field values
     *
     * @param textValues
     */

    public void setTextValues (List<String> textValues) {
        this.textValues = textValues;
    }

    /**
     * Get the embedded values
     *
     * @return the embedded values as a map
     */
    public List<FieldSet> getEmbeddedValues () {
        return embeddedValues != null ? embeddedValues : new LinkedList<FieldSet>();
    }

    /**
     * Set the embedded values
     *
     * @param embeddedValues embedded values as a map
     */
    public void setEmbeddedValues (List<FieldSet> embeddedValues) {
        this.embeddedValues = embeddedValues;
    }

    public List<Keyword> getKeywordValues () {
        return keywordValues != null ? keywordValues : new LinkedList<Keyword>();
    }

    @JsonSetter ("Keywords")
    public void setKeywords (List<Keyword> keywordValues) {
        this.keywordValues = keywordValues;
    }

    // DD4T 2.0.2 template support
    @JsonSetter ("KeywordValues")
    private void setKeywordValues (List<Keyword> keywordValues) {
        this.keywordValues = keywordValues;
    }

}