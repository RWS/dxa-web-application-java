package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.LocalizationUtils.TryFindPage;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.mapping.AbstractDefaultContentProvider;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.content.PageContentFactory;
import com.tridion.data.CharacterData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@R2
@Service("r2ContentProvider")
@Slf4j
public class DefaultContentProvider extends AbstractDefaultContentProvider {

    @Autowired
    private ModelBuilderPipeline builderPipeline;

    @Autowired
    @Qualifier("dxaR2ObjectMapper")
    private ObjectMapper objectMapper;

    public DefaultContentProvider(WebRequestContext webRequestContext,
                                  LinkResolver linkResolver,
                                  WebApplicationContext webApplicationContext,
                                  DynamicMetaRetriever dynamicMetaRetriever,
                                  BinaryContentRetriever binaryContentRetriever) {
        super(webRequestContext, linkResolver, webApplicationContext, dynamicMetaRetriever, binaryContentRetriever);
    }

    @Override
    protected TryFindPage<PageModel> _loadPageCallback() {
        return (path, publicationId) -> {
            try {
                SimpleBrokerQuery simpleBrokerQuery = SimpleBrokerQuery.builder().path(path).publicationId(publicationId).build();
                simpleBrokerQuery.setResultLimit(1);
                Query query = buildQuery(simpleBrokerQuery);
                query.addSorting(new SortParameter(SortParameter.ITEMS_URL, SortParameter.DESCENDING));
                String[] result = query.executeQuery();

                log.debug("Requesting publication {}, path {}, result is {}", publicationId, path, result);
                if (result.length > 0) {
                    CharacterData pageContent = new PageContentFactory().getPageContent(publicationId, TcmUtils.getItemId(result[0]));
                    PageModelData modelData = objectMapper.readValue(pageContent.getString(), PageModelData.class);
                    return builderPipeline.createPageModel(modelData, PageInclusion.EXCLUDE /*todo*/);
                } else {
                    log.debug("Page not found, publication id {}, path {}", publicationId, path);
                    return null;
                }
            } catch (StorageException | IOException e) {
                log.warn("Issue while getting page content, publication id {}, path {}", publicationId, path, e);
                return null;
            }
        };
    }

    @Override
    protected EntityModel _getEntityModel(String tcmUri) throws ContentProviderException, DxaException {



        return null;
    }

    @Override
    protected <T extends EntityModel> List<T> _convertEntities(List<ComponentMetadata> components, Class<T> entityClass, Localization localization) throws ContentProviderException {
        return components.stream()
                //todo finish
                .map(metadata -> new EntityModelData(metadata.getId(), null, null, null, null))
                .map(entityModelData -> builderPipeline.createEntityModel(entityModelData, entityClass))
                .collect(Collectors.toList());
    }

}
