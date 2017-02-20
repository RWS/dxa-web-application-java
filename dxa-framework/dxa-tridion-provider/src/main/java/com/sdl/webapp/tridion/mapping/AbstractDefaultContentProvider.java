package com.sdl.webapp.tridion.mapping;

import com.sdl.web.api.broker.querying.sorting.BrokerSortColumn;
import com.sdl.web.api.broker.querying.sorting.CustomMetaKeyColumn;
import com.sdl.web.api.broker.querying.sorting.SortParameter;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.web.api.meta.WebComponentMetaFactory;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.util.FileUtils;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.common.util.LocalizationUtils;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.MetadataType;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.PageURLCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.criteria.taxonomy.TaxonomyKeywordCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.filter.PagingFilter;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.data.BinaryData;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.NameValuePair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.webapp.common.util.ImageUtils.writeToFile;

@Slf4j
public abstract class AbstractDefaultContentProvider implements ContentProvider {

    private static final Object LOCK = new Object();

    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final WebRequestContext webRequestContext;

    private final LinkResolver linkResolver;

    private final WebApplicationContext webApplicationContext;

    private final DynamicMetaRetriever dynamicMetaRetriever;

    private final BinaryContentRetriever binaryContentRetriever;

    @Autowired
    public AbstractDefaultContentProvider(WebRequestContext webRequestContext,
                                          LinkResolver linkResolver,
                                          WebApplicationContext webApplicationContext,
                                          DynamicMetaRetriever dynamicMetaRetriever,
                                          BinaryContentRetriever binaryContentRetriever) {
        this.webRequestContext = webRequestContext;
        this.linkResolver = linkResolver;
        this.webApplicationContext = webApplicationContext;
        this.dynamicMetaRetriever = dynamicMetaRetriever;
        this.binaryContentRetriever = binaryContentRetriever;
    }

    @Override
    @SneakyThrows(UnsupportedEncodingException.class)
    public PageModel getPageModel(String path, Localization localization) throws ContentProviderException {
        String _path = UriUtils.encodePath(path, "UTF-8");
        PageModel pageModel = LocalizationUtils.findPageByPath(_path, localization, _loadPageCallback());


        if (pageModel != null) {
            pageModel.setUrl(LocalizationUtils.stripDefaultExtension(_path));
            webRequestContext.setPage(pageModel);
        }
        return pageModel;
    }

    protected abstract LocalizationUtils.TryFindPage<PageModel> _loadPageCallback();

    @Override
    public EntityModel getEntityModel(@NotNull String id, Localization _localization) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        String[] idParts = id.split("-");
        if (idParts.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", id));
        }

        String componentUri = TcmUtils.buildTcmUri(localization.getId(), idParts[0]);
        String templateUri = TcmUtils.buildTemplateTcmUri(localization.getId(), idParts[1]);

        EntityModel entityModel = _getEntityModel(componentUri, templateUri);
        if (entityModel.getXpmMetadata() != null) {
            entityModel.getXpmMetadata().put("IsQueryBased", true);
        }
        return entityModel;
    }

    @NotNull
    protected abstract EntityModel _getEntityModel(String componentUri, String templateUri) throws ContentProviderException;

    @Override
    public <T extends EntityModel> void populateDynamicList(DynamicList<T, SimpleBrokerQuery> dynamicList, Localization localization) throws ContentProviderException {
        if (localization == null) {
            log.info("Localization should not be null to populate dynamic list {}, skipping", dynamicList);
            return;
        }
        SimpleBrokerQuery query = dynamicList.getQuery(localization);
        dynamicList.setQueryResults(_convertEntities(executeMetadataQuery(query), dynamicList.getEntityType(), localization), query.isHasMore());
    }

    protected abstract <T extends EntityModel> List<T> _convertEntities(List<ComponentMetadata> components, Class<T> entityClass, Localization localization) throws ContentProviderException;

    @Override
    public StaticContentItem getStaticContent(final String path, String localizationId, String localizationPath)
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

    private static String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        String parentPath = StringUtils.join(new String[]{
                webApplicationContext.getServletContext().getRealPath("/"), STATIC_FILES_DIR, localizationId
        }, File.separator);

        final File file = new File(parentPath, path);
        log.trace("getStaticContentFile: {}", file);

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            return getStaticContentFile(file, pathInfo, publicationId);
        } catch (IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }

    protected StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
        BinaryMeta binaryMeta;
        WebComponentMetaFactory factory = new WebComponentMetaFactoryImpl(publicationId);
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

        long componentTime = componentMeta.getLastPublicationDate().getTime();
        if (isToBeRefreshed(file, componentTime)) {
            BinaryData binaryData = binaryContentRetriever.getBinary(publicationId, itemId, binaryMeta.getVariantId());

            log.debug("Writing binary content to file: {}", file);
            writeToFile(file, pathInfo, binaryData.getBytes());
        } else {
            log.debug("File does not need to be refreshed: {}", file);
        }

        return new StaticContentFile(file, binaryMeta.getType());
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    private String prependFullUrlIfNeeded(String path) {
        String baseUrl = webRequestContext.getBaseUrl();
        if (path.contains(baseUrl)) {
            return path;
        }
        return UriUtils.encodePath(baseUrl + path, "UTF-8");
    }

    static boolean isToBeRefreshed(File file, long time) throws ContentProviderException {
        if (FileUtils.isFileOlderThan(file, time)) {
            if (!FileUtils.parentFolderExists(file, true)) {
                throw new ContentProviderException("Failed to create parent directory for file: " + file);
            }
            return true;
        }
        return false;
    }

    protected List<String> executeQuery(SimpleBrokerQuery simpleBrokerQuery) {
        Query query = buildQuery(simpleBrokerQuery);
        try {
            return Arrays.asList(query.executeQuery());
        } catch (StorageException e) {
            log.warn("Exception while execution of broker query", e);
            return Collections.emptyList();
        }
    }

    protected Query buildQuery(SimpleBrokerQuery simpleBrokerQuery) {
        Query query = new Query(buildCriteria(simpleBrokerQuery));

        if (!isNullOrEmpty(simpleBrokerQuery.getSort()) &&
                !Objects.equals(simpleBrokerQuery.getSort().toLowerCase(), "none")) {
            query.addSorting(getSortParameter(simpleBrokerQuery));
        }

        int maxResults = simpleBrokerQuery.getResultLimit();
        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }

        int pageSize = simpleBrokerQuery.getPageSize();
        if (pageSize > 0) {
            // We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(simpleBrokerQuery.getStartAt(), pageSize + 1));
        }

        return query;
    }

    /**
     * Executes the given query on a specific version of Tridion and returns a list of metadata.
     *
     * @param simpleBrokerQuery query to execute
     * @return a list of metadata, never returns <code>null</code>
     */
    @Contract("_ -> !null")
    protected List<ComponentMetadata> executeMetadataQuery(SimpleBrokerQuery simpleBrokerQuery) {
        List<String> ids = executeQuery(simpleBrokerQuery);

        final WebComponentMetaFactory cmf = new WebComponentMetaFactoryImpl(simpleBrokerQuery.getPublicationId());
        simpleBrokerQuery.setHasMore(ids.size() > simpleBrokerQuery.getPageSize());

        return ids.stream()
                .filter(id -> cmf.getMeta(id) != null)
                .limit(simpleBrokerQuery.getPageSize())
                .map(id -> convert(cmf.getMeta(id)))
                .collect(Collectors.toList());
    }

    private Criteria buildCriteria(@NotNull SimpleBrokerQuery query) {
        final List<Criteria> children = new ArrayList<>();

        if (query.getSchemaId() > 0) {
            children.add(new ItemSchemaCriteria(query.getSchemaId()));
        }

        if (query.getPublicationId() > 0) {
            children.add(new PublicationCriteria(query.getPublicationId()));
        }

        if (query.getPath() != null) {
            children.add(new PageURLCriteria(query.getPath()));
        }

        if (query.getKeywordFilters() != null) {
            query.getKeywordFilters().entries().forEach(entry -> {
                children.add(new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true));
            });
        }

        return new AndCriteria(children);
    }

    private SortParameter getSortParameter(SimpleBrokerQuery simpleBrokerQuery) {
        SortDirection dir = simpleBrokerQuery.getSort().toLowerCase().endsWith("asc") ?
                SortDirection.ASCENDING : SortDirection.DESCENDING;
        return new SortParameter(getSortColumn(simpleBrokerQuery), dir);
    }

    private ComponentMetadata convert(ComponentMeta compMeta) {
        Map<String, ComponentMetadata.MetaEntry> custom = new HashMap<>(compMeta.getCustomMeta().getNameValues().size());
        for (Map.Entry<String, NameValuePair> entry : compMeta.getCustomMeta().getNameValues().entrySet()) {
            ComponentMetadata.MetaType metaType;
            switch (entry.getValue().getMetadataType()) {
                case DATE:
                    metaType = ComponentMetadata.MetaType.DATE;
                    break;
                case FLOAT:
                    metaType = ComponentMetadata.MetaType.FLOAT;
                    break;
                default:
                    metaType = ComponentMetadata.MetaType.STRING;
            }
            custom.put(entry.getKey(), ComponentMetadata.MetaEntry.builder()
                    .metaType(metaType)
                    .value(entry.getValue().getFirstValue())
                    .build());
        }

        return ComponentMetadata.builder()
                .id(String.valueOf(compMeta.getId()))
                .componentUrl(linkResolver.resolveLink("tcm:" + compMeta.getPublicationId() + '-' + compMeta.getId(), null))
                .publicationId(String.valueOf(compMeta.getPublicationId()))
                .owningPublicationId(String.valueOf(compMeta.getOwningPublicationId()))
                .schemaId(String.valueOf(compMeta.getSchemaId()))
                .title(compMeta.getTitle())
                .modificationDate(compMeta.getModificationDate())
                .initialPublicationDate(compMeta.getInitialPublicationDate())
                .lastPublicationDate(compMeta.getLastPublicationDate())
                .creationDate(compMeta.getCreationDate())
                .author(compMeta.getAuthor())
                .multimedia(compMeta.isMultimedia())
                .custom(custom)
                .build();
    }

    private BrokerSortColumn getSortColumn(SimpleBrokerQuery simpleBrokerQuery) {
        final String sortTrim = simpleBrokerQuery.getSort().trim();
        final int pos = sortTrim.indexOf(' ');
        final String sortCol = pos > 0 ? sortTrim.substring(0, pos) : sortTrim;
        switch (sortCol.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;

            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;

            default:
                // Default is to assume that its a custom metadata date field
                return new CustomMetaKeyColumn(simpleBrokerQuery.getSort(), MetadataType.DATE);
        }
    }

    protected static final class StaticContentFile {

        private final File file;

        private final String contentType;

        StaticContentFile(File file, String contentType) {
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
