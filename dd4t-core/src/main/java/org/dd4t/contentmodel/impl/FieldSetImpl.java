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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Schema;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Embedded fields basically are an array of
 * ambiguous and unknown keynames, with multiple Fields in them
 */
public class FieldSetImpl implements FieldSet {

    private static final Logger LOG = LoggerFactory.getLogger(FieldSetImpl.class);

    private final Map<String, Object> rawContent = new HashMap<>();
    @JsonIgnore
    private Map<String, Field> content = new HashMap<>();

    @JsonProperty ("Schema")
    private Schema schema;

    @Override
    public Schema getSchema () {
        return schema;
    }

    @Override
    public void setSchema (Schema schema) {
        this.schema = schema;
    }

    @JsonAnyGetter
    public Map<String, Object> getRawContent () {
        return rawContent;
    }

    @JsonAnySetter
    public void set (String fieldKey, JsonNode embeddedField) {

        try {
            // The basefield annotations will map the fields to concrete types
            BaseField b = JsonDataBinder.getGenericMapper().readValue(embeddedField.toString(), BaseField.class);
            content.put(fieldKey, b);
        } catch (IOException e) {
            LOG.error("Error deserializing FieldSet.", e);
        }

        rawContent.put(fieldKey, embeddedField);
    }

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    @Override
    public Map<String, Field> getContent () {
        return content;
    }

    /**
     * Set the content
     */
    @Override
    public void setContent (Map<String, Field> content) {
        this.content = content;
    }
}
