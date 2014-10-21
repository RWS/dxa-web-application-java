package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.PageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;

public class PageFactoryImpl extends BaseFactory implements PageFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PageFactoryImpl.class);
    private static final PageFactoryImpl INSTANCE = new PageFactoryImpl();
    @Autowired
    protected PageProvider pageProvider;

    protected PageFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static PageFactoryImpl getInstance() {
        return INSTANCE;
    }

    /**
     * @param tcmUri TCM Uri of page
     * @return the Page object
     * @throws ItemNotFoundException
     * @throws SerializationException
     * @throws ParseException
     * @throws FilterException
     */
    public GenericPage getPage(String tcmUri) throws ItemNotFoundException, SerializationException, ParseException, FilterException {
        return getPage(tcmUri, null);
    }

    /**
     * @param uri     of the page
     * @param context (normally wrapped around the HttpServletRequest)
     * @return
     * @throws ItemNotFoundException
     * @throws FilterException
     * @throws ParseException
     * @throws SerializationException
     */
    @Override
    public GenericPage getPage(String uri, RequestContext context)
            throws ItemNotFoundException, FilterException, ParseException, SerializationException {
        LOG.debug("Enter getPage with uri: {}", uri);

        CacheElement<GenericPage> cacheElement = cacheProvider.loadFromLocalCache(uri);
        GenericPage page;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    String pageSource = pageProvider.getPageContentById(uri, context);
                    if (pageSource == null || pageSource.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(uri, cacheElement);
                        throw new ItemNotFoundException("Unable to find page by id " + uri);
                    }

                    page = deserialize(pageSource, PageImpl.class);
                    cacheElement.setPayload(page);

                    TCMURI tcmUri = new TCMURI(uri);
                    cacheProvider.storeInItemCache(uri, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                    LOG.debug("Added page with uri: {} to cache", uri);
                } else {
                    LOG.debug("Return a page with uri: {} from cache", uri);
                    page = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return page with uri: {} from cache", uri);
            page = cacheElement.getPayload();
        }

        if (page == null) {
            throw new ItemNotFoundException("Unable to find page by id " + uri);
        }
        return page;
    }

    /**
     * @param url
     * @param publicationId
     * @return
     * @throws ItemNotFoundException
     * @throws FilterException
     * @throws SerializationException
     */
    @Override
    public GenericPage findPageByUrl(String url, int publicationId) throws ItemNotFoundException, FilterException, ParseException, SerializationException, IOException {
        LOG.debug("Enter findPageByUrl with url: {} and publicationId: {}", url, publicationId);

        String cacheKey = publicationId + "-" + url;
        CacheElement<GenericPage> cacheElement = cacheProvider.loadFromLocalCache(cacheKey);
        GenericPage page;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    String pageSource = pageProvider.getPageContentByURL(url, publicationId);
                    if (pageSource == null || pageSource.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(cacheKey, cacheElement);
                        throw new ItemNotFoundException("Page with url: " + url + " not found.");
                    }

                    page = deserialize(pageSource, PageImpl.class);




                    cacheElement.setPayload(page);

                    TCMURI tcmUri = new TCMURI(page.getId());
                    cacheProvider.storeInItemCache(cacheKey, cacheElement, publicationId, tcmUri.getItemId());
                    LOG.debug("Added page with uri: {} and publicationId: {} to cache", url, publicationId);
                } else {
                    LOG.debug("Return a page with url: {} and publicationId: {} from cache", url, publicationId);
                    page = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return page with url: {} and publicationId: {} from cache", url, publicationId);
            page = cacheElement.getPayload();
        }

        if (page == null) {
            throw new ItemNotFoundException("Page with url: " + url + " not found.");
        }
        return page;
    }


    /**
     * This method explicitly used for querying hammerfest ( broker/storage) for XML pages and returns XML content as string
     *
     * @param url
     * @param publicationId
     * @return XML as string
     * @throws ItemNotFoundException
     * @throws FilterException
     * @throws SerializationException
     * @throws ParseException
     */
    @Override
    public String findXMLPageByUrl(String url, int publicationId) throws ItemNotFoundException, FilterException, ParseException, SerializationException, IOException {
        LOG.debug("Enter findXMLPageByUrl with url: {} and publicationId: {}", url, publicationId);

        String cacheKey = publicationId + "-" + url;
        CacheElement<String> cacheElement = cacheProvider.loadFromLocalCache(cacheKey);

        String page = null;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    page = pageProvider.getPageContentByURL(url, publicationId);
                    if (page == null || page.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(cacheKey, cacheElement);
                        throw new ItemNotFoundException("XML Page with url: " + url + " not found.");
                    }

                    cacheElement.setPayload(page);

                    cacheProvider.storeInItemCache(cacheKey, cacheElement);
                    LOG.debug("Added XML page with uri: {} and publicationId: {} to cache", url, publicationId);
                } else {
                    LOG.debug("Return a XML page with url: {} and publicationId: {} from cache", url, publicationId);
                    page = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return XML page with url: {} and publicationId: {} from cache", url, publicationId);
            page = cacheElement.getPayload();
        }

        if (page == null) {
            throw new ItemNotFoundException("Page with url: " + url + " not found.");
        }
        return page;
    }


    /**
     * Method to check whether a page exists in the Tridion Broker.
     *
     * @param url
     * @param publicationId
     * @return
     * @throws SerializationException
     * @throws ItemNotFoundException
     */
    public Boolean isPagePublished(String url, int publicationId) throws ItemNotFoundException, SerializationException {
        LOG.debug("Enter isPagePublished with url: {} and publicationId: {}", url, publicationId);
        return pageProvider.checkPageExists(url, publicationId);
    }

    public void setPageProvider(PageProvider provider) {
        this.pageProvider = provider;
    }
}