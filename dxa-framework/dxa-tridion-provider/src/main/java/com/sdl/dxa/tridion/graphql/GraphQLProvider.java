package com.sdl.dxa.tridion.graphql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.dxa.api.datamodel.Constants;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.*;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sdl.dxa.common.util.PathUtils.normalizePathToDefaults;
import static javax.cache.Caching.getDefaultClassLoader;
import static org.springframework.util.ClassUtils.forName;

/**
 * Common class for interaction with GraphQL backend.
 */
@Slf4j
@Component
public class GraphQLProvider {
    private ApiClient pcaClient;

    private ObjectMapper objectMapper;

    @Autowired
    public GraphQLProvider(ApiClientProvider pcaClientProvider) {
        this.objectMapper = getObjectMapper();

        this.pcaClient = pcaClientProvider.getClient();
        pcaClient.setDefaultModelType(DataModelType.R2);
        pcaClient.setDefaultContentType(ContentType.MODEL);
        pcaClient.setModelSericeLinkRenderingType(ModelServiceLinkRendering.RELATIVE);
        pcaClient.setTcdlLinkRenderingType(TcdlLinkRendering.RELATIVE);
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
    public <T> T _loadPage(Class<T> type, PageRequestDto pageRequest, ContentType contentType) throws ContentProviderException {
        try {
            JsonNode pageNode = pcaClient.getPageModelData(
                    ContentNamespace.Sites,
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
                node = pcaClient.getPageModelData(ContentNamespace.Sites,
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

    public EntityModelData getEntityModelData(EntityRequestDto entityRequest) throws ContentProviderException {
        JsonNode node = null;
        try {
            node = pcaClient.getEntityModelData(ContentNamespace.Sites,
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

//    public EntityModelData getEntityModelData(String componentId, Localization localization ) throws ContentProviderException {
//        String[] ids = componentId.split("-");
//        if (ids.length != 2) return null;
//
//        int i1 = Integer.valueOf(ids[0]);
//        int i2 = Integer.valueOf(ids[1]);
//        EntityRequestDto.builder(localization.getId(), )
//        JsonNode stuff = pcaClient.getEntityModelData(ContentNamespace.Sites, Integer.valueOf(localization.getId()), i1, i2,
//                ContentType.MODEL, DataModelType.R2, DcpType.DEFAULT,
//                ContentIncludeMode.INCLUDE_DATA_AND_RENDER, null);
//
//        EntityModelData entityModelData = null;
//        try {
//            entityModelData = objectMapper.treeToValue(stuff, EntityModelData.class);
//        } catch (JsonProcessingException e) {
//            throw new ContentProviderException(e);
//        }
//
//        return entityModelData;
//    }

//    /**
//     * DXA supports "extensionless URLs" and "index pages".
//     * For example: the index Page at root level (aka the Home Page) has a CM URL path of /index.html
//     * It can be addressed in the DXA web application in several ways:
//     *   1. /index.html – exactly the same as the CM URL
//     *   2. /index – the file extension doesn't have to be specified explicitly {"extensionless URLs")
//     *   3. / - the file name of an index page doesn't have to be specified explicitly either.
//     * Note that the third option is the most clean one and considered the "canonical URL" in DXA; links to an index Page will be generated like that.
//     * The problem with these "URL compression" features is that if a URL does not end with a slash (nor an extension), you don't
//     * know upfront if the URL addresses a regular Page or an index Page (within a nested SG).
//     * To determine this, DXA first tries the regular Page and if it doesn't exist, it appends /index.html and tries again.
//     * TODO: The above should be handled by PCA (See CRQ-11703)
//     *
//     */
//    public PageModelData getPageModelData(String path, int publicationId) throws ContentProviderException {
//        JsonNode response = pcaClient.getPageModelData(ContentNamespace.Sites, publicationId,
//                getCanonicalUrlPath(path, false), ContentType.MODEL, DataModelType.R2, PageInclusion.INCLUDE,
//                ContentIncludeMode.INCLUDE_DATA_AND_RENDER, null);
//        if (response instanceof MissingNode) {
//            //Try again with the index page
//            response = pcaClient.getPageModelData(ContentNamespace.Sites, publicationId,
//                    getCanonicalUrlPath(path, true), ContentType.MODEL, DataModelType.R2, PageInclusion.INCLUDE,
//                    ContentIncludeMode.INCLUDE_DATA_AND_RENDER, null);
//        }
//
//        PageModelData pageModelData = null;
//        try {
//            pageModelData = mapToType(PageModelData.class, response);
//        } catch (JsonProcessingException e) {
//            throw new ContentProviderException(e);
//        }
//        return pageModelData;
//    }

    ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy());
        objectMapper.registerModule(new JodaModule());
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Polymorphic.class));
        scanner.findCandidateComponents(DataModelSpringConfiguration.class.getPackage().getName())
                .forEach(type -> {
                    try {
                        Class<?> aClass = forName(type.getBeanClassName(), getDefaultClassLoader());
                        objectMapper.addMixIn(aClass, PolymorphicObjectMixin.class);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class not found while mapping model data to typeIDs. Should never happen.", e);
                    }
                });
        objectMapper.addMixIn(Object.class, PolymorphicObjectMixin.class);

        return objectMapper;
    }

//    /**
//     * Copied from .Net implementation.
//     *
//     * @param urlPath
//     * @param tryIndexPage
//     * @return
//     */
//    private static String getCanonicalUrlPath(String urlPath, boolean tryIndexPage)
//    {
//        if (urlPath == null)
//            return "/" + Constants.DEFAULT_EXTENSIONLESS_PAGENAME + Constants.DEFAULT_EXTENSION;
//
//        if (urlPath.endsWith(Constants.DEFAULT_EXTENSION))
//            return urlPath;
//
//        if (urlPath.lastIndexOf(".") > 0)
//            return urlPath;
//
//        if (!urlPath.startsWith("/"))
//            urlPath = "/" + urlPath;
//
//        urlPath = StringUtils.stripEnd(urlPath, "/");
//
//        if (StringUtils.isEmpty(urlPath))
//            return "/" + Constants.DEFAULT_EXTENSIONLESS_PAGENAME + Constants.DEFAULT_EXTENSION;
//
//        return tryIndexPage
//                ? urlPath + "/" + Constants.DEFAULT_EXTENSIONLESS_PAGENAME + Constants.DEFAULT_EXTENSION
//                : urlPath + Constants.DEFAULT_EXTENSION;
//    }

    private <T> T mapToType(Class<T> type, JsonNode result) throws JsonProcessingException {
        if (type.equals(String.class)) {
            return (T) result.toString();
        }
        return objectMapper.treeToValue(result, type);
    }

}
