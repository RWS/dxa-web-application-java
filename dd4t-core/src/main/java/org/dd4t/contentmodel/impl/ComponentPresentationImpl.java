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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.core.databind.BaseViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Tridion Component Presentation object.
 *
 * @author bjornl, rai, sdl
 */
public class ComponentPresentationImpl implements ComponentPresentation {

    @JsonProperty ("Component")
    @JsonDeserialize (as = ComponentImpl.class)
    private Component component;

    @JsonProperty ("ComponentTemplate")
    @JsonDeserialize (as = ComponentTemplateImpl.class)
    private ComponentTemplate componentTemplate;

    @JsonProperty (value = "ExtensionData", required = false)
    @JsonDeserialize (contentAs = FieldSetImpl.class)
    private Map<String, FieldSet> extensionData;

    @JsonProperty ("IsDynamic")
    private boolean isDynamic;

    @JsonProperty ("RenderedContent")
    private String renderedContent;

    @JsonProperty ("OrderOnPage")
    private int orderOnPage;

    @JsonIgnore
    private Map<String, BaseViewModel> baseViewModels;

    @JsonIgnore
    private String rawComponentContent;

    /**
     * Get the component
     *
     * @return the component
     */
    @Override
    public Component getComponent () {
        return component;
    }

    /**
     * Set the component
     *
     * @param component
     */
    @Override
    public void setComponent (Component component) {
        this.component = component;
    }

    /**
     * Get the component template
     *
     * @return the component template
     */
    @Override
    public ComponentTemplate getComponentTemplate () {
        return componentTemplate;
    }

    /**
     * Set the component template
     *
     * @param componentTemplate
     */
    @Override
    public void setComponentTemplate (ComponentTemplate componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

    @Override
    public String getRenderedContent () {
        return renderedContent;
    }

    @Override
    public void setRenderedContent (String renderedContent) {
        this.renderedContent = renderedContent;
    }

    @Override
    public boolean isDynamic () {
        return isDynamic;
    }

    @Override
    public void setIsDynamic (final boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public int getOrderOnPage () {
        return orderOnPage;
    }

    @Override
    public void setOrderOnPage (final int orderOnPage) {
        this.orderOnPage = orderOnPage;
    }

    @Override
    public Map<String, BaseViewModel> getAllViewModels () {
        if (this.baseViewModels == null) {
            return new HashMap<>();
        }
        return this.baseViewModels;
    }

    @Override
    public void setViewModel (final Map<String, BaseViewModel> models) {
        this.baseViewModels = models;
    }

    @Override
    public BaseViewModel getViewModel (String key) {
        if (this.baseViewModels != null && this.baseViewModels.containsKey(key)) {
            return this.baseViewModels.get(key);
        }
        return null;
    }

    /**
     * Sets the raw component content form the broker.
     * Needed to build DCP strong models and to
     * have all needed meta information on the CP and CT.
     *
     * @param rawComponentContent the Json or XML Component String from the broker.
     */
    @Override
    public void setRawComponentContent (final String rawComponentContent) {
        this.rawComponentContent = rawComponentContent;
    }

    @Override
    public String getRawComponentContent () {
        return this.rawComponentContent;
    }

    @Override
    public Map<String, FieldSet> getExtensionData () {
        return extensionData;
    }

    @Override
    public void setExtensionData (final Map<String, FieldSet> extensionData) {
        this.extensionData = extensionData;
    }


}
