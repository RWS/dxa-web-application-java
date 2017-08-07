package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.modelservice.DefaultModelService;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.mapping.AbstractDefaultContentProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.dxa.common.dto.PageRequestDto.PageInclusion.INCLUDE;

@Service("r2ContentProvider")
@Slf4j
public class DefaultContentProvider extends AbstractDefaultContentProvider {

    @Autowired
    private ModelBuilderPipeline builderPipeline;

    @Autowired
    private DefaultModelService modelService;

    public DefaultContentProvider(WebRequestContext webRequestContext,
                                  StaticContentResolver staticContentResolver,
                                  LinkResolver linkResolver) {
        super(webRequestContext, linkResolver, staticContentResolver);
    }

    @Override
    protected PageModel _loadPage(String path, Localization localization) throws ContentProviderException {
        PageModelData modelData = modelService.loadPageModel(PageRequestDto.builder()
                .path(path)
                .includePages(INCLUDE)
                .build());
        return builderPipeline.createPageModel(modelData);
    }

    @NotNull
    @Override
    protected EntityModel _getEntityModel(String componentId) throws ContentProviderException {
        EntityModelData modelData = modelService.loadEntity(componentId);
        try {
            return builderPipeline.createEntityModel(modelData);
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot build the entity model for componentId" + componentId, e);
        }
    }

    @Override
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

}
