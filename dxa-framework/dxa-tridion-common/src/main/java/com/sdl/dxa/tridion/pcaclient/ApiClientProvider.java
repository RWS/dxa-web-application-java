package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.GraphQLClient;

/**
 * Provides instance of Api Client and GraphQL client which is works with environment configured in cd_client_conf.xml
 * configuration file.
 */
public interface ApiClientProvider {

    /**
     * Returns configured instance of ApiClient client.
     *
     * @return
     */
    ApiClient getClient();

    /**
     * Returns configured instance of GraphQLClient client.
     *
     * @return
     */
    GraphQLClient getGraphQLClient();
}
