package org.dd4t.core.factories;

import org.dd4t.contentmodel.Keyword;

import java.io.IOException;

/**
 * Provides ways to resolve keywords in a Taxonomy to their respective Keyword object. It uses the Taxonomy provider
 * to read the entire Taxonomy from Hammerfest. It also provides a caching layer for further read performance.
 *
 * @author Mihai Cadariu
 * @since 18.06.2014
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
    public Keyword getTaxonomy(String taxonomyURI) throws IOException;

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
    public Keyword getTaxonomyFilterBySchema(String taxonomyURI, String schemaURI) throws IOException;
}
