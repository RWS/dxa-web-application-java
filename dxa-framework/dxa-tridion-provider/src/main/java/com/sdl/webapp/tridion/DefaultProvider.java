package com.sdl.webapp.tridion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
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
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Implementation of {@link ContentProvider} that uses DD4T to provide content.
 */
@Component
@Slf4j
public final class DefaultProvider implements ContentProvider, NavigationProvider {
    private static final String DEFAULT_PAGE_NAME = "index";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String NAVIGATION_MODEL_URL = "/navigation.json";
    private static final String TYPE_STRUCTURE_GROUP = "StructureGroup";

    private static final Object LOCK = new Object();

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
    private DynamicMetaRetriever dynamicMetaRetriever;

    @Autowired
    private BinaryFactory binaryFactory;

    @Autowired
    private WebRequestContext webRequestContext;

    private static <T> T findPage(String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = processPath(path);
        final int publicationId = Integer.parseInt(localization.getId());

        log.debug("Try to find page: [{}] {}", publicationId, processedPath);
        T page = callback.tryFindPage(processedPath, publicationId);
        if (page == null && !path.endsWith("/") && !hasExtension(path)) {
            processedPath = processPath(path + "/");
            log.debug("Try to find page: [{}] {}", publicationId, processedPath);
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

    @Override
    public PageModel getPageModel(String path, final Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<PageModel>() {
            @Override
            public PageModel tryFindPage(String path, int publicationId) throws ContentProviderException {
                final org.dd4t.contentmodel.Page genericPage;
                try {
                    if (dd4tPageFactory.isPagePublished(path, publicationId)) {
                        genericPage = dd4tPageFactory.findPageByUrl(path, publicationId);
                    } else {
                        return null;
                    }
                } catch (ItemNotFoundException e) {
                    log.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                return modelBuilderPipeline.createPageModel(genericPage, localization, DefaultProvider.this);
            }
        });
    }

    @Override
    public EntityModel getEntityModel(String id, final Localization localization) throws DxaException {

        String[] idParts = id.split("-");
        if (idParts.length != 2) {
            throw new DxaException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", id));
        }
        String componentUri = String.format("tcm:%s-%s", localization.getId(), idParts[0]);
        String templateUri = String.format("tcm:%s-%s-32", localization.getId(), idParts[1]);
        try {
            final ComponentPresentation componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(componentUri, templateUri);
            return modelBuilderPipeline.createEntityModel(componentPresentation, localization);

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
                    pageContent = dd4tPageFactory.findSourcePageByUrl(path, publicationId);
                } catch (ItemNotFoundException e) {
                    log.debug("Page not found: [{}] {}", publicationId, path);
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

    @Override
    public void populateDynamicList(ContentList contentList, Localization localization) throws ContentProviderException {
        final BrokerQuery brokerQuery = new BrokerQuery();
        brokerQuery.setStart(contentList.getStart());
        brokerQuery.setPublicationId(Integer.parseInt(localization.getId()));
        brokerQuery.setPageSize(contentList.getPageSize());
        if (contentList.getContentType() != null) {
            brokerQuery.setSchemaId(mapSchema(contentList.getContentType().getKey(), localization));
        } else {
            log.error("ContentList {} - ContentType is null", contentList.getId());
        }
        if (contentList.getSort() != null) {
            brokerQuery.setSort(contentList.getSort().getKey());
        } else {
            log.error("ContentList {} - Sort is null", contentList.getId());
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

    private int mapSchema(String schemaKey, Localization localization) {
        final String[] parts = schemaKey.split("\\.");
        final String configKey = parts.length > 1 ? (parts[0] + ".schemas." + parts[1]) : ("core.schemas." + parts[0]);
        final String schemaId = localization.getConfiguration(configKey);
        try {
            return Integer.parseInt(schemaId);
        } catch (NumberFormatException e) {
            log.warn("Error while parsing schema id: {}", schemaId, e);
            return 0;
        }
    }

    /**
     * Implementation of {@code StaticContentProvider} that uses DD4T to provide static content.
     * <p/>
     * TODO: Should use DD4T BinaryFactory instead of calling the Tridion broker API directly.
     */

    @Override
    public StaticContentItem getStaticContent(String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        if (log.isTraceEnabled()) {
            log.trace("getStaticContent: {} [{}] {}", path, localizationId, localizationPath);
        }

        final String contentPath;
        if (localizationPath.length() > 1) {
            contentPath = localizationPath + removeVersionNumber(path.startsWith(localizationPath) ?
                    path.substring(localizationPath.length()) : path);
        } else {
            contentPath = removeVersionNumber(path);
        }

        final StaticContentFile staticContentFile = getStaticContentFile(contentPath, localizationId);

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
        };
    }

    private String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private String prependFullUrlIfNeeded(String path) {
        String baseUrl = webRequestContext.getBaseUrl();
        if (path.contains(baseUrl)) {
            return path;
        }
        return baseUrl + path;
    }

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        final File file = new File(new File(new File(new File(
                webApplicationContext.getServletContext().getRealPath("/")), STATIC_FILES_DIR), localizationId), path);
        log.trace("getStaticContentFile: {}", file);

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            BinaryMeta binaryMeta;
            ComponentMetaFactory factory = new ComponentMetaFactory(publicationId);
            ComponentMeta componentMeta;
            int itemId;
            synchronized (LOCK) {
                binaryMeta = dynamicMetaRetriever.getBinaryMetaByURL(prependFullUrlIfNeeded(pathInfo.getFileName()));
                if (binaryMeta == null) {
                    throw new StaticContentNotFoundException("No binary meta found for: [" + publicationId + "] " +
                            pathInfo.getFileName());
                }
                itemId = (int) binaryMeta.getURI().getItemId();
                componentMeta = factory.getMeta(itemId);
                if (componentMeta == null) {
                    throw new StaticContentNotFoundException("No meta meta found for: [" + publicationId + "] " +
                            pathInfo.getFileName());
                }
            }

            boolean refresh;
            if (file.exists()) {
                refresh = file.lastModified() < componentMeta.getLastPublicationDate().getTime();
            } else {
                refresh = true;
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new ContentProviderException("Failed to create parent directory for file: " + file);
                    }
                }
            }

            if (refresh) {
                BinaryData binaryData = binaryFactory.getBinary(publicationId, itemId, binaryMeta.getVariantId());

                byte[] content = binaryData.getBytes();
                if (pathInfo.isImage() && pathInfo.isResized()) {
                    content = ImageUtils.resizeImage(content, pathInfo);
                }

                log.debug("Writing binary content to file: {}", file);
                Files.write(file.toPath(), content);
            } else {
                log.debug("File does not need to be refreshed: {}", file);
            }

            return new StaticContentFile(file, binaryMeta.getType());
        } catch (IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }

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

    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);
        return new NavigationLinks(createLinksForVisibleItems(navigationModel.getItems()));
    }

    @Override
    public NavigationLinks getContextNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);
        final SitemapItem contextNavigationItem = findContextNavigationStructureGroup(navigationModel, requestPath);

        final List<Link> links = contextNavigationItem != null ?
                createLinksForVisibleItems(contextNavigationItem.getItems()) : Collections.<Link>emptyList();

        return new NavigationLinks(links);
    }

    private SitemapItem findContextNavigationStructureGroup(SitemapItem item, String requestPath) {
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

    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);

        final List<Link> links = new ArrayList<>();
        createBreadcrumbLinks(navigationModel, requestPath, links);

        return new NavigationLinks(links);
    }

    private boolean createBreadcrumbLinks(SitemapItem item, String requestPath, List<Link> links) {
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

    private List<Link> createLinksForVisibleItems(Iterable<SitemapItem> items) {
        final List<Link> links = new ArrayList<>();
        for (SitemapItem item : items) {
            if (item.isVisible()) {
                links.add(createLinkForItem(item));
            }
        }
        return links;
    }

    private Link createLinkForItem(SitemapItem item) {
        final Link link = new Link();
        link.setUrl(item.getUrl());
        link.setLinkText(item.getTitle());
        return link;
    }

    private interface TryFindPage<T> {
        T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }

    private static final class StaticContentFile {
        private final File file;
        private final String contentType;

        private StaticContentFile(File file, String contentType) {
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
