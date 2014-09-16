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
package com.sdl.tridion.referenceimpl.controller;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.HasMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class BaseDD4TController extends AbstractController {
    private static Logger logger = LoggerFactory.getLogger(BaseDD4TController.class);
    protected String defaultRegion = "default";
    protected boolean defaultIsStatic = false;
    
    public String getViewFromTemplate(HasMetadata item) {

        logger.debug("Found metadata: " + item.getMetadata());
        return (String) item.getMetadata().get("view").getValues().get(0);
    }

    public String getRegionFromTemplate(HasMetadata item) {

        logger.debug("Found metadata: " + item.getMetadata());
        Field regionField = item.getMetadata().get("region");
        if (regionField == null || regionField.getValues().size()==0)
        		return this.defaultRegion;
        return (String) regionField.getValues().get(0);
    }
    

}
