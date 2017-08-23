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

package org.dd4t.providers.impl;

import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.web.api.taxonomies.WebTaxonomyFactoryImpl;
import com.tridion.broker.StorageException;
import com.tridion.storage.RelatedKeyword;
import com.tridion.taxonomies.Keyword;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.NotImplementedException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.BaseBrokerProvider;
import org.dd4t.providers.TaxonomyProvider;

import java.text.ParseException;
import java.util.List;

/**
 * Provides access to taxonomies published to a Content Delivery database. It returns keywords with all their parent/
 * children relationships resolved. It also provides the capability to retrieve related items (i.e. Tridion items that
 * use each Keyword in the taxonomy directly).
 * <p/>
 * TODO: finish this for all taxonomy providers
 *
 * @author Mihai Cadariu, Raimond Kempees
 */
public class BrokerTaxonomyProvider extends BaseBrokerProvider implements TaxonomyProvider {

    /**
     * Retrieves the Keyword object model with all its Parent/Children relationships resloved.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI
     * @return Keyword the resolved taxonomy with its parent/children relationships
     * @throws StorageException if something went wrong during accessing the CD DB
     */

    public Keyword getTaxonomy (String taxonomyURI) throws StorageException {
        WebTaxonomyFactory webTaxonomyFactory = new WebTaxonomyFactoryImpl();
        return webTaxonomyFactory.getTaxonomyKeywords(taxonomyURI);
    }

    /**
     * Returns a list of RelatedKeyword objects representing the usage of each Keyword under the given taxonomy URI
     * and its direct using item. Returned items are only of the given type.
     *
     * @param taxonomyURI String representing the root taxonomy Keyword TCMURI
     * @param itemType    int representing the item type id to retrieve
     * @return List a list of RelatedKeyword objects holding item usage information
     * @throws ParseException   if the given Keyword URI does not represent a valid TCMURI
     * @throws StorageException if something went wrong during accessing the CD DB
     */

    public List<RelatedKeyword> getRelatedItems (String taxonomyURI, int itemType) throws ParseException, StorageException {
        throw new NotImplementedException();
    }

    /**
     * Returns a list of RelatedKeyword objects representing the usage of each Keyword under the given taxonomy URI
     * and its direct using item. Returned items are only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the root taxonomy Keyword TCMURI
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return List a list of RelatedKeyword objects holding item usage information
     * @throws ParseException   if the given Keyword URI does not represent a valid TCMURI
     * @throws StorageException if something went wrong during accessing the CD DB
     */
    public List<RelatedKeyword> getRelatedComponentsBySchema (String taxonomyURI, String schemaURI) throws ParseException, StorageException {
        throw new NotImplementedException();
    }


    @Override
    public String getTaxonomyByURI (final String taxonomyURI, final boolean resolveContent) throws ItemNotFoundException, SerializationException {
       throw new NotImplementedException();
    }

    @Override
    public String getTaxonomyFilterBySchema (final String taxonomyURI, final String schemaURI) throws ItemNotFoundException, SerializationException {
        throw new NotImplementedException();
    }
}