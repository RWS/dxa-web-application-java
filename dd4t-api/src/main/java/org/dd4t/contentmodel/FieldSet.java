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
 * Container for embedded fields.
 *
 * @author Rogier Oudshoorn
 */
public interface FieldSet {

    /**
     * This method is deprecated in dd4t-2; it will return a null.
     * If you do want to use it please utilize the  
     * org.dd4t.core.processors.impl.FieldSetSchemaProcessor from
     * dd4t-compatibility to have it filled as it was in dd4t-1.
     * I 
     *
     * @return the schema
     */
	@Deprecated
    Schema getSchema ();

    /**
     * Set the schema of the component
     *
     * @param schema
     */
    void setSchema (Schema schema);

    Map<String, Field> getContent ();

    void setContent (Map<String, Field> content);

}
