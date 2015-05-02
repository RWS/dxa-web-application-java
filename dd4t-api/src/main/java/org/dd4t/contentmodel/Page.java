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

import java.util.List;
import java.util.Map;

public interface Page extends RepositoryLocalItem {
    /**
     * Get the page template
     *
     * @return the page template object
     */
    public PageTemplate getPageTemplate();

    /**
     * Set the page template
     *
     * @param pageTemplate
     */
    public void setPageTemplate(PageTemplate pageTemplate);

    /**
     * Get the file name
     *
     * @return the name of the file
     */
    public String getFileName();

    /**
     * Set the file name
     *
     * @param fileName
     */
    public void setFileName(String fileName);

    /**
     * Get the file extension of the page
     *
     * @return the file extension
     */
    public String getFileExtension();

    /**
     * Set the file extension
     *
     * @param fileExtension
     */
    public void setFileExtension(String fileExtension);

    /**
     * Get the metadata as a map of fields
     */
    public Map<String, Field> getMetadata();

    /**
     * Set the metadata
     */
    public void setMetadata(Map<String, Field> metadata);

    /**
     * Get the list of component presentations on the page
     *
     * @return a list of component presentation
     */
    public List<ComponentPresentation> getComponentPresentations();

    /**
     * Set the component presentations
     *
     * @param componentPresentations
     */
    public void setComponentPresentations(List<ComponentPresentation> componentPresentations);

    public List<Category> getCategories();

    public void setCategories(List<Category> categories);

    public StructureGroup getStructureGroup();

    public void setStructureGroup(StructureGroup structureGroup);

    public int getVersion();


    public DateTime getLastPublishedDate();

    public void setLastPublishedDate(DateTime date);

    public DateTime getRevisionDate();

    public void setRevisionDate(DateTime date);
}
