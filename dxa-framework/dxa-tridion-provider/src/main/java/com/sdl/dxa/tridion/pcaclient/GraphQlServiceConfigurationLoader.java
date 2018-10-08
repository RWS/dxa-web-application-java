package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.configuration.api.ConfigurationHolder;
import com.sdl.web.client.impl.util.ClientsUtil;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import com.tridion.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Properties;

@Service("GraphQlServiceConfigurationLoader")
public class GraphQlServiceConfigurationLoader extends BaseClientConfigurationLoader implements PCAClientConfigurationLoader {
    private static final Logger LOG = LoggerFactory.getLogger(GraphQlServiceConfigurationLoader.class);

    private String serviceUrl;
    private volatile boolean initialized = false;

    public GraphQlServiceConfigurationLoader() throws ConfigurationException {
        super();
    }

    Optional<ContentServiceCapability> getContentServiceCapability() throws ConfigurationException {
        return this.getCapabilityFromDiscoveryService(ContentServiceCapability.class,
                new String[0]);
    }

    synchronized void initialize() {
        if (initialized) return;
        try {
            Optional<ContentServiceCapability> contentServiceCapability = getContentServiceCapability();
            String contentServiceUrl;
            if (contentServiceCapability.isPresent()) {
                contentServiceUrl = contentServiceCapability.get().getUri();
            } else {
                if (!this.getRootConfigHolder().hasConfiguration("/ContentService")) {
                    throw new ConfigurationException("Unable to load content service url!");
                }

                ConfigurationHolder configuration = this.getRootConfigHolder().getConfiguration("/ContentService");
                contentServiceUrl = configuration.getValue("ServiceUri");
                LOG.info("Successfully resolved capability uri: {} using fallback variant.", this.serviceUrl);
            }
            this.serviceUrl = ClientsUtil.makeEndWithoutSlash(StringUtils.replace(contentServiceUrl, "content.svc",
                    "cd/api"));
        } catch (ConfigurationException e) {
            throw new PCAConfigurationException("Exception during loading PCA client configuration", e);
        }
        LOG.debug("The Public Content API endpoint is '{}'", serviceUrl);
        initialized = true;
    }

    @Override
    public String getServiceUrl() {
        if (!initialized) {
            initialize();
        }
        return serviceUrl;
    }

    @Override
    public Properties getConfiguration() {
        if (!initialized) {
            initialize();
        }

        Properties props = new Properties();
        props.putAll(getProps());
        props.put("ServiceUri", ClientsUtil.makeEndWithoutSlash(StringUtils.replace(this.serviceUrl, "content.svc", "client/v4/content.svc")));
        return props;
    }

    Properties getProps() {
        return this.getCommonProps();
    }
}
