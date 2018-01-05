package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.caching.wrapper.CopyingCache;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.modelservice.DefaultModelService;
import com.sdl.web.api.broker.querying.sorting.BrokerSortColumn;
import com.sdl.web.api.broker.querying.sorting.CustomMetaKeyColumn;
import com.sdl.web.api.broker.querying.sorting.SortParameter;
import com.sdl.web.api.meta.WebComponentMetaFactory;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.FileUtils;
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
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.NameValuePair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.dxa.common.dto.PageRequestDto.PageInclusion.INCLUDE;

/**
 * Content Provider default implementation. Look at {@link ContentProvider} documentation for details.
 *
 * @dxa.publicApi
 */
@Service
@Slf4j
public class DefaultContentProvider implements ContentProvider {

    private final ModelBuilderPipeline builderPipeline;

    private final DefaultModelService modelService;

    private final WebRequestContext webRequestContext;

    private final LinkResolver linkResolver;

    private final StaticContentResolver staticContentResolver;

    private List<ConditionalEntityEvaluator> entityEvaluators = Collections.emptyList();

    @Autowired
    public DefaultContentProvider(WebRequestContext webRequestContext,
                                  StaticContentResolver staticContentResolver,
                                  LinkResolver linkResolver,
                                  ModelBuilderPipeline builderPipeline,
                                  DefaultModelService modelService) {
        this.webRequestContext = webRequestContext;
        this.linkResolver = linkResolver;
        this.staticContentResolver = staticContentResolver;
        this.builderPipeline = builderPipeline;
        this.modelService = modelService;
    }

    protected PageModel _loadPage(String path, Localization localization) throws ContentProviderException {
        PageModelData modelData = modelService.loadPageModel(
                PageRequestDto.builder(localization.getId(), path)
                        .includePages(INCLUDE)
                        .build());
        return builderPipeline.createPageModel(modelData);
    }

    @NotNull
    protected EntityModel _getEntityModel(String componentId) throws ContentProviderException {
        EntityModelData modelData = modelService.loadEntity(webRequestContext.getLocalization().getId(), componentId);
        try {
            return builderPipeline.createEntityModel(modelData);
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot build the entity model for componentId" + componentId, e);
        }
    }

    protected <T extends EntityModel> List<T> _convertEntities(List<ComponentMetadata> components, Class<T> entityClass, Localization localization) throws DxaException {
        List<T> entities = new ArrayList<>();
        for (ComponentMetadata metadata : components) {
            entities.add(builderPipeline.createEntityModel(EntityModelData.builder()
                    .id(metadata.getId())
                    .metadata(getContentModelData(metadata))
                    .schemaId(metadata.getSchemaId())
                    .build(), entityClass));
        }
        return entities;
    }

    @NotNull
    private ContentModelData getContentModelData(ComponentMetadata metadata) {
        ContentModelData outer = new ContentModelData();
        ContentModelData standardMetaContents = new ContentModelData();
        String dateTimeStringFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

        metadata.getCustom().entrySet()
                .forEach(entry -> {
                    ComponentMetadata.MetaEntry data = entry.getValue();
                    if (data != null && data.getValue() != null) {
                        Object value = data.getValue();
                        String field;
                        if (data.getMetaType() == ComponentMetadata.MetaType.DATE) {
                            field = new DateTime(value).toString(dateTimeStringFormat);
                        } else {
                            field = value.toString();
                        }

                        standardMetaContents.put(entry.getKey(), field);
                    }
                });

        if (!standardMetaContents.containsKey("dateCreated")) {
            standardMetaContents.put("dateCreated", new DateTime(metadata.getLastPublicationDate()).toString(dateTimeStringFormat));
        }

        if (!standardMetaContents.containsKey("name")) {
            standardMetaContents.put("name", metadata.getTitle());
        }

        outer.put("standardMeta", standardMetaContents);
        return outer;
    }

    @Autowired(required = false)
    public void setEntityEvaluators(List<ConditionalEntityEvaluator> entityEvaluators) {
        this.entityEvaluators = entityEvaluators;
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public PageModel getPageModel(String path, Localization localization) throws ContentProviderException {
        PageModel pageModel = _loadPage(path, localization);

        pageModel.filterConditionalEntities(entityEvaluators);
        //todo dxa2 refactor this, remove usage of deprecated method
        webRequestContext.setPage(pageModel);

        return pageModel;
    }

    /**
     * {@inheritDoc}
     * If you need copying cache for dynamic logic, use {@link CopyingCache}.
     *
     * @dxa.publicApi
     */
    @Override
    public EntityModel getEntityModel(@NotNull String id, Localization _localization) throws ContentProviderException {
        Assert.notNull(id);
        EntityModel entityModel = _getEntityModel(id);
        if (entityModel.getXpmMetadata() != null) {
            entityModel.getXpmMetadata().put("IsQueryBased", true);
        }
        return entityModel;
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public <T extends EntityModel> void populateDynamicList(DynamicList<T, SimpleBrokerQuery> dynamicList, Localization localization) throws ContentProviderException {
        if (localization == null) {
            log.info("Localization should not be null to populate dynamic list {}, skipping", dynamicList);
            return;
        }
        SimpleBrokerQuery query = dynamicList.getQuery(localization);
        try {
            dynamicList.setQueryResults(_convertEntities(executeMetadataQuery(query), dynamicList.getEntityType(), localization), query.isHasMore());
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot populate a dynamic list " + dynamicList.getId() + " localization " + localization.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public StaticContentItem getStaticContent(final String path, String localizationId, String localizationPath)
            throws ContentProviderException {

        return staticContentResolver.getStaticContent(
                StaticContentRequestDto.builder(path, localizationId)
                        .localizationPath(localizationPath)
                        .baseUrl(webRequestContext.getBaseUrl())
                        .noMediaCache(!FileUtils.isEssentialConfiguration(path, localizationPath) && webRequestContext.isPreview())
                        .build());
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
}
