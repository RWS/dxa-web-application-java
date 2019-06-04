package com.sdl.dxa.tridion.modelservice;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.dxa.tridion.graphql.GraphQLProvider;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "GraphQLModelServiceProvider")
@Profile("!cil.providers.active")
public class GraphQLModelServiceProvider implements ModelServiceProvider {

    private GraphQLProvider graphQLProvider;

    @Autowired
    public GraphQLModelServiceProvider(GraphQLProvider graphQLProvider) {
        this.graphQLProvider = graphQLProvider;
    }

    @NotNull
    @Override
    public PageModelData loadPageModel(PageRequestDto pageRequest) throws ContentProviderException {
        return graphQLProvider.loadPage(PageModelData.class, pageRequest, ContentType.MODEL);
    }

    /**
     * Loads a page from CD without any processing as it's stored in a database.
     *
     * @param pageRequest page request data
     * @return a page model data, never null
     * @throws PageNotFoundException    if the page doesn't exist
     * @throws ContentProviderException if couldn't load or parse the page content
     */
    @NotNull
    @Override
    public String loadPageContent(PageRequestDto pageRequest) throws ContentProviderException {
        return graphQLProvider.loadPage(String.class, pageRequest, ContentType.RAW);
    }

    /**
     * Shortcut method for {@link #loadEntity(EntityRequestDto)}.
     *
     * @param entityId entity ID in a format of {@code componentId-templateId}
     */
    @NotNull
    @Override
    public EntityModelData loadEntity(String publicationId, @NotNull String entityId) throws ContentProviderException {
        return loadEntity(EntityRequestDto.builder(publicationId, entityId).entityId(entityId).build());
    }

    @NotNull
    @Override
    public EntityModelData loadEntity(EntityRequestDto entityRequest) throws ContentProviderException {
        return graphQLProvider.getEntityModelData(entityRequest);

    }
}
