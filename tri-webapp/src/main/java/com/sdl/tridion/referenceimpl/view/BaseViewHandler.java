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
package com.sdl.tridion.referenceimpl.view;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages a directory full of files. Both directory and fileending are
 * given by implementing class, and the baseviewhandler will index all
 * appropriate files and deliver them to the implementing class inside of the
 * cachedviews Map in the form of [viewname] : [absolute path to file]
 * 
 * @author rooudsho
 * 
 */
public abstract class BaseViewHandler {
    private static Logger logger = LoggerFactory.getLogger(BaseViewHandler.class);

    private String filePath;

    private String fileEnding;

    protected Map<String, String> cachedViews;

    public BaseViewHandler() {

        cachedViews = new HashMap<String, String>();
    }

    public BaseViewHandler(String filePath, String fileEnding) {

        cachedViews = new HashMap<String, String>();
        this.filePath = filePath;
        this.fileEnding = fileEnding;

        loadViews();
    }

    /**
     * Initiation function, crawling the directory to search for appropriate
     * files.
     */
    private void loadViews() {

        File dir = new File(filePath);

        File[] files = dir.listFiles();

        // debug results of given linking to files
        if (logger.isDebugEnabled()) {
            logger.debug("Opening directory '" + filePath + "'");
            logger.debug(""+dir);

            if (files == null) {
                logger.debug("Unable to load files in directory.");
            } else {
                logger.debug("Preloading " + files.length + " JSP views");
            }
        }

        // crawl fileset and store in cachedviews
        for (File file : files) {
            if (file.getName().endsWith("fileEnding")) {
                String name = file.getName().replaceAll("." + fileEnding, "");
                cachedViews.put(name, file.getAbsolutePath());
            }
        }
    }

    public Collection<String> provideViews() {

        return cachedViews.keySet();
    }
}
