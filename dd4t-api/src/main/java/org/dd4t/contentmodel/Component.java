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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Interface for all components
 *
 * @author bjornl
 */
public interface Component extends RepositoryLocalItem {

    /**
     * Get the schema of the component
     *
     * @return the schema
     */
    @Override
    Schema getSchema ();

    /**
     * Set the schema of the component
     *
     * @param schema
     */
    void setSchema (Schema schema);

    /**
     * Get the last published date
     *
     * @return the last published date
     */
    @Override
    DateTime getLastPublishedDate ();

    /**
     * Set the last published date
     *
     * @param date published date
     */
    @Override
    void setLastPublishedDate (DateTime date);

    /**
     * Get the metadata
     *
     * @return a map of field objects representing the metadata
     */
    Map<String, Field> getMetadata ();

    /**
     * Set the metadata
     */
    void setMetadata (Map<String, Field> metadata);

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    Map<String, Field> getContent ();

    /**
     * Set the content
     */
    void setContent (Map<String, Field> content);

    /**
     * Get the component type
     *
     * @return the component type
     */
    ComponentType getComponentType ();

    /**
     * Set the component type
     *
     * @param componentType
     */
    void setComponentType (ComponentType componentType);

    /**
     * Get the multimedia object
     *
     * @return the multimedia object
     */
    Multimedia getMultimedia ();

    /**
     * Set the multimedia object
     */
    void setMultimedia (Multimedia multimedia);

    List<Category> getCategories ();

    void setCategories (List<Category> categories);

    int getVersion ();

    DateTime getRevisionDate ();

    void setRevisionDate (DateTime date);

    String getEclId ();

    enum ComponentType {
        MULTIMEDIA(0), NORMAL(1), UNKNOWN(-1);
        private final int value;
        private static final Logger LOG = LoggerFactory.getLogger(ComponentType.class);

        ComponentType (int value) {
            this.value = value;
        }

        public static ComponentType findByValue (int value) {
            for (ComponentType componentType : values()) {
                if (componentType.getValue() == value) {
                    return componentType;
                }
            }

            return UNKNOWN;
        }

        public static ComponentType findByName (String name) {
            try {
                return ComponentType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException iae) {
                LOG.error(iae.getLocalizedMessage(), iae);
                try {
                    int value = Integer.parseInt(name);
                    return findByValue(value);
                } catch (NumberFormatException nfe) {
                    LOG.error(nfe.getLocalizedMessage(), nfe);
                    return UNKNOWN;
                }
            }
        }

        public int getValue () {
            return value;
        }
    }
}
