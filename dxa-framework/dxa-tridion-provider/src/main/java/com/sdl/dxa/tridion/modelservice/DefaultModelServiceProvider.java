package com.sdl.dxa.tridion.modelservice;

import com.google.common.base.Strings;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.dxa.tridion.modelservice.exceptions.ModelServiceInternalServerErrorException;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service(value = "DefaultModelService")
@Profile("cil.providers.active")
public class DefaultModelServiceProvider implements ModelServiceProvider {

    private final ModelServiceConfiguration configuration;

    private final ModelServiceClient modelServiceClient;

    @Autowired
    public DefaultModelServiceProvider(ModelServiceConfiguration configuration, ModelServiceClient modelServiceClient) {
        this.configuration = configuration;
        this.modelServiceClient = modelServiceClient;
    }

    @NotNull
    @Override
    public PageModelData loadPageModel(PageRequestDto pageRequest) throws ContentProviderException {
        return _loadPage(configuration.getPageModelUrl(), PageModelData.class, pageRequest);
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
        String serviceUrl = UriComponentsBuilder.fromUriString(configuration.getPageModelUrl()).queryParam("raw", "true").build().toUriString();
        return _loadPage(serviceUrl, String.class, pageRequest);
    }

    private <T> T _loadPage(String serviceUrl, Class<T> type, PageRequestDto pageRequest) throws ContentProviderException {
        try {
            T page = modelServiceClient.getForType(serviceUrl, type,
                    pageRequest.getUriType(),
                    pageRequest.getPublicationId(),
                    removeLeadingAndEndingSlash(pageRequest.getPath()),
                    pageRequest.getIncludePages());
            log.trace("Loaded '{}' for pageRequest '{}'", page, pageRequest);
            return page;
        } catch (ModelServiceInternalServerErrorException e) {
            throw new ContentProviderException("Cannot load page from model service", e);
        } catch (ItemNotFoundInModelServiceException e) {
            throw new PageNotFoundException("Cannot load page '" + pageRequest + "'", e);
        }
    }

    private String removeLeadingAndEndingSlash(String path) {
        if (Strings.isNullOrEmpty(path)) return "";
        return path.replaceAll("^/+([^/].*)", "$1").replaceAll("(.*[^/])/+$", "$1");
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
        try {
            EntityModelData modelData = modelServiceClient.getForType(configuration.getEntityModelUrl(), EntityModelData.class,
                    entityRequest.getUriType(),
                    entityRequest.getPublicationId(),
                    entityRequest.getComponentId(),
                    entityRequest.getTemplateId());
            log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
            return modelData;
        } catch (ItemNotFoundInModelServiceException e) {
            throw new DxaItemNotFoundException("Entity " + entityRequest + " not found in the Model Service", e);
        }
    }
}