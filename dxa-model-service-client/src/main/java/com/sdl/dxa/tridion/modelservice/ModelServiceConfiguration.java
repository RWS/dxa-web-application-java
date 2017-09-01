package com.sdl.dxa.tridion.modelservice;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.sdl.web.discovery.datalayer.model.KeyValuePair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

@Component
// this artifact is to be used with older versions of DXA or DD4T which use Spring 3, which fails on lambdas
@SuppressWarnings("Convert2Lambda")
public class ModelServiceConfiguration extends BaseClientConfigurationLoader {

    private static final Logger log = getLogger(ModelServiceConfiguration.class);

    @Value("${dxa.model.service.url.page.model}")
    private String pageModelUrl;

    @Value("${dxa.model.service.url.entity.model}")
    private String entityModelUrl;

    @Value("${dxa.model.service.url.api.navigation}")
    private String navigationApiUrl;

    @Value("${dxa.model.service.url.api.navigation.subtree}")
    private String onDemandApiUrl;

    @Value("${dxa.model.service.key:#{null}}")
    private String modelServiceKey;

    @Value("${dxa.model.service.url:#{null}}")
    private String modelServiceUrl;

    private OAuthTokenProvider oAuthTokenProvider;

    private String serviceUrl;

    public ModelServiceConfiguration() throws ConfigurationException {
        //required empty
    }

    @PostConstruct
    public void init() throws ConfigurationException {
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
                    .filter(new Predicate<KeyValuePair>() { // NOSONAR
                        @Override
                        public boolean test(KeyValuePair keyValuePair) {
                            return Objects.equals(keyValuePair.getKey(), modelServiceKey);
                        }
                    })
                    .map(new Function<KeyValuePair, String>() { // NOSONAR
                        @Override
                        public String apply(KeyValuePair keyValuePair) {
                            return keyValuePair.getValue();
                        }
                    })
                    .findFirst()
                    .orElseThrow(new Supplier<ConfigurationException>() { // NOSONAR
                        @Override
                        public ConfigurationException get() {
                            return new ConfigurationException("DXA Model Service URL is not available on Discovery");
                        }
                    });
        } else {
            throw new ConfigurationException("ContentServiceCapability is not available, cannot get Model Service url");
        }
    }
}
