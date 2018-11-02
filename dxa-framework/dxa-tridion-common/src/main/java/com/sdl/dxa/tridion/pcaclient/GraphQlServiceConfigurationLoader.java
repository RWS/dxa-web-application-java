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
    private volatile boolean initialized = false;
    private String endpointContext;

    public GraphQlServiceConfigurationLoader(@Value("${dxa.graphql.endpoint:cd/api}") String endpointContext) throws ConfigurationException {
        super();
        this.endpointContext = endpointContext;
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
            LOG.debug("The Public Content API endpoint is '{}'", serviceUrl);
        } catch (ConfigurationException e) {
            throw new ApiClientConfigurationException("Exception during loading Api Client configuration, " +
                    "endpoint: [" + endpointContext + "]", e);
        }
        initialized = true;
    }

    boolean isInitialized() {
        return initialized;
    }

    @Override
    public String getServiceUrl() {
        initialize();
        return serviceUrl;
    }

    @Override
    public Properties getConfiguration() {
        initialize();
        Properties properties = new Properties();
        properties.putAll(getCommonProperties());
        properties.put(SERVICE_URI, ClientsUtil.makeEndWithoutSlash(StringUtils.replace(this.serviceUrl,
                CONTENT_SERVICE_CONTEXT_PATH,
                endpointContext)));
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