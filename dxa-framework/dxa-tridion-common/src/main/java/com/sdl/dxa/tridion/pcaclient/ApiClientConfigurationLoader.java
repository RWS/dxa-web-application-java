package com.sdl.dxa.tridion.pcaclient;

import java.util.Properties;

/**
 * Loads and holds public content api client configuration.
 */
public interface ApiClientConfigurationLoader {

    /**
     * Returns client configuration properties.
     *
     * @return
     */
    Properties getConfiguration();

    /**
     * Returns GraphQL service endpoint.
     *
     * @return
     */
    String getServiceUrl();
}
