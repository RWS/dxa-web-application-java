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

import org.dd4t.core.databind.BaseViewModel;

import java.util.Map;

public interface ComponentPresentation {

    /**
     * Get the component
     *
     * @return
     */
    Component getComponent ();

    /**
     * Set the component
     *
     * @param component
     */
    void setComponent (Component component);

    /**
     * Get the component template
     *
     * @return
     */
    ComponentTemplate getComponentTemplate ();

    /**
     * Set the component template
     *
     * @param componentTemplate
     */
    void setComponentTemplate (ComponentTemplate componentTemplate);

    Map<String, FieldSet> getExtensionData ();

    void setExtensionData (Map<String, FieldSet> extensionData);

    /**
     * Sets the raw component content form the broker.
     * Needed to build DCP strong models and to
     * have all needed meta information on the CP and CT.
     *
     * @param rawComponentContent the Json or XML Component String from the broker.
     */
    void setRawComponentContent (String rawComponentContent);

    String getRawComponentContent ();

    /**
     * Get the rendered content
     */
    String getRenderedContent ();

    /**
     * Set the rendered content
     *
     * @param renderedContent
     */
    void setRenderedContent (String renderedContent);

    /**
     * Return true if the component presentation is dynamic (i.e. available in the broker database as a separate item)
     *
     * @return
     */
    boolean isDynamic ();

    void setIsDynamic (boolean b);

    void setOrderOnPage (int i);

    void setViewModel (Map<String, BaseViewModel> models);

    Map<String, BaseViewModel> getAllViewModels ();

    BaseViewModel getViewModel (String modelName);
}
