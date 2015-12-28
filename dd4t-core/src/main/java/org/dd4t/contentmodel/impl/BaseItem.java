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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all tridion items
 * <p/>
 * Note: The latest DD4T version has uppercase start characters in elements.
 *
 * @author Quirijn Slings, Raimond Kempees
 */
public abstract class BaseItem implements Item {

    @JsonProperty ("Id")
    private String id;

    @JsonProperty ("Title")
    private String title;

    @JsonProperty ("CustomProperties")
    private Map<String, Object> customProperties = new HashMap<>();

    @JsonProperty (value = "ExtensionData", required = false)
    @JsonDeserialize (contentAs = FieldSetImpl.class)
    private Map<String, FieldSet> extensionData;

    @Override
    public String getId () {
        return id;
    }

    @Override
    public void setId (String id) {
        this.id = id;
    }

    @Override
    public String getTitle () {
        return title;
    }

    @Override
    public void setTitle (String title) {
        this.title = title;
    }

    @Override
    public Map<String, Object> getCustomProperties () {
        return customProperties;
    }

    @Override
    public void setCustomProperties (Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public void addCustomProperty (String key, Object value) {
        customProperties.put(key, value);
    }

    @Override
    public Object getCustomProperty (String key) {
        return customProperties.get(key);
    }

    @Override
    public Map<String, FieldSet> getExtensionData () {
        return this.extensionData;
    }

    @Override
    public void setExtensionData (Map<String, FieldSet> extensionData) {
        this.extensionData = extensionData;
    }
}