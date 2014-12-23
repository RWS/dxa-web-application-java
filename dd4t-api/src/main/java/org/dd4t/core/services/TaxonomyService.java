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
    public Keyword findKeywordByClassifiedId(TCMURI tcmuri) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given description.
     *
     * @param description String representing the description of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordByDescription(String description) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI.
     *
     * @param tcmuri String representing the TCMURI of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordById(String tcmuri) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI item id.
     *
     * @param itemId String representing the TCMURI item id of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordByItemId(int itemId) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given key.
     *
     * @param key String representing the key of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordByKey(String key) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given name.
     *
     * @param name String representing the name of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordByName(String name) throws IOException;

    /**
     * Retrieves a resolved Keyword object corresponding to the given path.
     *
     * @param path String representing the path of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    public Keyword findKeywordByPath(String path) throws IOException;

    /**
     * Returns the root Keyword of Taxonomy.
     *
     * @return Keyword the root node of the Taxonomy
     */
    public Keyword getTaxonomy();
}
