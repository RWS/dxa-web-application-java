package org.dd4t.core.factories;

import org.dd4t.core.services.TaxonomyService;

import java.io.IOException;

/**
 * @author Mihai Cadariu
 */
public interface TaxonomyServiceFactory {

    /**
     * Generic taxonomy service.
     *
     * @param categoryURI String representing the Tridion Category XML name or TCMURI
     * @return TaxonomyService representing taxonomy service
     * @throws IOException if something goes wrong while fetching the taxonomy
     */
    public TaxonomyService getTaxonomyService(String categoryURI) throws IOException;

    /**
     * Generic taxonomy service that retrieves only classified Component based on the given SchemaURI.
     *
     * @param categoryURI String representing the Tridion Category XML name or TCMURI
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return TaxonomyService
     * @throws IOException if something goes wrong while fetching the taxonomy
     */
    public TaxonomyService getTaxonomyBySchemaService(String categoryURI, String schemaURI) throws IOException;
}
