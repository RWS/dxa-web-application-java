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

package org.dd4t.core.services;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.util.TCMURI;

import java.io.IOException;

/**
 * Provides ways to resolve keywords in a Taxonomy to their respective Keyword object.
 *
 * @author Mihai Cadariu
 */
public interface TaxonomyService {

    /**
     * Retrieves a resolved Keyword object that was used to classify the item with the given TCMURI.
     *
     * @param tcmuri String representing the TCMURI of the item that was classified with the Keyword we are looking for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByClassifiedId (TCMURI tcmuri) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given description.
     *
     * @param description String representing the description of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByDescription (String description) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI.
     *
     * @param tcmuri String representing the TCMURI of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordById (String tcmuri) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI item id.
     *
     * @param itemId String representing the TCMURI item id of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByItemId (int itemId) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given key.
     *
     * @param key String representing the key of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByKey (String key) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given name.
     *
     * @param name String representing the name of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByName (String name) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given path.
     *
     * @param path String representing the path of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    Keyword findKeywordByPath (String path) throws IOException;

    /**
     * Returns the root Keyword of Taxonomy.
     *
     * @return Keyword the root node of the Taxonomy
     */
    Keyword getTaxonomy ();
}
