package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.model.Page;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.filters.FilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    private static final String DEFAULT_PAGE_NAME = "index.html";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static interface TryFindPage<T> {
        public T tryFindPage(String url, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    // TODO: Get rid of circular dependency between DD4TContentProvider and PageFactoryImpl, this prevents constructor injection
    @Autowired
    private PageFactory pageFactory;

    @Autowired
    public DD4TContentProvider(org.dd4t.core.factories.PageFactory dd4tPageFactory) {
        this.dd4tPageFactory = dd4tPageFactory;
    }

    @Override
    public Page getPageModel(String url, final Localization localization) throws ContentProviderException {
        return findPage(url, localization, new TryFindPage<Page>() {
            @Override
            public Page tryFindPage(String url, int publicationId) throws ContentProviderException {
                GenericPage page;
                try {
                    page = (GenericPage) dd4tPageFactory.findPageByUrl(url, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, url);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" +
                            publicationId + "] " + url, e);
                }

                return pageFactory.createPage(page, localization);
            }
        });
    }

    @Override
    public InputStream getPageContent(String url, Localization localization) throws ContentProviderException {
        return findPage(url, localization, new TryFindPage<InputStream>() {
            @Override
            public InputStream tryFindPage(String url, int publicationId) throws ContentProviderException {
                final String page;
                try {
                    // NOTE: The method is called 'findXMLPageByUrl' but it does actually not have anything to do with XML
                    page = dd4tPageFactory.findXMLPageByUrl(url, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, url);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page content for: [" +
                            publicationId + "] " + url, e);
                }

                // NOTE: This assumes page content is always in UTF-8 encoding
                return new ByteArrayInputStream(page.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private static <T> T findPage(String url, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedUrl = processUrl(url);
        int publicationId = Integer.parseInt(localization.getId());

        LOG.debug("Try to find page: [{}] {}", publicationId, processedUrl);
        T page = callback.tryFindPage(processedUrl, publicationId);
        if (page == null && !url.endsWith("/") && !hasExtension(url)) {
            processedUrl = processUrl(url + "/");
            LOG.debug("Try to find page: [{}] {}", publicationId, processedUrl);
            page = callback.tryFindPage(processedUrl, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedUrl);
        }

        return page;
    }

    private static String processUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return DEFAULT_PAGE_NAME;
        }
        if (url.endsWith("/")) {
            url = url + DEFAULT_PAGE_NAME;
        }
        if (!hasExtension(url)) {
            url = url + DEFAULT_PAGE_EXTENSION;
        }
        return url;
    }

    private static boolean hasExtension(String url) {
        return url.lastIndexOf('.') > url.lastIndexOf('/');
    }
}
