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
import org.dd4t.contentmodel.*;

import java.util.HashMap;
import java.util.Map;

public class ComponentImpl extends BaseComponent implements GenericComponent, HasContent, HasMetadata, HasMultimedia {

    @JsonProperty ("ComponentType")
    @JsonDeserialize (as = ComponentImpl.ComponentType.class)
    protected ComponentType componentType;

    @JsonProperty ("Fields")
    @JsonDeserialize (contentAs = BaseField.class)
    private Map<String, Field> content;

    @JsonProperty ("Multimedia")
    @JsonDeserialize (as = MultimediaImpl.class)
    private Multimedia multimedia;

    @JsonProperty ("EclId")
    @JsonDeserialize (as = String.class)
    private String eclId;

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    @Override
    public Map<String, Field> getContent () {
        if (content == null) {
            content = new HashMap<String, Field>();
        }
        return content;
    }

    /**
     * Set the content
     */
    @Override
    public void setContent (Map<String, Field> content) {
        this.content = content;
    }

    /**
     * Get the component type
     *
     * @return the component type
     */
    @Override
    public ComponentType getComponentType () {
        return componentType;
    }

    /**
     * Set the component type
     *
     * @param componentType
     */
    @Override
    public void setComponentType (ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * Get the multimedia object
     *
     * @return the multimedia object
     */
    @Override
    public Multimedia getMultimedia () {
        return multimedia;
    }

    /**
     * Set the multimedia object
     */
    @Override
    public void setMultimedia (Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    @Override
    public String getEclId () {
        return this.eclId;
    }

    public void setEclId (String eclId) {
        this.eclId = eclId;
    }
}