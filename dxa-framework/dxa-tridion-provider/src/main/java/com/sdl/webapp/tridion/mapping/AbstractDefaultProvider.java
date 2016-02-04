package com.sdl.webapp.tridion.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.ContentList;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.tridion.query.BrokerQuery;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.sdl.webapp.util.dd4t.TcmUtils.buildTcmUri;
import static com.sdl.webapp.util.dd4t.TcmUtils.buildTemplateTcmUri;


/**
 * Implementation of {@link com.sdl.webapp.common.api.content.ContentProvider} that uses DD4T to provide content.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class AbstractDefaultProvider implements ContentProvider, NavigationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDefaultProvider.class);

    private static final Object LOCK = new Object();

    private static final String DEFAULT_PAGE_NAME = "index";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String NAVIGATION_MODEL_URL = "/navigation.json";
    private static final String TYPE_STRUCTURE_GROUP = "StructureGroup";

    @Autowired
    private PageFactory dd4tPageFactory;

    @Autowired
    private ComponentPresentationFactory dd4tComponentPresentationFactory;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LinkResolver linkResolver;

    @Autowired
    private WebRequestContext webRequestContext;

    private static <T> T findPage(String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = processPath(path);
        final int publicationId = Integer.parseInt(localization.getId());

        LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
        T page = callback.tryFindPage(processedPath, publicationId);
        if (page == null && !path.endsWith("/") && !hasExtension(path)) {
            processedPath = processPath(path + '/');
            LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
            page = callback.tryFindPage(processedPath, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedPath);
        }

        return page;
    }

    private static String processPath(String path) {

        if (StringUtils.isEmpty(path)) {
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

    /**
     * <p>isToBeRefreshed.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param time a long.
     * @return a boolean.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    protected static boolean isToBeRefreshed(File file, long time) throws ContentProviderException {
        boolean refresh;
        if (file.exists()) {
            refresh = file.lastModified() < time;
        } else {
            refresh = true;
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new ContentProviderException("Failed to create parent directory for file: " + file);
                }
            }
        }
        return refresh;
    }

    /**
     * <p>writeToFile.</p>
     *
     * @param file     a {@link java.io.File} object.
     * @param pathInfo a {@link com.sdl.webapp.common.util.ImageUtils.StaticContentPathInfo} object.
     * @param content  an array of byte.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     * @throws java.io.IOException                                        if any.
     */
    protected static void writeToFile(File file, ImageUtils.StaticContentPathInfo pathInfo, byte[] content) throws ContentProviderException, IOException {
        if (pathInfo.isImage() && pathInfo.isResized()) {
            content = ImageUtils.resizeImage(content, pathInfo);
        }

        Files.write(content, file);
    }

    private static int mapSchema(String schemaKey, Localization localization) {
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

    private static String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private static boolean createBreadcrumbLinks(SitemapItem item, String requestPath, List<Link> links) {
        if (requestPath.startsWith(item.getUrl().toLowerCase())) {
            // Add link for this matching item
            links.add(createLinkForItem(item));

            // Add links for the following matching subitems
            boolean firstItem = true;
            for (SitemapItem subItem : item.getItems()) {
                // Fix to correct the breadcrumb
                // TODO: Implement this propertly
                if (firstItem) {
                    firstItem = false;
                } else {
                    if (createBreadcrumbLinks(subItem, requestPath, links)) {
                        return true;
                    }
                }
            }

            return true;
        }

        return false;
    }

    private static List<Link> createLinksForVisibleItems(Iterable<SitemapItem> items) {
        final List<Link> links = new ArrayList<>();
        for (SitemapItem item : items) {
            if (item.isVisible()) {
                links.add(createLinkForItem(item));
            }
        }
        return links;
    }

    private static Link createLinkForItem(SitemapItem item) {
        final Link link = new Link();
        link.setUrl(item.getUrl());
        link.setLinkText(item.getTitle());
        return link;
    }

    private static SitemapItem findContextNavigationStructureGroup(SitemapItem item, String requestPath) {
        if (item.getType().equals(TYPE_STRUCTURE_GROUP) && requestPath.startsWith(item.getUrl().toLowerCase())) {
            // Check if there is a matching subitem, if yes, then return it
            for (SitemapItem subItem : item.getItems()) {
                final SitemapItem matchingSubItem = findContextNavigationStructureGroup(subItem, requestPath);
                if (matchingSubItem != null) {
                    return matchingSubItem;
                }
            }

            // Otherwise return this matching item
            return item;
        }

        // No matching item
        return null;
    }

    /**
     * <p>getStaticContentFile.</p>
     *
     * @param file          a {@link java.io.File} object.
     * @param pathInfo      a {@link com.sdl.webapp.common.util.ImageUtils.StaticContentPathInfo} object.
     * @param publicationId a int.
     * @return a {@link com.sdl.webapp.tridion.mapping.AbstractDefaultProvider.StaticContentFile} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     * @throws java.io.IOException                                        if any.
     */
    protected abstract StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException;

    /** {@inheritDoc} */
    @Override
    public PageModel getPageModel(String path, final Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<PageModel>() {
            @Override
            public PageModel tryFindPage(String path, int publicationId) throws ContentProviderException {
                final org.dd4t.contentmodel.Page genericPage;
                try {
                    synchronized (LOCK) {
                        if (dd4tPageFactory.isPagePublished(path, publicationId)) {
                            genericPage = dd4tPageFactory.findPageByUrl(path, publicationId);
                        } else {
                            return null;
                        }
                    }
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                return modelBuilderPipeline.createPageModel(genericPage, localization, AbstractDefaultProvider.this);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public EntityModel getEntityModel(@NonNull String id, final Localization localization) throws DxaException {

        String[] idParts = id.split("-");
        if (idParts.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", id));
        }

        String componentUri = buildTcmUri(localization.getId(), idParts[0]);
        String templateUri = buildTemplateTcmUri(localization.getId(), idParts[1]);

        try {
            final ComponentPresentation componentPresentation;
            synchronized (LOCK) {
                componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(componentUri, templateUri);
            }
            EntityModel entityModel = modelBuilderPipeline.createEntityModel(componentPresentation, localization);
            if (entityModel.getXpmMetadata() != null) {
                entityModel.getXpmMetadata().put("IsQueryBased", true);
            }
            return entityModel;

        } catch (FactoryException e) {
            throw new DxaItemNotFoundException(id);
        } catch (ContentProviderException e) {
            throw new DxaException("Problem building entity model", e);
        }
    }

    private InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<InputStream>() {
            @Override
            public InputStream tryFindPage(String path, int publicationId) throws ContentProviderException {
                final String pageContent;
                try {
                    synchronized (LOCK) {
                        pageContent = dd4tPageFactory.findSourcePageByUrl(path, publicationId);
                    }
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page content for: [" + publicationId +
                            "] " + path, e);
                }

                // NOTE: This assumes page content is always in UTF-8 encoding
                return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void populateDynamicList(ContentList contentList, Localization localization) throws ContentProviderException {
        final BrokerQuery brokerQuery = instantiateBrokerQuery();
        brokerQuery.setStart(contentList.getStart());
        brokerQuery.setPublicationId(Integer.parseInt(localization.getId()));
        brokerQuery.setPageSize(contentList.getPageSize());
        if (contentList.getContentType() != null) {
            brokerQuery.setSchemaId(mapSchema(contentList.getContentType().getKey(), localization));
        } else {
            LOG.error("ContentList {} - ContentType is null", contentList.getId());
        }
        if (contentList.getSort() != null) {
            brokerQuery.setSort(contentList.getSort().getKey());
        } else {
            LOG.error("ContentList {} - Sort is null", contentList.getId());
        }

        // Execute query
        try {
            contentList.setItemListElements(brokerQuery.executeQuery());
        } catch (BrokerQueryException e) {
            throw new ContentProviderException(e);
        }

        // Resolve links
        for (Teaser item : contentList.getItemListElements()) {
            item.getLink().setUrl(linkResolver.resolveLink(item.getLink().getUrl(), null));
        }

        contentList.setHasMore(brokerQuery.isHasMore());
    }

    /**
     * {@inheritDoc}
     *
     * Implementation of {@code StaticContentProvider} that uses DD4T to provide static content.
     * <p>
     * TODO: Should use DD4T BinaryFactory instead of calling the Tridion broker API directly.
     * </p>
     */
    @Override
    public StaticContentItem getStaticContent(final String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getStaticContent: {} [{}] {}", path, localizationId, localizationPath);
        }

        final String contentPath;
        if (localizationPath.length() > 1) {
            contentPath = localizationPath + removeVersionNumber(path.startsWith(localizationPath) ?
                    path.substring(localizationPath.length()) : path);
        } else {
            contentPath = removeVersionNumber(path);
        }

        final StaticContentFile staticContentFile = getStaticContentFile(contentPath, localizationId);

        //noinspection ReturnOfInnerClass
        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return staticContentFile.getFile().lastModified();
            }

            @Override
            public String getContentType() {
                return staticContentFile.getContentType();
            }

            @Override
            public InputStream getContent() throws IOException {
                return new FileInputStream(staticContentFile.getFile());
            }

            @Override
            public boolean isVersioned() {
                return path.contains("/system/");
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        try {
            final String path = localization.localizePath(NAVIGATION_MODEL_URL);
            return resolveLinks(objectMapper.readValue(this.getPageContent(path, localization),
                    SitemapItem.class), localization);
        } catch (ContentProviderException | IOException e) {
            throw new NavigationProviderException("Exception while loading navigation model", e);
        }
    }

    private SitemapItem resolveLinks(SitemapItem sitemapItem, Localization localization) {
        sitemapItem.setUrl(linkResolver.resolveLink(sitemapItem.getUrl(), localization.getId()));

        for (SitemapItem subItem : sitemapItem.getItems()) {
            resolveLinks(subItem, localization);
        }

        return sitemapItem;
    }

    /** {@inheritDoc} */
    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);
        return new NavigationLinks(createLinksForVisibleItems(navigationModel.getItems()));
    }

    /** {@inheritDoc} */
    @Override
    public NavigationLinks getContextNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);
        final SitemapItem contextNavigationItem = findContextNavigationStructureGroup(navigationModel, requestPath);

        final List<Link> links = contextNavigationItem != null ?
                createLinksForVisibleItems(contextNavigationItem.getItems()) : Collections.<Link>emptyList();

        return new NavigationLinks(links);
    }

    /** {@inheritDoc} */
    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);

        final List<Link> links = new ArrayList<>();
        createBreadcrumbLinks(navigationModel, requestPath, links);

        return new NavigationLinks(links);
    }

    /**
     * <p>instantiateBrokerQuery.</p>
     *
     * @return a {@link com.sdl.webapp.tridion.query.BrokerQuery} object.
     */
    protected abstract BrokerQuery instantiateBrokerQuery();

    /**
     * <p>prependFullUrlIfNeeded.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String prependFullUrlIfNeeded(String path) {
        String baseUrl = webRequestContext.getBaseUrl();
        if (path.contains(baseUrl)) {
            return path;
        }
        return baseUrl + path;
    }

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        String parentPath = StringUtils.join(new String[]{
                webApplicationContext.getServletContext().getRealPath("/"), STATIC_FILES_DIR, localizationId
        }, File.separator);

        final File file = new File(parentPath, path);
        LOG.trace("getStaticContentFile: {}", file);

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            return getStaticContentFile(file, pathInfo, publicationId);
        } catch (IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }

    private interface TryFindPage<T> {
        T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }

    protected static final class StaticContentFile {
        private final File file;
        private final String contentType;

        protected StaticContentFile(File file, String contentType) {
            this.file = file;
            this.contentType = StringUtils.isEmpty(contentType) ? DEFAULT_CONTENT_TYPE : contentType;
        }

        public File getFile() {
            return file;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
