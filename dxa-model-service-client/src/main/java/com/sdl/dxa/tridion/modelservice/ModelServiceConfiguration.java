package com.sdl.dxa.tridion.modelservice;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.sdl.web.discovery.datalayer.model.KeyValuePair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ModelServiceConfiguration extends BaseClientConfigurationLoader {

    private static final Logger log = getLogger(ModelServiceConfiguration.class);

    private final String pageModelUrl;

    private final String entityModelUrl;

    private final String navigationApiUrl;

    private final String onDemandApiUrl;

    private OAuthTokenProvider oAuthTokenProvider;

    private String serviceUrl;

    public ModelServiceConfiguration(
            @Value("${dxa.model.service.url.page.model}") String pageModelUrl,
            @Value("${dxa.model.service.url.entity.model}") String entityModelUrl,
            @Value("${dxa.model.service.url.api.navigation}") String navigationApiUrl,
            @Value("${dxa.model.service.url.api.navigation.subtree}") String onDemandApiUrl,
            @Value("${dxa.model.service.key}") String modelServiceKey) throws ConfigurationException {
        this(pageModelUrl, entityModelUrl, navigationApiUrl, onDemandApiUrl, modelServiceKey, null);
    }

    @Autowired
    public ModelServiceConfiguration(
            @Value("${dxa.model.service.url.page.model}") String pageModelUrl,
            @Value("${dxa.model.service.url.entity.model}") String entityModelUrl,
            @Value("${dxa.model.service.url.api.navigation}") String navigationApiUrl,
            @Value("${dxa.model.service.url.api.navigation.subtree}") String onDemandApiUrl,
            @Value("${dxa.model.service.key:#{null}}") String modelServiceKey,
            @Value("${dxa.model.service.url:#{null}}") String modelServiceUrl) throws ConfigurationException {
        if (isTokenConfigurationAvailable()) {
            this.oAuthTokenProvider = new OAuthTokenProvider(getOauthTokenProviderConfiguration());
            // try to get token to validate credentials
            this.oAuthTokenProvider.getToken();
        }

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
        this.navigationApiUrl = this.serviceUrl + navigationApiUrl;
        this.onDemandApiUrl = this.serviceUrl + onDemandApiUrl;
    }

    @Override
    protected String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Returns a health check URL for the Model Service. HTTP status after calling to the returned endpoint is supposed to show the status of Model Service.
     *
     * @return URL of the MS for the health check
     */
    public String getHealthCheckUrl() {
        return getServiceUrl();
    }

    public String getPageModelUrl() {
        return pageModelUrl;
    }

    public String getEntityModelUrl() {
        return entityModelUrl;
    }

    public String getNavigationApiUrl() {
        return navigationApiUrl;
    }

    public String getOnDemandApiUrl() {
        return onDemandApiUrl;
    }

    public OAuthTokenProvider getOAuthTokenProvider() {
        return oAuthTokenProvider;
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
