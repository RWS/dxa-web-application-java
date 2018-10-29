package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.GraphQLClient;
import com.sdl.web.pca.client.PublicContentApi;

/**
 * Provides instance of PCA and GraphQL client which is works with environment configured in cd_client_conf.xml
 * configuration file.
 */
public interface PCAClientProvider {

    /**
     * Returns configured instance of PublicContentApi client.
     *
     * @return
     */
    PublicContentApi getClient();

    /**
     * Returns configured instance of GraphQLClient client.
     *
     * @return
     */
    GraphQLClient getGraphQLClient();
}
