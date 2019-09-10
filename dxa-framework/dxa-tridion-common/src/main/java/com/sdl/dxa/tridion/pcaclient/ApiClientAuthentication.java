package com.sdl.dxa.tridion.pcaclient;

import com.sdl.odata.client.api.exception.ODataClientRuntimeException;
import com.sdl.web.client.OAuthClient;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.impl.DefaultOAuthClient;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.sdl.web.oauth.common.OAuthToken;
import com.sdl.web.pca.client.auth.Authentication;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static com.sdl.dxa.tridion.common.ConfigurationConstants.AUTHORIZATION_HEADER;
import static com.sdl.odata.client.property.PropertyUtils.getStringProperty;
import static com.sdl.web.client.configuration.ClientConstants.Security.CLIENT_ID;
import static com.sdl.web.client.configuration.ClientConstants.Security.CLIENT_SECRET;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;

@Service("ApiClientAuthentication")
@Profile("!cil.providers.active")
public class ApiClientAuthentication implements Authentication {
    private static final Logger LOG = LoggerFactory.getLogger(ApiClientAuthentication.class);

    private OAuthTokenProvider tokenProvider;

    ApiClientAuthentication() {
    }

    private static class OAuthTokenProviderInternal extends OAuthTokenProvider {
        private OAuthToken oAuthToken;
        private OAuthClient oAuthClient;
        private String clientId;
        private String clientSecret;

        public OAuthTokenProviderInternal(Properties properties) {
            super(properties);
            this.clientId = getStringProperty(properties, CLIENT_ID);
            requireNonNull(clientId, "Client id is required!");
            this.clientSecret = getStringProperty(properties, CLIENT_SECRET);
            requireNonNull(clientSecret, "Client secret is required!");
            oAuthClient = new DefaultOAuthClient(properties);
            LOG.info("OAuth token initialized");
        }

        public synchronized boolean isTokenExpired() {
            boolean tokenExpired = oAuthToken == null || now().toEpochMilli() > oAuthToken.getExpiresOn();
            if (tokenExpired) {
                LOG.info("OAuth token expired! Taking another one..." + (oAuthToken == null ? " was null":""));
            }
            return tokenExpired;
        }

        private String getTokenInternal(int attempt) {
            if (attempt < 0) throw new IllegalStateException("Could not obtain new oAuth token!");
            if (!isTokenExpired()) {
                return oAuthToken.getToken();
            }
            try {
                if (oAuthToken == null) {
                    LOG.info("Creating a token...");
                    oAuthToken = oAuthClient.getToken(clientId, clientSecret);
                    LOG.info("New token created");
                }
                if (isTokenExpired()) {
                    LOG.info("Refreshing a token...");
                    oAuthToken = oAuthClient.refreshToken(clientId, oAuthToken);
                    LOG.info("Refresh token created");
                }
                String token = oAuthToken.getToken();
                LOG.info("Returning a token " + token);
                return token;
            } catch (Exception e) {
                if (attempt == 0) {
                    LOG.error("Obtaining token using refresh token not successful: {}." +
                            " Trying to obtain new one using client id/secret.", e);
                } else {
                    LOG.info("Attempt #"+attempt+" to get new token!");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    LOG.error("Interrupted");
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted is fired");
                }
                return getTokenInternal(attempt--);
            }
        }

        public synchronized String getToken() {
            return getTokenInternal(5);
        }
    }

    @Autowired
    public ApiClientAuthentication(GraphQlServiceConfigurationLoader configurationLoader) {
        try {
            tokenProvider = new OAuthTokenProviderInternal(configurationLoader.getOauthTokenProviderConfiguration());
        } catch (ConfigurationException e) {
            LOG.warn("Unable to read configuration for token provider.", e);
        } catch (ODataClientRuntimeException e) {
            LOG.warn("Unable to initialize Token Provider.", e);
        }
    }

    @Override
    public void applyManualAuthentication(HttpRequest request) {
        if (tokenProvider != null) {
            LOG.debug("Request is secured, adding security token");
            request.addHeader(AUTHORIZATION_HEADER, "Bearer " + tokenProvider.getToken());
        } else {
            LOG.trace("Request is not secured. Token provider is not available.");
        }
    }

    void setTokenProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
}
