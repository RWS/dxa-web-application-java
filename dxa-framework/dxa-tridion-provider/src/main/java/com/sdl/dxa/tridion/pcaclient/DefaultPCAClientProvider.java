package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.DefaultGraphQLClient;
import com.sdl.web.pca.client.DefaultPublicContentApi;
import com.sdl.web.pca.client.GraphQLClient;
import com.sdl.web.pca.client.PublicContentApi;
import com.sdl.web.pca.client.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PCAClientProvider")
public class DefaultPCAClientProvider implements PCAClientProvider{

    private PublicContentApi publicContentApi;

    private GraphQLClient graphQLClient;

    @Autowired
    public DefaultPCAClientProvider(PCAClientConfigurationLoader configurationLoader, Authentication auth) {
        this.graphQLClient = new DefaultGraphQLClient(configurationLoader.getServiceUrl(), null, auth);
        this.publicContentApi = new DefaultPublicContentApi(graphQLClient,
                Integer.valueOf(configurationLoader.getConfiguration()
                        .getOrDefault("ConnectionTimeout", 0).toString()));
    }

    @Override
    public PublicContentApi getClient() {
        return publicContentApi;
    }

    @Override
    public GraphQLClient getGraphQLClient() {
        return graphQLClient;
    }
}
