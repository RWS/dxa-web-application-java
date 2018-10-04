package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.pca.client.auth.Authentication;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PCAAuthentication")
public class PCAAuthentication implements Authentication {
    private static final Logger LOG = LoggerFactory.getLogger(PCAAuthentication.class);

    private OAuthTokenProvider tokenProvider;

    PCAAuthentication() {
    }

    @Autowired
    public PCAAuthentication(GraphQlServiceConfigurationLoader configurationLoader) {
        try {
            tokenProvider = new OAuthTokenProvider(configurationLoader.getOauthTokenProviderConfiguration());
        } catch (ConfigurationException e) {
            LOG.warn("Unable to read configuration for token provider.", e);
        }
    }

    @Override
    public void applyManualAuthentication(HttpRequest request) {
        if (tokenProvider != null) {
            LOG.trace("Request is secured, adding security token.");
            request.addHeader("Authorization", "Bearer" + tokenProvider.getToken());
        } else {
            LOG.trace("Request is not secured. Token provider is not available.");
        }
    }

    void setTokenProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
}
