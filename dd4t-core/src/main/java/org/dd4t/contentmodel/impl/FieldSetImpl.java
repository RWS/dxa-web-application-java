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

import java.util.Map;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Schema;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;


public class FieldSetImpl implements FieldSet {
    @ElementMap(name = "fields", keyType = String.class, valueType = Field.class, entry = "item", required = false)
    private Map<String, Field> content;
    
    @Element(name = "schema", required = true)
    private Schema schema;

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
    
    public Schema getSchema() {
            return schema;
    }
    
    /**
     * Set the content
     */
    public void setContent(Map<String, Field> content) {
            this.content = content;
    }

    /**
     * Get the content
     * 
     * @return a map of field objects representing the content
     */
    public Map<String, Field> getContent() {
            return content;
    }
}
