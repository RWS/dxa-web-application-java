package com.sdl.dxa.tridion.rest;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.web.client.configuration.XMLConfigurationHolder;
import com.sdl.web.client.configuration.XMLConfigurationReaderImpl;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.DefaultDiscoveryClient;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.sdl.web.discovery.datalayer.model.KeyValuePair;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.web.WebClaims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

@Slf4j
@Service
public class DefaultModelService implements ModelService {

    private static final String X_PREVIEW_SESSION_TOKEN = "x-preview-session-token";

    private static final String PREVIEW_SESSION_TOKEN = "preview-session-token";

    private final RestTemplate restTemplate;

    @Value("${cil.cd.client.conf}")
    private String configurationFileName;

    @Value("${dxa.model.service.key}")
    private String modelServiceKey;

    @Value("${dxa.model.service.url:#{null}}")
    private String modelServiceUrl;

    @Value("${dxa.model.service.url.page.model}")
    private String pageModelUrl;

    @Value("${dxa.model.service.url.entity.model}")
    private String entityModelUrl;

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    public DefaultModelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void initService() throws ConfigurationException {
        if (modelServiceUrl != null) {
            log.debug("Using model service URL from properties, don't ask Discovery, {}", modelServiceUrl);
        } else {
            XMLConfigurationHolder configuration = (new XMLConfigurationReaderImpl()).readConfiguration(configurationFileName);
            String serviceUri = configuration.getConfiguration("/DiscoveryService").getValue("ServiceUri");

            Properties properties = new Properties();
            properties.put("ServiceUri", serviceUri);

            modelServiceUrl = new DefaultDiscoveryClient(properties).getCapability(ContentServiceCapability.class)
                    .orElseThrow(() -> new ConfigurationException("ContentServiceCapability is not available on Discovery " + serviceUri))
                    .getExtensionProperties().stream()
                    .filter(keyValuePair -> Objects.equals(keyValuePair.getKey(), modelServiceKey))
                    .map(KeyValuePair::getValue)
                    .findFirst()
                    .orElseThrow(() -> new ConfigurationException("DXA Model Service URL is not available on Discovery"));

            log.debug("Using model service from Discovery, {}", modelServiceUrl);
        }

        this.pageModelUrl = modelServiceUrl + pageModelUrl;
        this.entityModelUrl = modelServiceUrl + entityModelUrl;
    }

    @NotNull
    @Override
    @Cacheable(value = "default")
    public PageModelData loadPageModel(PageRequestDto pageRequest) throws ContentProviderException {
        return _loadPage(pageModelUrl, PageModelData.class, pageRequest);
    }

    private <T> T _loadPage(String serviceUrl, Class<T> type, PageRequestDto pageRequest) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        try {
            T page = _processRequest(serviceUrl, type,
                    pageRequest.getUriType(),
                    pageRequest.getPublicationId() != 0 ? pageRequest.getPublicationId() : localization.getId(),
                    pageRequest.getPath(),
                    pageRequest.getIncludePages());
            log.trace("Loaded '{}' for localization '{}' and pageRequest '{}'", page, localization, pageRequest);
            return page;
        } catch (DxaItemNotFoundException e) {
            throw new PageNotFoundException("Cannot load page '" + pageRequest + "'", e);
        }
    }

    private <T> T _processRequest(String serviceUrl, Class<T> type, Object... params) throws ContentProviderException {
        try {
            HttpHeaders headers = new HttpHeaders();

            //noinspection unchecked
            String previewToken = _getClaimValue(WebClaims.REQUEST_HEADERS, X_PREVIEW_SESSION_TOKEN,
                    claim -> Optional.of(((List<String>) claim).get(0)))
                    .orElseGet(() -> _getClaimValue(WebClaims.REQUEST_COOKIES, PREVIEW_SESSION_TOKEN,
                            claim -> Optional.of(claim.toString()))
                            .orElse(null));

            if (previewToken != null) {
                // commented because of bug in CIS https://jira.sdl.com/browse/CRQ-3935
                // headers.add(X_PREVIEW_SESSION_TOKEN, previewToken);
                headers.add(HttpHeaders.COOKIE, String.format("%s=%s", PREVIEW_SESSION_TOKEN, previewToken));
            }

            ResponseEntity<T> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(headers), type, params);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            HttpStatus statusCode = e.getStatusCode();
            log.info("Got response with a status code {}", statusCode);
            if (statusCode.is4xxClientError()) {
                throw new DxaItemNotFoundException("Item not found requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
            }
            throw new ContentProviderException("Internal server error requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
        }
    }

    @NotNull
    private Optional<String> _getClaimValue(URI uri, String key, Function<Object, Optional<String>> deriveValue) {
        ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
        if (claimStore != null) {
            Map claims = claimStore.get(uri, Map.class);
            if (claims != null && claims.containsKey(key)) {
                return deriveValue.apply(claims.get(key));
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    @Cacheable(value = "default")
    public String loadPageContent(PageRequestDto pageRequest) throws ContentProviderException {
        String serviceUrl = UriComponentsBuilder.fromUriString(pageModelUrl).queryParam("raw").build().toUriString();
        return _loadPage(serviceUrl, String.class, pageRequest);
    }

    @NotNull
    @Override
    @Cacheable(value = "default", key = "'emd-entityId-' + #entityId")
    public EntityModelData loadEntity(@NotNull String entityId) throws ContentProviderException {
        return loadEntity(EntityRequestDto.builder().entityId(entityId).build());
    }

    @NotNull
    @Override
    @Cacheable(value = "default")
    public EntityModelData loadEntity(EntityRequestDto entityRequest) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();

        EntityModelData modelData = _processRequest(entityModelUrl, EntityModelData.class,
                entityRequest.getUriType(),
                entityRequest.getPublicationId() != 0 ? entityRequest.getPublicationId() : localization.getId(),
                entityRequest.getComponentId(),
                entityRequest.getTemplateId());
        log.trace("Loaded '{}' for entityId '{}'", modelData, entityRequest.getComponentId());
        return modelData;
    }
}
