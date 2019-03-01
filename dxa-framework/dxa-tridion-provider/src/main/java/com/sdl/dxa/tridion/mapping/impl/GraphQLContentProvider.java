package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.caching.wrapper.CopyingCache;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.broker.GraphQLQueryProvider;
import com.sdl.dxa.tridion.broker.QueryProvider;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.dxa.tridion.graphql.GraphQLProvider;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.web.pca.client.contentmodel.generated.Component;
import com.sdl.web.pca.client.contentmodel.generated.CustomMetaEdge;
import com.sdl.web.pca.client.contentmodel.generated.Item;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.exceptions.DxaRuntimeException;
import com.sdl.webapp.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Content Provider GraphQL implementation. Look at {@link ContentProvider} documentation for details.
 *
 * @dxa.publicApi
 */
@Service(value = "GraphQLContentProvider")
@Profile("!cil.providers.active")
@Primary
@Slf4j
public class GraphQLContentProvider implements ContentProvider {

    private ModelBuilderPipeline builderPipeline;

    private WebRequestContext webRequestContext;

    private StaticContentResolver staticContentResolver;

    private List<ConditionalEntityEvaluator> entityEvaluators = Collections.emptyList();

    private GraphQLBinaryContentProvider graphQLBinaryContentProvider;
    private GraphQLProvider graphQLProvider;
    private ApiClient pcaClient;

    @Autowired
    public GraphQLContentProvider(WebApplicationContext webApplicationContext,
                                  WebRequestContext webRequestContext,
                                  StaticContentResolver staticContentResolver,
                                  ModelBuilderPipeline builderPipeline, GraphQLProvider graphQLProvider,
                                  ApiClientProvider pcaClientProvider) {
        this.pcaClient = pcaClientProvider.getClient();
        this.graphQLBinaryContentProvider = new GraphQLBinaryContentProvider(pcaClientProvider.getClient(), webApplicationContext);
        this.webRequestContext = webRequestContext;
        this.staticContentResolver = staticContentResolver;
        this.builderPipeline = builderPipeline;
        this.graphQLProvider = graphQLProvider;
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
        PageModel pageModel = loadPage(path, localization);

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
    public EntityModel getEntityModel(@NotNull String id, Localization localization) throws ContentProviderException {
        Assert.notNull(id);
        EntityModel entityModel = getEntityModel(id);
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
        SimpleBrokerQuery simpleBrokerQuery = dynamicList.getQuery(localization);

        // get our cursor indexer for this list
        CursorIndexer cursors = CursorIndexer.getCursorIndexer(dynamicList.getId());

        // given our start index into the paged list we need to translate that to a cursor
        int start = simpleBrokerQuery.getStartAt();

        simpleBrokerQuery.setCursor(cursors.get(start));
        // the cursor retrieved may of came from a different start index so we update start
        simpleBrokerQuery.setStartAt(start);
        dynamicList.setStart(cursors.getStart());

        QueryProvider brokerQuery = new GraphQLQueryProvider(pcaClient);

        List<Item> components = brokerQuery.executeQueryItems(simpleBrokerQuery);
        log.debug("Broker query returned {} results. hasMore={}", components.size(), brokerQuery.hasMore());

        if (components.size() > 0) {
            Class<T> resultType = dynamicList.getEntityType();
            dynamicList.setQueryResults(
                    components.stream().map(c -> {
                        try {
                            return builderPipeline.createEntityModel(createEntityModelData((Component) c), resultType);
                        } catch (DxaException e) {
                            throw new DxaRuntimeException(e);
                        }
                    }).collect(Collectors.toList()),
                    brokerQuery.hasMore()
            );
        }

        if (brokerQuery.hasMore()) {
            cursors.cursors.put(simpleBrokerQuery.getStartAt() + simpleBrokerQuery.getPageSize(), brokerQuery.getCursor());
        }
    }

    private EntityModelData createEntityModelData(Component component) {
        ContentModelData standardMeta = new ContentModelData();

        for (CustomMetaEdge meta : component.getCustomMetas().getEdges()) {
            standardMeta.put(meta.getNode().getKey(), meta.getNode().getValue());
        }
        // The semantic mapping requires that some metadata fields exist. This may not be the case so we map some component meta properties onto them
        // if they don't exist.
        if (!standardMeta.containsKey("dateCreated")) {
            standardMeta.put("dateCreated", component.getLastPublishDate());
        }

        if (!standardMeta.containsKey("name")) {
            standardMeta.put("name", component.getTitle());
        }

        EntityModelData result = new EntityModelData();
        result.setId("" + component.getItemId());
        result.setSchemaId("" + component.getSchemaId());
        ContentModelData meta = new ContentModelData();
        meta.put("standardMeta", standardMeta);
        result.setMetadata(meta);
        return result;
    }

    private static class CursorIndexer {
        public static final String SESSION_KEY = "dxa_indexer";
        private Map<Integer, String> cursors = new HashMap<>();
        private int startIndex;

        public CursorIndexer() {
        }

        public static CursorIndexer getCursorIndexer(String id) {
            HttpSession session = (HttpSession) RequestContextHolder.getRequestAttributes().getSessionMutex();
            Map<String, CursorIndexer> indexer = (Map<String, CursorIndexer>) session.getAttribute(SESSION_KEY);
            if (indexer == null) {
                indexer = new HashMap<>();
            }
            if (!indexer.containsKey(id)) {
                indexer.put(id, new CursorIndexer());
            }
            session.setAttribute(SESSION_KEY, indexer);
            return indexer.get(id);
        }

        public String get(int index) {
             if (index == 0) {
                startIndex = 0;
                return null;
            }
            if (cursors.size() == 0) {
                startIndex = 0;
                return null;
            }
            if (cursors.containsKey(index)) {
                startIndex = index;
                return cursors.get(index);
            }
            int min = 0;
            for (Integer x : cursors.keySet()) {
                if (x >= min && x < index) min = x;
            }
            startIndex = min;
            return startIndex == 0 ? null : cursors.get(startIndex);
        }

        public void set(int index, String value) {
            cursors.put(index, value);
        }

        public int getStart() {
            return startIndex;
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

    protected PageModel loadPage(String path, Localization localization) throws ContentProviderException {
        PageRequestDto pageRequest = PageRequestDto.builder(localization.getId(), path)
                .includePages(PageRequestDto.PageInclusion.INCLUDE)
                .build();
        PageModelData pageModelData = graphQLProvider.loadPage(PageModelData.class, pageRequest, ContentType.MODEL);

        return builderPipeline.createPageModel(pageModelData);
    }

    @NotNull
    protected EntityModel getEntityModel(String componentId) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        EntityRequestDto entityRequest = EntityRequestDto.builder(localization.getId(), componentId).build();

        EntityModelData entityModelData = graphQLProvider.getEntityModelData(entityRequest);

        try {
            return builderPipeline.createEntityModel(entityModelData);
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot build the entity model for componentId" + componentId, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    @Override
    public StaticContentItem getStaticContent(int binaryId, String localizationId, String localizationPath) throws ContentProviderException {
        return graphQLBinaryContentProvider.getStaticContent(this, binaryId, localizationId, localizationPath);
    }

}