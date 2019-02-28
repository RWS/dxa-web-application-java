package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.exception.DxaTridionCommonException;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.webapp.tridion.linking.AbstractLinkResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@Profile("!cil.providers.active")
public class GraphQLLinkResolver extends AbstractLinkResolver {

    private ApiClient apiClient;

    public GraphQLLinkResolver() {
    }

    @Autowired
    public GraphQLLinkResolver(ApiClientProvider apiClientProvider) {
        this.apiClient = apiClientProvider.getClient();
    }

    @Override
    protected Function<ResolvingData, Optional<String>> componentResolver() {
        return resolvingData -> Optional.ofNullable(
                apiClient.resolveComponentLink(resolveNamespace(resolvingData.getUri()),
                        resolvingData.getPublicationId(), resolvingData.getItemId(), null,
                        null, false));
    }

    private ContentNamespace resolveNamespace(String uri) {
        if (uri.startsWith("tcm:"))
            return ContentNamespace.Sites;
        else if (uri.startsWith("ish:"))
            return ContentNamespace.Docs;
        else
            throw new DxaTridionCommonException("Not a valid Tridion CmUri request uri " + uri);
    }

    @Override
    protected Function<ResolvingData, Optional<String>> pageResolver() {
        return resolvingData -> Optional.ofNullable(
                apiClient.resolvePageLink(resolveNamespace(resolvingData.getUri()), resolvingData.getPublicationId(), resolvingData.getItemId(), true));
    }

    @Override
    protected Function<ResolvingData, Optional<String>> binaryResolver() {
        return resolvingData -> Optional.ofNullable(
                apiClient.resolveBinaryLink(resolveNamespace(resolvingData.getUri()), resolvingData.getPublicationId(), resolvingData.getItemId(), null, true));
    }
}
