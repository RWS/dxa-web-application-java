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
package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Container for embedded fields.
 * 
 *
 * @author <a href="rogier.oudshoorn@">Rogier Oudshoorn</a>
 * @version $Revision: 6 $
 */
public interface FieldSet {
    
    /**
     * Get the schema of the component
     * @return the schema
     */
    public Schema getSchema();
    
    /**
     * Set the schema of the component
     * @param schema
     */
    public void setSchema(Schema schema);
    
    public void setContent(Map<String, Field> content);
    
    public Map<String, Field> getContent();

}
