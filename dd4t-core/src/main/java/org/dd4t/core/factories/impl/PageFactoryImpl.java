package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.FilterException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.PageFactory;
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
     * @param uri     of the page
     * @return
     * @throws ItemNotFoundException
     * @throws FilterException
     * @throws ParseException
     * @throws SerializationException
     */
    @Override
    public GenericPage getPage(String uri) throws FactoryException{
        LOG.debug("Enter getPage with uri: {}", uri);

        CacheElement<GenericPage> cacheElement = cacheProvider.loadFromLocalCache(uri);
        GenericPage page;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
	                String pageSource = null;
	                try {
		                pageSource = pageProvider.getPageContentById(uri);
	                } catch (ItemNotFoundException | ParseException | SerializationException | IOException e) {
		               throw new FactoryException(e);
	                }

	                if (pageSource == null || pageSource.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(uri, cacheElement);
                        throw new FactoryException("Unable to find page by id " + uri);
                    }

	                try {
		                page = deserialize(pageSource, PageImpl.class);
		                cacheElement.setPayload(page);

		                TCMURI tcmUri = new TCMURI(uri);
		                cacheProvider.storeInItemCache(uri, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
		                LOG.debug("Added page with uri: {} to cache", uri);
	                } catch (SerializationException | ParseException e) {
		               throw new FactoryException(e);
	                }

                } else {
                    LOG.debug("Return a page with uri: {} from cache", uri);
                    page = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return page with uri: {} from cache", uri);
            page = cacheElement.getPayload();
        }
        return page;
    }

    /**
     * @param url the url of the page
     * @param publicationId the publication Id
     * @return a GenericPage object
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    @Override
    public GenericPage findPageByUrl(String url, int publicationId) throws FactoryException{
        LOG.debug("Enter findPageByUrl with url: {} and publicationId: {}", url, publicationId);

        String cacheKey = publicationId + "-" + url;
        CacheElement<GenericPage> cacheElement = cacheProvider.loadFromLocalCache(cacheKey);
        GenericPage page;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
	                String pageSource = null;
	                try {
		                pageSource = pageProvider.getPageContentByURL(url, publicationId);
	                } catch (ItemNotFoundException| SerializationException | IOException e) {
		                throw new FactoryException(e);
	                }
	                if (pageSource == null || pageSource.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(cacheKey, cacheElement);
                        throw new FactoryException("Page with url: " + url + " not found.");
                    }

	                try {
		                page = deserialize(pageSource, PageImpl.class);

		                cacheElement.setPayload(page);

		                TCMURI tcmUri = new TCMURI(page.getId());
		                cacheProvider.storeInItemCache(cacheKey, cacheElement, publicationId, tcmUri.getItemId());
		                LOG.debug("Added page with uri: {} and publicationId: {} to cache", url, publicationId);
	                } catch (SerializationException | ParseException e) {
		                throw new FactoryException(e);
	                }

                } else {
                    LOG.debug("Return a page with url: {} and publicationId: {} from cache", url, publicationId);
                    page = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return page with url: {} and publicationId: {} from cache", url, publicationId);
            page = cacheElement.getPayload();
        }

        return page;
    }


    /**
     * This method explicitly used for querying the Broker Storage
     * for pages and returns raw content as string.
     *
     * To handle nulls, check for a returned null in the controller.
     *
     * @param url the url of the page
     * @param publicationId the publication Id
     * @return XML as string
     */
    @Override
    public String findSourcePageByUrl(String url, int publicationId) throws FactoryException {
        LOG.debug("Enter findXMLPageByUrl with url: {} and publicationId: {}", url, publicationId);

        String cacheKey = publicationId + "-" + url;
        CacheElement<String> cacheElement = cacheProvider.loadFromLocalCache(cacheKey);

        String page = null;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
	                try {
		                page = pageProvider.getPageContentByURL(url, publicationId);
	                } catch (ItemNotFoundException | SerializationException | IOException e) {
		                throw new FactoryException(e);
	                }
	                if (page == null || page.length() == 0) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(cacheKey, cacheElement);
                        throw new FactoryException("XML Page with url: " + url + " not found.");
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
            throw new FactoryException("Page with url: " + url + " not found.");
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