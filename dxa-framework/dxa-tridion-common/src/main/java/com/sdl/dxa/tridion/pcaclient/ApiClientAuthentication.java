package com.sdl.dxa.tridion.pcaclient;

import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.pca.client.auth.Authentication;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sdl.dxa.tridion.common.ConfigurationConstants.AUTHORIZATION_HEADER;

@Service("ApiClientAuthentication")
public class ApiClientAuthentication implements Authentication {
    private static final Logger LOG = LoggerFactory.getLogger(ApiClientAuthentication.class);

    private OAuthTokenProvider tokenProvider;

    ApiClientAuthentication() {
    }

    @Autowired
    public ApiClientAuthentication(GraphQlServiceConfigurationLoader configurationLoader) {
        try {
            tokenProvider = new OAuthTokenProvider(configurationLoader.getOauthTokenProviderConfiguration());
        } catch (ConfigurationException e) {
            LOG.warn("Unable to read configuration for token provider.", e);
        } catch (ODataClientRuntimeException e) {
            LOG.warn("Unable to initialize Token Provider.", e);
        }
    }

    @Override
    public void applyManualAuthentication(HttpRequest request) {
        if (tokenProvider != null) {
            LOG.trace("Request is secured, adding security token.");
            request.addHeader(AUTHORIZATION_HEADER, "Bearer" + tokenProvider.getToken());
        } else {
            LOG.trace("Request is not secured. Token provider is not available.");
        }
    }

    void setTokenProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
}
