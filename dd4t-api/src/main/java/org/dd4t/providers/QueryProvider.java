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

import java.util.Collection;
import java.util.Map;

/**
 * QueryProvider. Interface for doing Broker look ups.
 * Currently provides only the basic stuff.
 * <p/>
 * TODO: add methods to query custom meta for normal CPs
 *
 * @author R. Kempees
 */
public interface QueryProvider {

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its locale and keyValueMap
     * The returned content can for instance represent multiple Component Strings separated by |.
     * <p/>
     * The KeyValueMap can hold 0 or more CUSTOM_META criteria
     *
     * @param locale      String representing the locale to be queried, for e.g.: en_gb
     * @param keyValueMap A Map representing the Key Value pairs to insert as Meta Filters
     * @return String representing the content of the CP (s)
     * @throws ItemNotFoundException  if the requested CP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    String[] getDynamicComponentPresentationsByCustomMetaQuery (String locale, Map<String, Collection<String>> keyValueMap, int templateId) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves content of a Dynamic Component Presentation by doing a Broker Query
     * based on Schema Criteria. If the templateId is set, only components published
     * with that templateId are returned.
     * <p/>
     * Note that doing a direct Broker query is significantly
     * faster than using the TaxonomyProvider.
     * <p/>
     * The returned content can for instance represent multiple Component Strings separated by |.
     *
     * @param locale     The locale, to be translated to a publication Id
     * @param schema     The title of the schema
     * @param templateId The Component template Id of CPs to return
     * @return String representing the content of the CP(s)
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    String[] getDynamicComponentPresentationsBySchema (String locale, String schema, int templateId) throws ItemNotFoundException, SerializationException;


    /**
     * Retrieves content of a Dynamic Component Presentation by doing an AND Broker Query
     * based on Schema and Keyword Criteria. If the templateId is set, only components published
     * with that templateId are returned.
     * <p/>
     * Note that doing a direct Broker query is significantly
     * faster than using the TaxonomyProvider.
     * <p/>
     * The returned content can for instance represent multiple Component Strings separated by |.
     *
     * @param locale     The locale, to be translated to a publication Id
     * @param schema     The title of the schema
     * @param categoryId The Item Id of the category
     * @param keywordId  The Item Id of the keyword
     * @param templateId The Component template Id of CPs to return
     * @return String representing the content of the CP(s)
     * @throws ItemNotFoundException
     * @throws SerializationException
     */

    String[] getDynamicComponentPresentationsBySchemaInKeyword (String locale, String schema, int categoryId, int keywordId, int templateId) throws ItemNotFoundException, SerializationException;
}
