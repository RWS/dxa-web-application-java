package com.sdl.dxa.tridion.modelservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.EntityModelService;
import com.sdl.dxa.modelservice.service.PageModelService;
import com.sdl.dxa.tridion.modelservice.exceptions.ModelServiceInternalServerErrorException;
import com.sdl.web.pca.client.DefaultGraphQLClient;
import com.sdl.web.pca.client.DefaultPublicContentApi;
import com.sdl.web.pca.client.GraphQLClient;
import com.sdl.web.pca.client.PublicContentApi;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.web.pca.client.contentmodel.enums.DataModelType;
import com.sdl.web.pca.client.contentmodel.enums.DcpType;
import com.sdl.web.pca.client.contentmodel.enums.PageInclusion;
import com.sdl.web.pca.client.exception.PublicContentApiException;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Service(value = "PCAModelService")
@Primary
public class PCAModelService implements PageModelService, EntityModelService {

    private final ModelServiceConfiguration configuration;

    private final PublicContentApi pcaClient;

    @Autowired
    public PCAModelService(ModelServiceConfiguration configuration) {
        this.configuration = configuration;
        GraphQLClient graphQLClient = new DefaultGraphQLClient("http://localhost:8081/udp/content/",null);
        this.pcaClient = new DefaultPublicContentApi(graphQLClient);
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
    public String loadPageContent(PageRequestDto pageRequest) throws ContentProviderException {
        String serviceUrl = UriComponentsBuilder.fromUriString(configuration.getPageModelUrl()).queryParam("raw", "true").build().toUriString();
        return _loadPage(serviceUrl, String.class, pageRequest);
    }

    private String getPathUrl(String path)
    {
        if(path.equals("/"))
            path=path+"index.html";
        return path;
    }

    private <T> T _loadPage(String serviceUrl, Class<T> type, PageRequestDto pageRequest) throws ContentProviderException {
        try {
            JsonNode pageNode = pcaClient.getPageModelData(ContentNamespace.Sites,  pageRequest.getPublicationId(), getPathUrl(pageRequest.getPath()), ContentType.MODEL, DataModelType.R2, PageInclusion.INCLUDE, false, new ContextData());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            T  result = (T)mapper.readValue(pageNode.toString(),PageModelData.class);
            log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
            return result;
        } catch (PublicContentApiException | ModelServiceInternalServerErrorException | IOException e) {
            throw new ContentProviderException("Cannot load page from model service for " + pageRequest, e);
        }
    }

    /**
     * Shortcut method for {@link #loadEntity(EntityRequestDto)}.
     *
     * @param entityId entity ID in a format of {@code componentId-templateId}
     */
    @NotNull
    public EntityModelData loadEntity(String publicationId, @NotNull String entityId) throws ContentProviderException {
        return loadEntity(EntityRequestDto.builder(publicationId, entityId).entityId(entityId).build());
    }

    @NotNull
    @Override
    public EntityModelData loadEntity(EntityRequestDto entityRequest) throws ContentProviderException {
        try {
            JsonNode node = pcaClient.getEntityModelData(ContentNamespace.Sites,8,1458,9195,ContentType.MODEL,DataModelType.R2,DcpType.DEFAULT, false, new ContextData());
            ObjectMapper MAPPER = new ObjectMapper();
            EntityModelData modelData = MAPPER.readValue(node.toString(),EntityModelData.class);

            log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
            return modelData;
        } catch (PublicContentApiException | IOException e) {
            throw new DxaItemNotFoundException("Entity " + entityRequest + " not found in the Model Service", e);
        }
    }
}
