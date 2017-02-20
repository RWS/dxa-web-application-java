package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
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
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.sdl.webapp.common.util.LocalizationUtils.TryFindPage;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.mapping.AbstractDefaultContentProvider;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.content.PageContentFactory;
import com.tridion.data.CharacterData;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.dcp.ComponentPresentationFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
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
                switch (result.length) {
                    case 1:
                        CharacterData pageContent = new PageContentFactory().getPageContent(publicationId, TcmUtils.getItemId(result[0]));
                        PageModelData modelData = objectMapper.readValue(pageContent.getString(), PageModelData.class);
                        return builderPipeline.createPageModel(modelData, PageInclusion.INCLUDE);
                    case 0:
                        log.debug("Page not found, publication id {}, path {}", publicationId, path);
                        return null;
                    default:
                        throw new ContentProviderException("Got " + result.length + " pages for path " + path);
                }
            } catch (StorageException | IOException e) {
                log.warn("Issue while getting page content, publication id {}, path {}", publicationId, path, e);
                throw new ContentProviderException("Issue while deserializing page content for publication id" + publicationId + ", path " + path, e);
            }
        };
    }

    @NotNull
    @Override
    protected EntityModel _getEntityModel(String componentUri, String templateUri) throws ContentProviderException {
        try {
            ComponentPresentationFactory componentPresentationFactory = new ComponentPresentationFactory(componentUri);
            ComponentPresentation componentPresentation = componentPresentationFactory.getComponentPresentation(componentUri, templateUri);

            if (componentPresentation == null) {
                String message = "Cannot find a CP for componentUri" + componentUri + ", templateUri" + templateUri;
                log.warn(message);
                throw new DxaItemNotFoundException(message);
            }
            EntityModelData entityModelData = objectMapper.readValue(componentPresentation.getContent(), EntityModelData.class);
            return builderPipeline.createEntityModel(entityModelData);
        } catch (IOException e) {
            log.warn("Issue while deserializing entity content, componentUri {}, templateUri {}", componentUri, templateUri, e);
            throw new ContentProviderException("Issue while deserializing entity content for componentUri" + componentUri + ", templateUri", e);
        }
    }

    @Override
    protected <T extends EntityModel> List<T> _convertEntities(List<ComponentMetadata> components, Class<T> entityClass, Localization localization) throws ContentProviderException {
        return components.stream()
                .map(metadata -> EntityModelData.builder()
                        .id(metadata.getId())
                        .metadata(getContentModelData(metadata))
                        .schemaId(metadata.getSchemaId())
                        .build())
                .map(entityModelData -> builderPipeline.createEntityModel(entityModelData, entityClass))
                .collect(Collectors.toList());
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

}
