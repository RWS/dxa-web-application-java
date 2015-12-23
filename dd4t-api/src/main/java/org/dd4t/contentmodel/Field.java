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

import java.util.List;

public interface Field {

    /**
     * Get the values of the field.
     *
     * @return a list of objects, where the type is depending of the field type.
     * Never returns null.
     */
    List<Object> getValues();

    /**
     * Get the name of the field.
     *
     * @return the name of the field
     */
    String getName();

    /**
     * Set the name of the field
     *
     * @param name
     */
    void setName(String name);

    /**
     * Get the xPath of the field (used for SiteEdit)
     *
     * @return the xPath of the field
     */
    String getXPath();

    /**
     * Set the xPath of the field (used for SiteEdit)
     *
     * @param xPath
     */
    void setXPath(String xPath);

    /**
     * Get the field type
     *
     * @return the field type
     */
    FieldType getFieldType();

    /**
     * Set the field type
     *
     * @param fieldType
     */
    void setFieldType(FieldType fieldType);
}