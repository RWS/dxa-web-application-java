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

import com.sdl.web.api.content.PageContentRetriever;
import com.sdl.web.api.content.PageContentRetrieverImpl;
import com.sdl.web.api.meta.WebPageMetaFactory;
import com.sdl.web.api.meta.WebPageMetaFactoryImpl;
import com.tridion.data.CharacterData;
import com.tridion.meta.PageMeta;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.NotImplementedException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BaseBrokerProvider;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.ProviderResultItem;
import org.dd4t.providers.StringResultItemImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to Page content and metadata from Content Delivery database. Access to page content is not cached,
 * so as such much be cached externally. Calls to Page meta are cached in the Tridion object cache.
 */
public class BrokerPageProvider extends BaseBrokerProvider implements PageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerPageProvider.class);
    private static final Map<Integer,WebPageMetaFactory> WEB_PAGE_META_FACTORIES = new ConcurrentHashMap<>();
    private static final PageContentRetriever PAGE_CONTENT_RETRIEVER = new PageContentRetrieverImpl();

    @Override
    public ProviderResultItem<String> getPageById (final int id, final int publication) throws IOException, ItemNotFoundException, SerializationException {

        final PageMeta pageMeta = getPageMetaById(id, publication);
        final ProviderResultItem<String> pageResult = new StringResultItemImpl();
        pageResult.setLastPublishDate(pageMeta.getLastPublicationDate());
        pageResult.setRevisionDate(pageMeta.getModificationDate());
        pageResult.setContentSource(getPageContentById(id, publication));

        return pageResult;
    }

    @Override
    public ProviderResultItem<String> getPageByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
        final PageMeta pageMeta = getPageMetaByURL(url, publication);
        final ProviderResultItem<String> pageResult = new StringResultItemImpl();
        pageResult.setLastPublishDate(pageMeta.getLastPublicationDate());
        pageResult.setRevisionDate(pageMeta.getModificationDate());
        pageResult.setContentSource(getPageContentById(pageMeta.getId(), pageMeta.getPublicationId()));
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

        final CharacterData data = PAGE_CONTENT_RETRIEVER.getPageContent(publication,id);

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
     * @throws SerializationException if something goes wrong deserializing
     * @throws ItemNotFoundException if the requested page does not exist
     */
    @Override
    public String getPageContentByURL (String url, int publication) throws ItemNotFoundException, SerializationException {
        final PageMeta meta = getPageMetaByURL(url, publication);
        return getPageContentById(meta.getId(), meta.getPublicationId());
    }

    @Override
    public String getPageContentById (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {
        final TCMURI uri = new TCMURI(tcmUri);
        return getPageContentById(uri.getItemId(), uri.getPublicationId());
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

//        List<PageMeta> itemMetas = null;


//        ODataClientQuery clientQuery = (new FunctionImportClientQuery.Builder()).withEntityType(String.class).withFunctionName("GetPageMetaListByUrlFunctionImport").withFunctionParameter("PublicationId", "" + publication).build();
//
//        new ODataClientFactoryImpl().create(null).getCollections(clientQuery);

//        String result = (String)ContentClientProvider.getInstance().getContentClient().getEntity("Edm.String", clientQuery);
//        if(StringUtils.isNotEmpty(new String[]{result})) {
//            return ITEM_SERIALIZER.deserialize(result);
//        } else {
//            LOG.debug("Could not find page metas: {}", clientQuery);
//            return new ArrayList();
//        }
        // TODO for web 8 REST. The oDataClient doesn't seem to be able to handle this yet.
        throw new NotImplementedException();

//        try {
//            ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
//            itemMetas = itemDAO.findAll(publication, ItemTypeSelector.PAGE);
//        } catch (StorageException e) {
//            LOG.error(e.getMessage(), e);
//        }
//
//        if (itemMetas == null || itemMetas.isEmpty()) {
//            throw new ItemNotFoundException("Unable to find page URL list by publication '" + publication + "'.");
//        }
//
//        StringBuilder result = new StringBuilder();
//        for (ItemMeta itemMeta : itemMetas) {
//            result.append(((PageMeta) itemMeta).getUrl()).append("\r\n");
//        }
//
//        return result.toString();


    }

    protected static WebPageMetaFactory getWebPageMetaFactory (final int publication) {
        WebPageMetaFactory webPageMetaFactory = WEB_PAGE_META_FACTORIES.get(publication);

        if (webPageMetaFactory == null) {
            webPageMetaFactory = new WebPageMetaFactoryImpl(publication);
            WEB_PAGE_META_FACTORIES.put(publication,webPageMetaFactory);
        }
        return webPageMetaFactory;
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

        final WebPageMetaFactory webPageMetaFactory = getWebPageMetaFactory(publication);
        final PageMeta pageMeta = webPageMetaFactory.getMeta(id);

        if (pageMeta == null) {
            throw new ItemNotFoundException("Unable to find page by id '" + id + "' and publication '" + publication + "'.");
        }
        return pageMeta;
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

        final WebPageMetaFactory webPageMetaFactory = getWebPageMetaFactory(publication);
        final PageMeta pageMeta = webPageMetaFactory.getMetaByURL(publication, url);

        if (pageMeta == null) {
            throw new ItemNotFoundException("Unable to find page by url '" + url + "' and publication '" + publication + "'.");
        }

        return pageMeta;
    }


    // TODO: introduce ProviderException
    @Override
    public boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {

        LOG.debug("Checking whether Page with url: {} exists", url);

        String key = getKey(CacheType.PAGE_EXISTS, url);
        CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = 0;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);


                    TCMURI tcmuri = null;

                    try {
                        final PageMeta pageMeta = getPageMetaByURL(url,publicationId);
                        if (pageMeta != null) {
                            result = 1;
                            tcmuri = new TCMURI(pageMeta.getPublicationId(),pageMeta.getId(),pageMeta.getType());
                        }
                    } catch (ItemNotFoundException e) {
                        LOG.trace(String.format("Page with url:%s does not seem to exist.",url),e);
                    }

                    if (result == 1) {
                        cacheElement.setPayload(result);
                        cacheProvider.storeInItemCache(key, cacheElement, tcmuri.getPublicationId(), tcmuri.getItemId());
                    } else {
                        result = 0;
                        cacheElement.setPayload(result);
                        cacheProvider.storeInItemCache(key, cacheElement);
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
        final PageMeta pageMeta = getPageMetaByURL(url, publicationId);
        if (pageMeta != null) {
            return new TCMURI(publicationId, pageMeta.getId(), pageMeta.getType(), pageMeta.getMajorVersion());
        }
        throw new ItemNotFoundException("Page Id for URL not found.");
    }

    @Override
    public DateTime getLastPublishDate (final String url, final int publication) throws ItemNotFoundException {
        final PageMeta pageMeta = getPageMetaByURL(url, publication);
        final Date lpd = pageMeta.getLastPublicationDate();
        return lpd != null ? new DateTime(lpd) : Constants.THE_YEAR_ZERO;
    }
}
