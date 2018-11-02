package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.DefaultGraphQLClient;
import com.sdl.web.pca.client.DefaultApiClient;
import com.sdl.web.pca.client.GraphQLClient;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sdl.dxa.tridion.common.ConfigurationConstants.CONNECTION_TIMEOUT;

@Service("DefaultApiClientProvider")
public class DefaultApiClientProvider implements ApiClientProvider {

    private ApiClient apiClient;

    private GraphQLClient graphQLClient;

    @Autowired
    public DefaultApiClientProvider(ApiClientConfigurationLoader configurationLoader, Authentication auth) {
        this.graphQLClient = new DefaultGraphQLClient(configurationLoader.getServiceUrl(), null, auth);
        this.apiClient = new DefaultApiClient(graphQLClient,
                Integer.valueOf(configurationLoader.getConfiguration()
                        .getOrDefault(CONNECTION_TIMEOUT, 0).toString()));
    }

    @Override
    public ApiClient getClient() {
        return apiClient;
    }

    @Override
    public GraphQLClient getGraphQLClient() {
        return graphQLClient;
    }
}
