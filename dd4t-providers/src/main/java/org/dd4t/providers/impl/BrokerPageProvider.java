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

import com.tridion.broker.StorageException;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.content.PageURLCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.data.CharacterData;
import com.tridion.storage.ItemMeta;
import com.tridion.storage.PageMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.storage.dao.ItemTypeSelector;
import com.tridion.storage.dao.PageDAO;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BaseBrokerProvider;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.PageProviderResultItem;
import org.dd4t.providers.PageResultItemImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Provides access to Page content and metadata from Content Delivery database. Access to page content is not cached,
 * so as such much be cached externally. Calls to Page meta are cached in the Tridion object cache.
 */
public class BrokerPageProvider extends BaseBrokerProvider implements PageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerPageProvider.class);

    @Override
    public PageProviderResultItem<String> getPageById (final int id, final int publication) throws IOException, ItemNotFoundException, SerializationException {

        final PageMeta pageMeta = getPageMetaById(id, publication);

        if (pageMeta == null) {
            throw new ItemNotFoundException("Unable to find page meta by id '" + id + "' and publication '" + publication + "'.");
        }

        PageProviderResultItem<String> pageResult = new PageResultItemImpl(pageMeta.getPublicationId(), pageMeta.getItemId(), pageMeta.getUrl());

        pageResult.setLastPublishDate(pageMeta.getLastPublishDate());
        pageResult.setRevisionDate(pageMeta.getModificationDate());
        pageResult.setContentSource(getPageContentById(id, publication));

        return pageResult;
    }

    @Override
    public PageProviderResultItem<String> getPageByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
        PageMeta pageMeta = getPageMetaByURL(url, publication);

        if (pageMeta == null) {
            throw new ItemNotFoundException("Unable to find page meta by url '" + url + "' and publication '" + publication + "'.");
        }

        PageProviderResultItem<String> pageResult = new PageResultItemImpl(pageMeta.getPublicationId(), pageMeta.getItemId(), pageMeta.getUrl());

        pageResult.setLastPublishDate(pageMeta.getLastPublishDate());
        pageResult.setRevisionDate(pageMeta.getModificationDate());
        pageResult.setContentSource(getPageContentById(pageMeta.getItemId(), pageMeta.getPublicationId()));
        return pageResult;
    }

    /**
     * Retrieves content of a Page by looking the page up by its item id and Publication id.
     *
     * @param id          int representing the page item id
     * @param publication int representing the Publication id of the page
     * @return String representing the content of the Page
     * @throws ItemNotFoundException if the requested page does not exist
     */
    @Override
    public String getPageContentById (int id, int publication) throws ItemNotFoundException, SerializationException {

        CharacterData data = null;
        try {
            PageDAO pageDAO = (PageDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE);
            data = pageDAO.findByPrimaryKey(publication, id);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (data == null) {
            throw new ItemNotFoundException("Unable to find page by id '" + id + "' and publication '" + publication + "'.");
        }
        try {
            return decodeAndDecompressContent(convertStreamToString(data.getInputStream()));
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Retrieves content of a Page by looking the page up by its URL.
     *
     * @param url         String representing the path part of the page URL
     * @param publication int representing the Publication id of the page
     * @return String representing the content of the Page
     * @throws SerializationException           if the character stream cannot be read
     * @throws ItemNotFoundException if the requested page does not exist
     */
    @Override
    public String getPageContentByURL (String url, int publication) throws ItemNotFoundException, SerializationException {
        PageMeta meta = getPageMetaByURL(url, publication);
        return getPageContentById(meta.getItemId(), meta.getPublicationId());
    }

    @Override
    public String getPageContentById (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {
        TCMURI uri = new TCMURI(tcmUri);
        return getPageContentById(uri.getItemId(), uri.getPublicationId());
    }

    /**
     * Retrieves metadata of a Page by looking the page up by its item id and Publication id.
     *
     * @param id          int representing the page item id
     * @param publication int representing the Publication id of the page
     * @return PageMeta representing the metadata of the Page
     * @throws ItemNotFoundException if the requested page does not exist
     */
    public PageMeta getPageMetaById (int id, int publication) throws ItemNotFoundException {

        PageMeta meta = null;
        try {
            ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
            meta = (PageMeta) itemDAO.findByPrimaryKey(publication, id);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (meta == null) {
            throw new ItemNotFoundException("Unable to find page by id '" + id + "' and publication '" + publication + "'.");
        }

        return meta;
    }

    /**
     * Retrieves metadata of a Page by looking the page up by its URL.
     *
     * @param url         String representing the path part of the page URL
     * @param publication int representing the Publication id of the page
     * @return PageMeta representing the metadata of the Page
     * @throws ItemNotFoundException if the requested page does not exist
     */
    public PageMeta getPageMetaByURL (String url, int publication) throws ItemNotFoundException {

        PageMeta meta = null;
        try {
            ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
            meta = itemDAO.findByPageURL(publication, url);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (meta == null) {
            throw new ItemNotFoundException("Unable to find page by url '" + url + "' and publication '" + publication + "'.");
        }

        return meta;
    }

    /**
     * Retrieves a list of URLs for all published Tridion Pages in a Publication.
     *
     * @param publication int representing the Publication id of the page
     * @return String representing the list of URLs (one URL per line)
     * @throws ItemNotFoundException if the requested page does not exist
     */
    @Override
    public String getPageListByPublicationId (int publication) throws ItemNotFoundException {

        List<ItemMeta> itemMetas = null;
        try {
            ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
            itemMetas = itemDAO.findAll(publication, ItemTypeSelector.PAGE);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (itemMetas == null || itemMetas.isEmpty()) {
            throw new ItemNotFoundException("Unable to find page URL list by publication '" + publication + "'.");
        }

        StringBuilder result = new StringBuilder();
        for (ItemMeta itemMeta : itemMetas) {
            result.append(((PageMeta) itemMeta).getUrl()).append("\r\n");
        }

        return result.toString();
    }

    // TODO: introduce ProviderException
    @Override
    public boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {

        LOG.debug("Checking whether Page with url: {} exists", url);

        String key = getKey(CacheType.PAGE_EXISTS, url);
        CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = null;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    final PublicationCriteria publicationCriteria = new PublicationCriteria(publicationId);
                    final PageURLCriteria pageURLCriteria = new PageURLCriteria(url);

                    final Query tridionQuery = new Query(new AndCriteria(publicationCriteria, pageURLCriteria));
                    tridionQuery.setResultFilter(new LimitFilter(1));
                    tridionQuery.addSorting(new SortParameter(SortParameter.ITEMS_URL, SortDirection.DESCENDING));

                    try {
                        String[] results = tridionQuery.executeQuery();
                        if (results != null && results.length > 0) {
                            result = 1;
                            TCMURI tcmuri = new TCMURI(results[0]);
                            cacheElement.setPayload(result);
                            cacheProvider.storeInItemCache(key, cacheElement, tcmuri.getPublicationId(), tcmuri.getItemId());
                            cacheElement.setExpired(false);
                        } else {
                            result = 0;
                            cacheElement.setPayload(result);
                            cacheProvider.storeInItemCache(key, cacheElement);
                            cacheElement.setExpired(false);
                        }
                    } catch (StorageException | ParseException e) {
                        LOG.error(e.getLocalizedMessage(), e);
                    }
                    LOG.debug("Stored Page exist check with key: {} in cache", key);
                } else {
                    LOG.debug("Fetched a Page exist check with key: {} from cache", key);
                    result = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Fetched Page exist check with key: {} from cache", key);
            result = cacheElement.getPayload();
        }

        return result != null && (result == 1);
    }

    @Override
    public TCMURI getPageIdForUrl (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
        PageMeta pageMeta = getPageMetaByURL(url, publicationId);
        if (pageMeta != null) {
            return new TCMURI(publicationId, pageMeta.getItemId(), pageMeta.getItemType(), pageMeta.getMajorVersion());
        }
        throw new ItemNotFoundException("Page Id for URL not found.");
    }

    @Override
    public DateTime getLastPublishDate (final String url, final int publication) throws ItemNotFoundException {
        PageMeta pageMeta = getPageMetaByURL(url, publication);
        Date lpd = pageMeta.getLastPublishDate();
        return lpd != null ? new DateTime(pageMeta.getLastPublishDate()) : Constants.THE_YEAR_ZERO;
    }
}
