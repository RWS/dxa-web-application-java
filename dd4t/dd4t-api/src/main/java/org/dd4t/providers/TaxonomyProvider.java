package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;

/**
 * Client side of the Taxonomy provider. This communicates with the service layer (Hammerfest) to read
 * Taxonomies by their URI. The communication is done using JAX-RS singleton client.
 * <p/>
 * No Tridion dependencies are allowed here (they are all in Hammerfest)
 */
public interface TaxonomyProvider {

    /**
     * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
     * children relationships resolved.
     *
     * @param taxonomyURI    String representing the TCMURI of the taxonomy to retrieve
     * @param resolveContent boolean indicating whether or not to resolverepresenting the context Publication id to read the Page from
     * @return String representing the JSON encoded Keyword object
     * @throws ItemNotFoundException  if said taxonomy cannot be found
     * @throws SerializationException if response from service does not represent a serialized Keyword object
     */
    public String getTaxonomyByURI(String taxonomyURI, boolean resolveContent) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
     * children relationships resolved. The related items are filtered to only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the TCMURI of the taxonomy to retrieve
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return String representing the JSON encoded Keyword object
     * @throws ItemNotFoundException  if said taxonomy cannot be found
     * @throws SerializationException if response from service does not represent a serialized Keyword object
     */
    public String getTaxonomyFilterBySchema(String taxonomyURI, String schemaURI) throws ItemNotFoundException, SerializationException;
}
