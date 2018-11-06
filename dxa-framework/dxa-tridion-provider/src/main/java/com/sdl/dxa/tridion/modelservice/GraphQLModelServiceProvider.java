package com.sdl.dxa.tridion.modelservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentIncludeMode;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.web.pca.client.contentmodel.enums.DataModelType;
import com.sdl.web.pca.client.contentmodel.enums.DcpType;
import com.sdl.web.pca.client.contentmodel.enums.PageInclusion;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.sdl.dxa.common.util.PathUtils.normalizePathToDefaults;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

@Slf4j
@Service(value = "GraphQLModelServiceProvider")
@Profile("!cil.providers.active")
public class GraphQLModelServiceProvider implements ModelServiceProvider {

    private ApiClientProvider apiClientProvider;

    private ApiClient pcaClient;

    private ObjectMapper mapper;

    GraphQLModelServiceProvider() {
        this.mapper = getObjectMapper();
    }

    @Autowired
    public GraphQLModelServiceProvider(ApiClientProvider apiClientProvider) {
        this.apiClientProvider = apiClientProvider;
        this.pcaClient = apiClientProvider.getClient();
        this.mapper = getObjectMapper();
    }

    @NotNull
    @Override
    public PageModelData loadPageModel(PageRequestDto pageRequest) throws ContentProviderException {
        return _loadPage(PageModelData.class, pageRequest, ContentType.MODEL);
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
        return _loadPage(String.class, pageRequest, ContentType.RAW);
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
    private <T> T _loadPage(Class<T> type, PageRequestDto pageRequest, ContentType contentType) throws ContentProviderException {
        try {
            JsonNode pageNode = pcaClient.getPageModelData(
                    ContentNamespace.Sites,
                    pageRequest.getPublicationId(),
                    normalizePathToDefaults(pageRequest.getPath()),
                    contentType,
                    DataModelType.valueOf(pageRequest.getDataModelType().toString()),
                    PageInclusion.valueOf(pageRequest.getIncludePages().toString()),
                    ContentIncludeMode.INCLUDE,
                    null
            );
            T result = mapToType(type, pageNode);
            log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
            return result;
        } catch (IOException e) {
            log.debug("Page not found by path {}, trying to find by path {}",
                    normalizePathToDefaults(pageRequest.getPath()),
                    normalizePathToDefaults(pageRequest.getPath(), true));
            try {
                JsonNode pageNode = pcaClient.getPageModelData(
                        ContentNamespace.Sites,
                        pageRequest.getPublicationId(),
                        normalizePathToDefaults(pageRequest.getPath(), true),
                        contentType,
                        DataModelType.valueOf(pageRequest.getDataModelType().toString()),
                        PageInclusion.valueOf(pageRequest.getIncludePages().toString()),
                        ContentIncludeMode.INCLUDE,
                        null
                );
                T result = mapToType(type, pageNode);
                log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
                return result;
            } catch (IOException ex) {
                throw new PageNotFoundException("Unable to load page, by request " + pageRequest, ex);
            }
        }
    }

    private <T> T mapToType(Class<T> type, JsonNode result) throws IOException {
        if (type.equals(String.class)) {
            return (T) result.toString();
        }
        return mapper.readValue(result.toString(), type);
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
            JsonNode node = pcaClient.getEntityModelData(
                    ContentNamespace.Sites,
                    entityRequest.getPublicationId(),
                    entityRequest.getComponentId(),
                    entityRequest.getTemplateId(),
                    ContentType.valueOf(entityRequest.getContentType().toString()),
                    DataModelType.valueOf(entityRequest.getDataModelType().toString()),
                    DcpType.valueOf(entityRequest.getDcpType().toString()),
                    ContentIncludeMode.INCLUDE_AND_RENDER,
                    null
            );

            EntityModelData modelData = mapper.readValue(node.toString(), EntityModelData.class);
            log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
            return modelData;
        } catch (IOException e) {
            throw new ContentProviderException("Entity " + entityRequest + " not found", e);
        }
    }

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
}
