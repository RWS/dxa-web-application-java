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

public interface TaxonomyProvider {

    /**
     * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
     * children relationships resolved.
     *
     * @param taxonomyURI    String representing the TCMURI of the taxonomy to retrieve
     * @param resolveContent boolean indicating whether or not to resolverepresenting the context Publication id to read the Page from
     * @return String representing the Keyword object
     * @throws ItemNotFoundException  if said taxonomy cannot be found
     * @throws SerializationException if response from service does not represent a serialized Keyword object
     */
    String getTaxonomyByURI (String taxonomyURI, boolean resolveContent) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
     * children relationships resolved. The related items are filtered to only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the TCMURI of the taxonomy to retrieve
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return String representing the Keyword object
     * @throws ItemNotFoundException  if said taxonomy cannot be found
     * @throws SerializationException if response from service does not represent a serialized Keyword object
     */
    String getTaxonomyFilterBySchema (String taxonomyURI, String schemaURI) throws ItemNotFoundException, SerializationException;
}
