package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.util.Collection;
import java.util.Map;

/**
 * QueryProvider. Interface for doing Broker look ups.
 * Currently provides only the basic stuff.
 *
 * TODO: add methods to query custom meta for normal CPs
 *
 * @author R. Kempees
 */
public interface QueryProvider {

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its locale and keyValueMap
     * The returned content can for instance represent multiple Component Strings separated by |.
     *
     * The KeyValueMap can hold 0 or more CUSTOM_META criteria
     *
     * @param locale      String representing the locale to be queried, for e.g.: en_gb
     * @param keyValueMap A Map representing the Key Value pairs to insert as Meta Filters
     * @return String representing the content of the CP (s)
     * @throws ItemNotFoundException  if the requested CP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    public String[] getDynamicComponentPresentationsByCustomMetaQuery(String locale, Map<String, Collection<String>> keyValueMap, int templateId) throws ItemNotFoundException, SerializationException;

	/**
	 * Retrieves content of a Dynamic Component Presentation by doing a Broker Query
	 * based on Schema Criteria. If the templateId is set, only components published
	 * with that templateId are returned.
	 *
	 * Note that doing a direct Broker query is significantly
	 * faster than using the TaxonomyProvider.
	 *
	 * The returned content can for instance represent multiple Component Strings separated by |.
	 *
	 * @param locale The locale, to be translated to a publication Id
	 * @param schema The title of the schema
	 * @param templateId The Component template Id of CPs to return
	 * @return String representing the content of the CP(s)
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */
	public String[] getDynamicComponentPresentationsBySchema(String locale, String schema, int templateId) throws ItemNotFoundException, SerializationException;


	/**
	 * Retrieves content of a Dynamic Component Presentation by doing an AND Broker Query
	 * based on Schema and Keyword Criteria. If the templateId is set, only components published
	 * with that templateId are returned.
	 *
	 * Note that doing a direct Broker query is significantly
	 * faster than using the TaxonomyProvider.
	 *
	 * The returned content can for instance represent multiple Component Strings separated by |.
	 *
	 * @param locale The locale, to be translated to a publication Id
	 * @param schema The title of the schema
	 * @param categoryId The Item Id of the category
	 * @param keywordId The Item Id of the keyword
	 * @param templateId The Component template Id of CPs to return
	 * @return String representing the content of the CP(s)
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */

	public String[] getDynamicComponentPresentationsBySchemaInKeyword(String locale, String schema, int categoryId, int keywordId, int templateId) throws ItemNotFoundException, SerializationException;
}
