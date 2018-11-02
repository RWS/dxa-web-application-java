package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.exception.DxaTridionCommonException;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
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

    @Autowired
    private ApiClientProvider apiClientProvider;

    @Override
    protected Function<ResolvingData, Optional<String>> _componentResolver() {
        return resolvingData -> Optional.ofNullable(
                apiClientProvider.getClient().resolveComponentLink(resolveNamespace(resolvingData.getUri()), resolvingData.getPublicationId(), resolvingData.getItemId(), null, null, false));
    }

    private ContentNamespace resolveNamespace(String uri)
    {
        if(uri.startsWith("tcm:"))
            return ContentNamespace.Sites;
        else if(uri.startsWith("ish:"))
            return ContentNamespace.Docs;
        else
            throw new DxaTridionCommonException("Not a valid Tridion CmUri request");
    }

    @Override
    protected Function<ResolvingData, Optional<String>> _pageResolver() {

        return resolvingData -> Optional.ofNullable(
                apiClientProvider.getClient().resolvePageLink(resolveNamespace(resolvingData.getUri()), resolvingData.getPublicationId(), resolvingData.getItemId(), false));
    }

    @Override
    protected Function<ResolvingData, Optional<String>> _binaryResolver() {

        return resolvingData -> Optional.ofNullable(
                apiClientProvider.getClient().resolveBinaryLink(resolveNamespace(resolvingData.getUri()), resolvingData.getPublicationId(), resolvingData.getItemId(), null, false));
    }
}
