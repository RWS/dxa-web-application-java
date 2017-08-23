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

package org.dd4t.core.factories;

import org.dd4t.contentmodel.Keyword;

import java.io.IOException;

/**
 * Provides ways to resolve keywords in a Taxonomy to their respective Keyword object. It uses the Taxonomy provider
 * to read the entire Taxonomy. It also provides a caching layer for further read performance.
 *
 * @author Mihai Cadariu
 */
public interface TaxonomyFactory {

    /**
     * Returns the root Keyword of Taxonomy by reading the specified taxonomy from the local cache or from the
     * Taxonomy provider, if not found in cache.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI to read
     * @return Keyword the root node of the Taxonomy
     * @throws IOException if said taxonomy cannot be found or an error occurred while fetching it
     */
    Keyword getTaxonomy (String taxonomyURI) throws IOException;

    /**
     * Returns the root Keyword of Taxonomy by reading the specified taxonomy from the local cache or from the
     * Taxonomy provider, if not found in cache.
     * <p/>
     * The returned classified items are filtered to only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI to read
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return Keyword the root node of the Taxonomy
     * @throws IOException if said taxonomy cannot be found or an error occurred while fetching it
     */
    Keyword getTaxonomyFilterBySchema (String taxonomyURI, String schemaURI) throws IOException;
}
