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

package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Top interface for all items in tridion.
 *
 * @author Quirijn Slings
 */
public interface Item {

    /**
     * Get the tridion id.
     *
     * @return the tridion id i.e. tcm:1-1-32
     */
    String getId ();

    /**
     * Set the id
     *
     * @param id
     */
    void setId (String id);

    /**
     * Get the title
     *
     * @return
     */
    String getTitle ();

    /**
     * Set the title
     *
     * @param title
     */
    void setTitle (String title);

    /**
     * Add a custom property
     *
     * @param key
     * @param value
     */
    void addCustomProperty (String key, Object value);

    /**
     * Get a custom property
     *
     * @param key
     * @return the property object
     */
    Object getCustomProperty (String key);

    /**
     * Get the Map of custom properties
     *
     * @return the map of custom properties
     */
    Map<String, Object> getCustomProperties ();

    /**
     * Set the map of custom properties
     *
     * @param customProperties
     */
    void setCustomProperties (Map<String, Object> customProperties);

    Map<String, FieldSet> getExtensionData ();

    void setExtensionData (Map<String, FieldSet> extensionData);
}
