package com.sdl.dxa.tridion.modelservice;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.sdl.web.discovery.datalayer.model.KeyValuePair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Getter
@Component
public class ModelServiceConfiguration extends BaseClientConfigurationLoader {

    private OAuthTokenProvider oAuthTokenProvider;

    @Getter(AccessLevel.PROTECTED)
    private String serviceUrl;

    private String pageModelUrl;

    private String entityModelUrl;

    public ModelServiceConfiguration(
            @Value("${dxa.model.service.url.page.model}") String pageModelUrl,
            @Value("${dxa.model.service.url.entity.model}") String entityModelUrl,
            @Value("${dxa.model.service.key:#{null}}") String modelServiceKey,
            @Value("${dxa.model.service.url:#{null}}") String modelServiceUrl) throws ConfigurationException {
        if (modelServiceUrl != null) {
            log.debug("Using Model Service Url {} from properties", modelServiceUrl);
            this.serviceUrl = modelServiceUrl;
        } else {
            Assert.notNull(modelServiceKey, "At least one of two properties required: dxa.model.service.key, dxa.model.service.url");
            this.serviceUrl = loadServiceUrlFromCapability(modelServiceKey);
            log.debug("Using Model Service Url {} from Discovery Service", this.serviceUrl);
        }

        this.pageModelUrl = this.serviceUrl + pageModelUrl;
        this.entityModelUrl = this.serviceUrl + entityModelUrl;

        if (isTokenConfigurationAvailable()) {
            this.oAuthTokenProvider = new OAuthTokenProvider(getOauthTokenProviderConfiguration());
        }
    }

    private String loadServiceUrlFromCapability(String modelServiceKey) throws ConfigurationException {
        Optional<ContentServiceCapability> capability = getCapabilityFromDiscoveryService(ContentServiceCapability.class);
        if (capability.isPresent()) {
            return capability.get().getExtensionProperties().stream()
                    .filter(keyValuePair -> Objects.equals(keyValuePair.getKey(), modelServiceKey))
                    .map(KeyValuePair::getValue)
                    .findFirst()
                    .orElseThrow(() -> new ConfigurationException("DXA Model Service URL is not available on Discovery"));
        } else {
            throw new ConfigurationException("ContentServiceCapability is not available, cannot get Model Service url");
        }
    }
}
