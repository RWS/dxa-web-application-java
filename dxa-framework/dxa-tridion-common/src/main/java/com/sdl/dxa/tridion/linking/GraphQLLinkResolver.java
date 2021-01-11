package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.exception.DxaTridionCommonException;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.webapp.tridion.linking.AbstractLinkResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!cil.providers.active")
public class GraphQLLinkResolver extends AbstractLinkResolver {

    private ApiClientProvider apiClientProvider;

    public GraphQLLinkResolver() {
    }

    @Autowired
    public GraphQLLinkResolver(ApiClientProvider apiClientProvider) {
        this.apiClientProvider = apiClientProvider;
    }

    @Override
    protected String resolveComponent(ResolvingData resolvingData) {
        ApiClient client = apiClientProvider.getClient();
        ContentNamespace namespace = resolveNamespace(resolvingData.getUri());
        int pubId = resolvingData.getPublicationId();
        int itemId = resolvingData.getItemId();
        int pageId = resolvingData.getPageId();
        String componentLink = client.resolveComponentLink(namespace, pubId, itemId, pageId, null, true);
        if ("null".equals(componentLink)) {
            return null;
        }
        return componentLink;
    }

    @Override
    protected String resolvePage(ResolvingData resolvingData) {
        ApiClient client = apiClientProvider.getClient();
        ContentNamespace namespace = resolveNamespace(resolvingData.getUri());
        int pubId = resolvingData.getPublicationId();
        int itemId = resolvingData.getItemId();
        String pageLink = client.resolvePageLink(namespace, pubId, itemId, true);
        if ("null".equals(pageLink)) {
            return null;
        }
        return pageLink;
    }

    @Override
    protected String resolveBinary(ResolvingData resolvingData) {
        ApiClient client = apiClientProvider.getClient();
        ContentNamespace namespace = resolveNamespace(resolvingData.getUri());
        int pubId = resolvingData.getPublicationId();
        int itemId = resolvingData.getItemId();
        String binaryLink = client.resolveBinaryLink(namespace, pubId, itemId, null, true);
        if ("null".equals(binaryLink)) {
            return null;
        }
        return binaryLink;
    }

    private ContentNamespace resolveNamespace(String uri) {
        if (uri.startsWith("tcm:"))
            return ContentNamespace.Sites;
        else if (uri.startsWith("ish:"))
            return ContentNamespace.Docs;
        else
            throw new DxaTridionCommonException("Not a valid Tridion CmUri request uri " + uri);
    }
}
