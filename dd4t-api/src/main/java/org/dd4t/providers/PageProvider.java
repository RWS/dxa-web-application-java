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
import org.dd4t.core.util.TCMURI;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.ParseException;

/**
 * Page Provider.Reads Pages by their URL or id.
 */
public interface PageProvider extends BaseProvider {


    ProviderResultItem<String> getPageById (int id, int publication) throws IOException, ItemNotFoundException, SerializationException;

    ProviderResultItem<String> getPageByURL (String url, int publication) throws ItemNotFoundException, SerializationException;


    String getPageContentById (int id, int publication) throws IOException, ItemNotFoundException, SerializationException;

    /**
     * Retrieves a Page by its Publication and URL. It returns JSON representing a Page model object.
     *
     * @param url         String representing the path part of the page URL
     * @param publication int representing the context Publication id to read the Page from
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    String getPageContentByURL (String url, int publication) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a Page by its TCMURI. It returns JSON representing a Page model object.
     *
     * @param tcmUri String representing the Tridion Page URI
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws ParseException         if given parameter does not represent a TCMURI
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    String getPageContentById (String tcmUri) throws ItemNotFoundException, ParseException, SerializationException;

    /**
     * Retrieves a list of published page URLs as one String.
     *
     * @param publication the Publication Id
     * @return A String containing all publication URLs
     * @throws ItemNotFoundException
     * @throws SerializationException
     */

    String getPageListByPublicationId (int publication) throws ItemNotFoundException, SerializationException;

    /**
     * Checks whether a page exists (published from Tridion) by querying its URL
     *
     * @param url           String representing the path part of the page URL
     * @param publicationId int representing the context Publication id to read the Page from
     * @return Boolean True if the page is published and exists
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws SerializationException if there was an error communicating with the service
     */
    boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException;

    TCMURI getPageIdForUrl (final String url, final int publicationId) throws ItemNotFoundException, SerializationException;

    DateTime getLastPublishDate (final String url, final int publication) throws ItemNotFoundException;
}
