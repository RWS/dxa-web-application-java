package com.sdl.webapp.tridion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
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
import com.sdl.webapp.tridion.query.BrokerQuery;
import com.sdl.webapp.tridion.query.BrokerQueryException;
import com.tridion.broker.StorageException;
import com.tridion.storage.BinaryContent;
import com.tridion.storage.BinaryMeta;
import com.tridion.storage.BinaryVariant;
import com.tridion.storage.ItemMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
import com.tridion.storage.dao.ItemDAO;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import javax.imageio.ImageIO;


/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DefaultProvider implements ContentProvider, NavigationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultProvider.class);

    public static final String DEFAULT_PAGE_NAME = "index";
    public static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static interface TryFindPage<T> {
        public T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    private final ComponentPresentationFactory dd4tComponentPresentationFactory;

//    private final PageBuilder pageBuilder;
//    private final EntityBuilder entityBuilder;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    private final LinkResolver linkResolver;

    @Autowired
    public DefaultProvider(PageFactory dd4tPageFactory,
                               ComponentPresentationFactory dd4tComponentPresentationFactory,
                               //PageBuilder pageBuilder,
                               //EntityBuilder entityBuilder,
                               LinkResolver linkResolver,
                               ObjectMapper objectMapper,
                               WebApplicationContext webApplicationContext) {

        this.dd4tPageFactory = dd4tPageFactory;
        this.dd4tComponentPresentationFactory = dd4tComponentPresentationFactory;
        //this.pageBuilder = pageBuilder;
        //this.entityBuilder = entityBuilder;

        this.linkResolver = linkResolver;
        this.objectMapper = objectMapper;
        this.webApplicationContext = webApplicationContext;
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
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                return modelBuilderPipeline.CreatePageModel(genericPage, localization, DefaultProvider.this);
            }
        });
    }

    @Override
    public EntityModel getEntityModel(String id, final Localization localization) throws DxaException {

        String [] idParts = id.split("-");
        if(idParts.length != 2)
        {
            throw new DxaException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", id));
        }
        String componentUri = String.format("tcm:%s-%s", localization.getId(), idParts[0]);
        String templateUri = String.format("tcm:%s-%s-32", localization.getId(), idParts[1]);
        try {
            final ComponentPresentation componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(componentUri, templateUri);
            return modelBuilderPipeline.CreateEntityModel(componentPresentation, localization);

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
            LOG.warn("Error while parsing schema id: {}", schemaId, e);
            return 0;
        }
    }
    
    
    
    
    /* staticcontentprovider code */
    /**
     * Implementation of {@code StaticContentProvider} that uses DD4T to provide static content.
     * <p/>
     * TODO: Should use DD4T BinaryFactory instead of calling the Tridion broker API directly.
     */

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final class StaticContentFile {
        private final File file;
        private final String contentType;

        private StaticContentFile(File file, String contentType) {
            this.file = file;
            this.contentType = Strings.isNullOrEmpty(contentType) ? DEFAULT_CONTENT_TYPE : contentType;
        }

        public File getFile() {
            return file;
        }

        public String getContentType() {
            return contentType;
        }
    }

    private final WebApplicationContext webApplicationContext;

    @Override
    public StaticContentItem getStaticContent(String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getStaticContent: {} [{}] {}", new Object[]{path, localizationId, localizationPath});
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

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        final File file = new File(new File(new File(new File(
                webApplicationContext.getServletContext().getRealPath("/")), STATIC_FILES_DIR), localizationId), path);
        LOG.trace("getStaticContentFile: {}", file);

        final StaticContentPathInfo pathInfo = new StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            final BinaryVariant binaryVariant = findBinaryVariant(publicationId, pathInfo.getFileName());
            if (binaryVariant == null) {
                throw new StaticContentNotFoundException("No binary variant found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
            }

            final BinaryMeta binaryMeta = binaryVariant.getBinaryMeta();
            final ItemMeta itemMeta = findItemMeta(binaryMeta.getPublicationId(), binaryMeta.getItemId());

            boolean refresh;
            if (file.exists()) {
                refresh = file.lastModified() < itemMeta.getLastPublishDate().getTime();
            } else {
                refresh = true;
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new ContentProviderException("Failed to create parent directory for file: " + file);
                    }
                }
            }

            if (refresh) {
                final BinaryContent binaryContent = findBinaryContent(itemMeta.getPublicationId(), itemMeta.getItemId(),
                        binaryVariant.getVariantId());

                byte[] content = binaryContent.getContent();
                if (pathInfo.isImage() && pathInfo.isResized()) {
                    content = resizeImage(content, pathInfo);
                }

                LOG.debug("Writing binary content to file: {}", file);
                Files.write(file.toPath(), content);
            } else {
                LOG.debug("File does not need to be refreshed: {}", file);
            }

            return new StaticContentFile(file, binaryVariant.getBinaryType());
        } catch (StorageException | IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }

    private BinaryVariant findBinaryVariant(int publicationId, String path) throws StorageException {
        final BinaryVariantDAO dao = (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_VARIANT);
        return dao.findByURL(publicationId, path);
    }

    private ItemMeta findItemMeta(int publicationId, int itemId) throws StorageException {
        final ItemDAO dao = (ItemDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.ITEM_META);
        return dao.findByPrimaryKey(publicationId, itemId);
    }

    private BinaryContent findBinaryContent(int publicationId, int itemId, String variantId) throws StorageException {
        final BinaryContentDAO dao = (BinaryContentDAO) StorageManagerFactory.getDAO(publicationId,
                StorageTypeMapping.BINARY_CONTENT);
        return dao.findByPrimaryKey(publicationId, itemId, variantId);
    }

    private byte[] resizeImage(byte[] original, StaticContentPathInfo pathInfo) throws ContentProviderException {
        try {
            final BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(original));

            int cropX = 0, cropY = 0;
            int sourceW = originalImage.getWidth(), sourceH = originalImage.getHeight();
            int targetW, targetH;

            // Most complex case is if a height AND width is specified
            if (pathInfo.getWidth() > 0 && pathInfo.getHeight() > 0) {
                if (pathInfo.isNoStretch()) {
                    // If we don't want to stretch, then we crop
                    float originalAspect = (float) sourceW / (float) sourceH;
                    float targetAspect = (float) pathInfo.getWidth() / (float) pathInfo.getHeight();
                    if (targetAspect < originalAspect) {
                        // Crop the width - ensuring that we do not stretch if the requested height is bigger than the original
                        targetH = Math.min(pathInfo.getHeight(), sourceH); // pathInfo.getHeight() > sourceH ? sourceH : pathInfo.getHeight();
                        targetW = (int) Math.ceil(targetH * targetAspect);
                        cropX = (int) Math.ceil((sourceW - (sourceH * targetAspect)) / 2);
                        sourceW = sourceW - 2 * cropX;
                    } else {
                        // Crop the height - ensuring that we do not stretch if the requested width is bigger than the original
                        targetW = Math.min(pathInfo.getWidth(), sourceW); // pathInfo.getWidth() > sourceW ? sourceW : pathInfo.getWidth();
                        targetH = (int) Math.ceil(targetW / targetAspect);
                        cropY = (int) Math.ceil((sourceH - (sourceW / targetAspect)) / 2);
                        sourceH = sourceH - 2 * cropY;
                    }
                } else {
                    // We stretch to fit the dimensions
                    targetH = pathInfo.getHeight();
                    targetW = pathInfo.getWidth();
                }
            } else if (pathInfo.getWidth() > 0) {
                // If we simply have a certain width or height, its simple: We just use that and derive the other
                // dimension from the original image aspect ratio. We also check if the target size is bigger than
                // the original, and if we allow stretching.
                targetW = (pathInfo.isNoStretch() && pathInfo.getWidth() > sourceW) ?
                        sourceW : pathInfo.getWidth();
                targetH = (int) (sourceH * ((float) targetW / (float) sourceW));
            } else {
                targetH = (pathInfo.isNoStretch() && pathInfo.getHeight() > sourceH) ?
                        sourceH : pathInfo.getHeight();
                targetW = (int) (sourceW * ((float) targetH / (float) sourceH));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Image: {}, cropX = {}, cropY = {}, sourceW = {}, sourceH = {}, targetW = {}, targetH = {}",
                        new Object[]{pathInfo.getFileName(), cropX, cropY, sourceW, sourceH, targetW, targetH});
            }

            if (targetW == sourceW && targetH == sourceH) {
                // No resize required
                return original;
            }

            final BufferedImage target = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);

            final Graphics2D graphics = target.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            final AffineTransform transform = new AffineTransform();
            transform.scale((double) targetW / (double) sourceW, (double) targetH / (double) sourceH);
            transform.translate(-cropX, -cropY);

            graphics.drawRenderedImage(originalImage, transform);

            graphics.dispose();

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(target, pathInfo.getImageFormatName(), out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ContentProviderException("Exception while processing image data", e);
        }
    }


    private static final String NAVIGATION_MODEL_URL = "/navigation.json";

    private static final String TYPE_STRUCTURE_GROUP = "StructureGroup";

    private final ObjectMapper objectMapper;

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


}
