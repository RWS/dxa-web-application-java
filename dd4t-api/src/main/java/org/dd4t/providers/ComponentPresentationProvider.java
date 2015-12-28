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

package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.util.List;

/**
 * Dynamic Component Presentation Provider.
 * <p/>
 * Note that this class is NOT used when fetching pages with static Component Presentations!
 */
public interface ComponentPresentationProvider {

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
     * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
     * <p/>
     * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
     * Do provide a templateId!</b>
     *
     * @param componentId   int representing the Component item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException  if the requested DCP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    String getDynamicComponentPresentation (int componentId, int publicationId) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
     *
     * @param componentId   int representing the Component item id
     * @param templateId    int representing the Component Template item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException  if the requested DCP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    String getDynamicComponentPresentation (int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;

    /**
     * Convenience method to obtain a list of component presentations for the same template id.
     *
     * @param itemUris      array of found Component TCM IDs
     * @param templateId    the CT Id to fetch DCPs on
     * @param publicationId the current Publication Id
     * @return a List of Component Presentations
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    List<String> getDynamicComponentPresentations (String[] itemUris, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;
}
