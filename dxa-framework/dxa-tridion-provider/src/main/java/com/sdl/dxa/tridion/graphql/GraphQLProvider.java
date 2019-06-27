package com.sdl.dxa.tridion.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentIncludeMode;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.web.pca.client.contentmodel.enums.DataModelType;
import com.sdl.web.pca.client.contentmodel.enums.DcpType;
import com.sdl.web.pca.client.contentmodel.enums.ModelServiceLinkRendering;
import com.sdl.web.pca.client.contentmodel.enums.PageInclusion;
import com.sdl.web.pca.client.contentmodel.enums.TcdlLinkRendering;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sdl.dxa.common.util.PathUtils.normalizePathToDefaults;
import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Docs;
import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites;

/**
 * Common class for interaction with GraphQL backend.
 */
@Slf4j
@Component
@Profile("!cil.providers.active")
public class GraphQLProvider {

    private ObjectMapper objectMapper;
    private ApiClientProvider pcaClientProvider;

    @Autowired
    public GraphQLProvider(ApiClientProvider pcaClientProvider, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.pcaClientProvider = pcaClientProvider;
    }

    private ApiClient getPcaClient() {
        ApiClient pcaClient = this.pcaClientProvider.getClient();
        pcaClient.setDefaultModelType(DataModelType.R2);
        pcaClient.setDefaultContentType(ContentType.MODEL);
        pcaClient.setModelSericeLinkRenderingType(ModelServiceLinkRendering.RELATIVE);
        pcaClient.setTcdlLinkRenderingType(TcdlLinkRendering.RELATIVE);

        return pcaClient;
    }

    // DXA supports "extensionless URLs" and "index pages".
    // For example: the index Page at root level (aka the Home Page) has a CM URL path of /index.html
    // It can be addressed in the DXA web application in several ways:
    //      1. /index.html – exactly the same as the CM URL
    //      2. /index – the file extension doesn't have to be specified explicitly {"extensionless URLs")
    //      3. / - the file name of an index page doesn't have to be specified explicitly either.
    // Note that the third option is the most clean one and considered the "canonical URL" in DXA; links to an index Page will be generated like that.
    // The problem with these "URL compression" features is that if a URL does not end with a slash (nor an extension), you don't
    // know upfront if the URL addresses a regular Page or an index Page (within a nested SG).
    // To determine this, DXA first tries the regular Page and if it doesn't exist, it appends /index.html and tries again.
    // TODO: The above should be handled by GraphQL (See CRQ-11703)
    public <T> T loadPage(Class<T> type, PageRequestDto pageRequest, ContentType contentType) throws ContentProviderException {
        try {
            JsonNode pageNode = getPcaClient().getPageModelData(
                    getNamespace(pageRequest.getUriType()),
                    pageRequest.getPublicationId(),
                    normalizePathToDefaults(pageRequest.getPath()),
                    contentType,
                    DataModelType.valueOf(pageRequest.getDataModelType().toString()),
                    PageInclusion.valueOf(pageRequest.getIncludePages().toString()),
                    ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                    null
            );
            T result = mapToType(type, pageNode);
            if (log.isTraceEnabled()) {
                log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
            }
            return result;
        } catch (IOException e) {
            String pathToDefaults = normalizePathToDefaults(pageRequest.getPath(), true);
            log.info("Page not found by " + pageRequest + ", trying to find it by path " + pathToDefaults);
            JsonNode node = null;
            try {
                node = getPcaClient().getPageModelData(
                        getNamespace(pageRequest.getUriType()),
                        pageRequest.getPublicationId(),
                        pathToDefaults,
                        contentType,
                        DataModelType.valueOf(pageRequest.getDataModelType().toString()),
                        PageInclusion.valueOf(pageRequest.getIncludePages().toString()),
                        ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                        null);
                T result = mapToType(type, node);
                if (log.isTraceEnabled()) {
                    log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
                }
                return result;
            } catch (IOException ex) {
                if (log.isTraceEnabled()) {
                    log.trace("Response for request " + pageRequest + " is " + node, e);
                }
                throw new PageNotFoundException("Unable to load page, by request " + pageRequest, ex);
            }
        }
    }

    public <T> T loadPage(Class<T> type, String namespace, int publicationId, int pageId, ContentType contentType, DataModelType modelType, PageInclusion pageInclusion, ContextData contextData) throws ContentProviderException {
        JsonNode pageNode = getPcaClient().getPageModelData(
                getNamespace(namespace),
                publicationId,
                pageId,
                contentType,
                modelType,
                pageInclusion,
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                contextData
        );
        T result = null;
        try {
            result = mapToType(type, pageNode);
        } catch (IOException ex) {
            throw new PageNotFoundException(String.format("Page not found: [%d] %d/index.html", publicationId, pageId), ex);
        }
        return result;
    }

    public EntityModelData getEntityModelData(EntityRequestDto entityRequest) throws ContentProviderException {
        JsonNode node = null;
        try {
            node = getPcaClient().getEntityModelData(Sites,
                    entityRequest.getPublicationId(),
                    entityRequest.getComponentId(),
                    entityRequest.getTemplateId(),
                    ContentType.valueOf(entityRequest.getContentType().toString()),
                    DataModelType.valueOf(entityRequest.getDataModelType().toString()),
                    DcpType.valueOf(entityRequest.getDcpType().toString()),
                    ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                    null);

            EntityModelData modelData = mapToType(EntityModelData.class, node);
            if (log.isTraceEnabled()) {
                log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
            }
            return modelData;
        } catch (IOException e) {
            if (log.isTraceEnabled()) {
                log.trace("Response for request " + entityRequest + " is " + node, e);
            }
            throw new ContentProviderException("Entity not found ny request " + entityRequest, e);
        }
    }

    private <T> T mapToType(Class<T> type, JsonNode result) throws JsonProcessingException {
        if (type.equals(String.class)) {
            return (T) result.toString();
        }
        return objectMapper.treeToValue(result, type);
    }

    /**
     * Convert from
     * com.sdl.webapp.common.impl.model.ContentNamespace
     * to
     * com.sdl.web.pca.client.contentmodel.enums.ContentNamespace
     * @param contentNamespace
     */
    private ContentNamespace getNamespace(String contentNamespace) {
        switch (contentNamespace) {
            case "ish":
                return Docs;
            case "tcm":
                return Sites;
        };
        return null;
    }


}
