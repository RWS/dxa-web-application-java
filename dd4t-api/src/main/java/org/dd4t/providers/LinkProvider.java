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

/**
 * Link provider.
 */
public interface LinkProvider {

    /**
     * Retrieves a link URL to a Component.
     *
     * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not resolved.
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    String resolveComponent (String targetComponentUri) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a link URL to a Component from a Page.
     *
     * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not resolved
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    String resolveComponentFromPage (String targetComponentUri, String sourcePageUri) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a link URL to a Component with the option to exclude links
     * to the specified component template.
     *
     * @param targetComponentUri          String representing the TcmUri of the Component to resolve a link to
     * @param excludeComponentTemplateUri The Component Presentation to exclude
     * @return String representing the URL of the link; or null, if the Component is not resolved.
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    String resolveComponent (final String targetComponentUri, final String excludeComponentTemplateUri) throws ItemNotFoundException, SerializationException;
}
