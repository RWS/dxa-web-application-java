package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.configuration.api.ConfigurationHolder;
import com.sdl.web.client.impl.util.ClientsUtil;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.tridion.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Properties;

import static com.sdl.dxa.tridion.common.ConfigurationConstants.CONTENT_SERVICE;
import static com.sdl.dxa.tridion.common.ConfigurationConstants.CONTENT_SERVICE_CONTEXT_PATH;
import static com.sdl.dxa.tridion.common.ConfigurationConstants.SERVICE_URI;

@Service("GraphQlServiceConfigurationLoader")
public class GraphQlServiceConfigurationLoader extends BaseClientConfigurationLoader implements ApiClientConfigurationLoader {
    private static final Logger LOG = LoggerFactory.getLogger(GraphQlServiceConfigurationLoader.class);
    private static final String[] EMPTY = new String[0];

    private String serviceUrl;
    private boolean initialized = false;
    private String endpointContext;
    private boolean claimForwarding;

    public GraphQlServiceConfigurationLoader(@Value("${dxa.graphql.endpoint:cd/api}") String endpointContext,
                                             @Value("${dxa.graphql.claimforwarding:true}") boolean claimForwarding) throws ConfigurationException {
        super();
        this.endpointContext = endpointContext;
        this.claimForwarding = claimForwarding;
    }

    Optional<ContentServiceCapability> getContentServiceCapability() throws ConfigurationException {
        return this.getCapabilityFromDiscoveryService(ContentServiceCapability.class, EMPTY);
    }

    synchronized void initialize() {
        if (initialized) return;
        try {
            Optional<ContentServiceCapability> contentServiceCapability = getContentServiceCapability();
            String contentServiceUrl = null;
            if (contentServiceCapability.isPresent()) {
                contentServiceUrl = contentServiceCapability.get().getUri();
                LOG.info("Successfully got capability uri: {}.", contentServiceUrl);
            } else {
                ConfigurationHolder rootConfigHolder = getRootConfigHolder();
                if (!rootConfigHolder.hasConfiguration("/" + CONTENT_SERVICE)) {
                    throw new ConfigurationException("Unable to load content service url!");
                }
                ConfigurationHolder configuration = rootConfigHolder.getConfiguration("/" + CONTENT_SERVICE);
                contentServiceUrl = configuration.getValue(SERVICE_URI);
                LOG.info("Successfully got capability uri: {} using fallback variant.", contentServiceUrl);
            }
            this.serviceUrl = ClientsUtil.makeEndWithoutSlash(StringUtils.replace(contentServiceUrl,
                    CONTENT_SERVICE_CONTEXT_PATH,
                    endpointContext));
            LOG.info("The Public Content API endpoint is '{}'", serviceUrl);
            initialized = true;
        } catch (ConfigurationException e) {
            throw new ApiClientConfigurationException("Exception during loading Api Client configuration, " +
                    "endpoint: [" + endpointContext + "]", e);
        }
    }

    synchronized boolean isInitialized() {
        return initialized;
    }

    @Override
    public String getServiceUrl() {
        initialize();
        return serviceUrl;
    }

    @Override
    public boolean claimForwarding() {
        return this.claimForwarding;
    }

    @Override
    public Properties getConfiguration() {
        initialize();
        Properties properties = new Properties();
        properties.putAll(getCommonProperties());
        final String serviceUrl;
        synchronized (this) {
            serviceUrl = this.serviceUrl;
        }
        String fullUrl = StringUtils.replace(serviceUrl, CONTENT_SERVICE_CONTEXT_PATH, endpointContext);
        properties.put(SERVICE_URI, ClientsUtil.makeEndWithoutSlash(fullUrl));
        return properties;
    }

    Properties getCommonProperties() {
        return this.getCommonProps();
    }

    @Override
    protected ConfigurationHolder getRootConfigHolder() {
        return super.getRootConfigHolder();
    }
}