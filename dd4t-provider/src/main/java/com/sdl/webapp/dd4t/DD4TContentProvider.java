package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.entity.ContentList;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sdl.webapp.tridion.query.BrokerQuery;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    public static final String DEFAULT_PAGE_NAME = "index";
    public static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static interface TryFindPage<T> {
        public T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    private final ComponentPresentationFactory dd4tComponentPresentationFactory;

    private final PageBuilder pageBuilder;
    private final EntityBuilder entityBuilder;

    private final ContentResolver contentResolver;

    @Autowired
    public DD4TContentProvider(PageFactory dd4tPageFactory,
                               ComponentPresentationFactory dd4tComponentPresentationFactory,
                               PageBuilder pageBuilder,
                               EntityBuilder entityBuilder,
                               ContentResolver contentResolver) {

        this.dd4tPageFactory = dd4tPageFactory;
        this.dd4tComponentPresentationFactory = dd4tComponentPresentationFactory;
        this.pageBuilder = pageBuilder;
        this.entityBuilder = entityBuilder;
        this.contentResolver = contentResolver;
    }

    @Override
    public Page getPageModel(String path, final Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<Page>() {
            @Override
            public Page tryFindPage(String path, int publicationId) throws ContentProviderException {
                final org.dd4t.contentmodel.Page genericPage;
                try {
                    genericPage = dd4tPageFactory.findPageByUrl(path, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                return pageBuilder.createPage(genericPage, localization, DD4TContentProvider.this);
            }
        });
    }

    @Override
    public Entity getEntityModel(String id, String templateId, final Localization localization) throws ContentProviderException {

        // TODO: Use just id's here instead of TCM-URIs

        try {
            final ComponentPresentation componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(id, templateId);
            return this.entityBuilder.createEntity(componentPresentation, localization);
        }
        catch ( ItemNotFoundException e ) {
            LOG.debug("Dynamic component not found: ID: {} Template ID: {}", id, templateId);
            return null;
        }
        catch (FactoryException e) {
            throw new ContentProviderException("Exception while getting entity model for: " + id, e);
        }
    }


    @Override
    public InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<InputStream>() {
            @Override
            public InputStream tryFindPage(String path, int publicationId) throws ContentProviderException {
                final String pageContent;
                try {
                    pageContent = dd4tPageFactory.findSourcePageByUrl(path, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page content for: [" +  publicationId +
                            "] " + path, e);
                }

                // NOTE: This assumes page content is always in UTF-8 encoding
                return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private static <T> T findPage(String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = processPath(path);
        final int publicationId = Integer.parseInt(localization.getId());

        LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
        T page = callback.tryFindPage(processedPath, publicationId);
        if (page == null && !path.endsWith("/") && !hasExtension(path)) {
            processedPath = processPath(path + "/");
            LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
            page = callback.tryFindPage(processedPath, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedPath);
        }

        return page;
    }

    private static String processPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return DEFAULT_PAGE_NAME + DEFAULT_PAGE_EXTENSION;
        }
        if (path.endsWith("/")) {
            path = path + DEFAULT_PAGE_NAME + DEFAULT_PAGE_EXTENSION;
        }
        if (!hasExtension(path)) {
            path = path + DEFAULT_PAGE_EXTENSION;
        }
        return path;
    }

    private static boolean hasExtension(String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }

    @Override
    public void populateDynamicList(ContentList contentList, Localization localization) throws ContentProviderException {
        final BrokerQuery brokerQuery = new BrokerQuery();
        brokerQuery.setStart(contentList.getStart());
        brokerQuery.setPublicationId(Integer.parseInt(localization.getId()));
        brokerQuery.setPageSize(contentList.getPageSize());
        brokerQuery.setSchemaId(mapSchema(contentList.getContentType().getKey(), localization));
        brokerQuery.setSort(contentList.getSort().getKey());

        // Execute query
        try {
            contentList.setItemListElements(brokerQuery.executeQuery());
        } catch (BrokerQueryException e) {
            throw new ContentProviderException(e);
        }

        // Resolve links
        for (Teaser item : contentList.getItemListElements()) {
            item.getLink().setUrl(contentResolver.resolveLink(item.getLink().getUrl(), null));
        }

        contentList.setHasMore(brokerQuery.isHasMore());
    }

    private int mapSchema(String schemaKey, Localization localization) {
        final String[] parts = schemaKey.split("\\.");
        final String configKey = parts.length > 1 ? (parts[0] + ".schemas." + parts[1]) : ("core.schemas." + parts[0]);
        final String schemaId = localization.getConfiguration(configKey);
        try {
            return Integer.parseInt(schemaId);
        } catch (NumberFormatException e) {
            LOG.warn("Error while parsing schema id: {}", schemaId, e);
            return 0;
        }
    }
}
