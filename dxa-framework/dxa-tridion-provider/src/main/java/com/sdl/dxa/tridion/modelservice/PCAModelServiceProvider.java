package com.sdl.dxa.tridion.modelservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Strings;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.json.PolymorphicObjectMixin;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.dxa.tridion.pcaclient.PCAClientProvider;
import com.sdl.web.pca.client.PublicContentApi;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentIncludeMode;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;
import com.sdl.web.pca.client.contentmodel.enums.DataModelType;
import com.sdl.web.pca.client.contentmodel.enums.DcpType;
import com.sdl.web.pca.client.contentmodel.enums.PageInclusion;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

@Slf4j
@Service(value = "PCAModelService")
@Profile("!cil.providers.active")
@Primary
public class PCAModelServiceProvider implements ModelServiceProvider {

    private final PCAClientProvider pcaClientProvider;

    private final PublicContentApi pcaClient;

    private final ObjectMapper mapper;

    @Autowired
    public PCAModelServiceProvider(PCAClientProvider pcaClientProvider) {
        this.pcaClientProvider = pcaClientProvider;
        this.pcaClient = pcaClientProvider.getClient();
        this.mapper = getObjectMapper();
    }

    @NotNull
    @Override
    public PageModelData loadPageModel(PageRequestDto pageRequest) {
        return _loadPage(PageModelData.class, pageRequest);
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
    public String loadPageContent(PageRequestDto pageRequest) {
        return _loadPage(String.class, pageRequest);
    }

    private String getPathUrl(String path) {
        if (path.equals("/"))
            path = path + "index.html";
        return path;
    }

    private <T> T _loadPage(Class<T> type, PageRequestDto pageRequest) {
        try {
            JsonNode pageNode = pcaClient.getPageModelData(ContentNamespace.Sites, pageRequest.getPublicationId(),
                    getPathUrl(pageRequest.getPath()), ContentType.RAW, DataModelType.R2, PageInclusion.INCLUDE,
                    ContentIncludeMode.INCLUDE, null);
            T result = (T) mapper.readValue(pageNode.toString(), PageModelData.class);
            log.trace("Loaded '{}' for pageRequest '{}'", result, pageRequest);
            return result;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    public EntityModelData loadEntity(String publicationId, @NotNull String entityId) throws ContentProviderException {
        return loadEntity(EntityRequestDto.builder(publicationId, entityId).entityId(entityId).build());
    }

    @NotNull
    @Override
    public EntityModelData loadEntity(EntityRequestDto entityRequest) throws ContentProviderException {
        try {
            //todo finish this class
            JsonNode node = pcaClient.getEntityModelData(ContentNamespace.Sites, 8, 1458, 9195,
                    ContentType.MODEL, DataModelType.R2, DcpType.DEFAULT, ContentIncludeMode.EXCLUDE, new ContextData());
            EntityModelData modelData = mapper.readValue(node.toString(), EntityModelData.class);

            log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
            return modelData;
        } catch (IOException e) {
            e.printStackTrace();
            throw new DxaItemNotFoundException("Entity " + entityRequest + " not found in the Model Service", e);
        }
    }

    private ObjectMapper getObjectMapper() {
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
                        log.warn("Class not found while mapping model data to typeIDs. Should never happen.", e);
                    }
                });
        objectMapper.addMixIn(Object.class, PolymorphicObjectMixin.class);

        return objectMapper;
    }
}
