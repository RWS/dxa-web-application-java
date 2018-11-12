package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.GraphQLClient;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValue;

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
     * Add a global claim to send to client.
     *
     * @param claim
     */
    void addGlobalClaim(ClaimValue claim);

    /**
     * Remove global claim from client.
     *
     * @param claim
     */
    void removeGlobalClaim(ClaimValue claim);
}
