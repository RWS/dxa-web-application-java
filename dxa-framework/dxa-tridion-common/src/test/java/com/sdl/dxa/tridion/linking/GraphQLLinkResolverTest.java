package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraphQLLinkResolverTest {

    @Mock
    private ApiClientProvider apiClientProvider;

    @Mock
    private ApiClient publicContentApi;

    @InjectMocks
    private GraphQLLinkResolver linkResolver = new GraphQLLinkResolver();

    @BeforeEach
    public void setup(){
        when(apiClientProvider.getClient()).thenReturn(publicContentApi);
    }

    @Test
    public void resolvePageLink() {

        when(apiClientProvider.getClient().resolvePageLink(ContentNamespace.Sites,2,3,true)).thenReturn("/index.html");

        String pageLinkresult = linkResolver.resolveLink("tcm:2-3-64", "2", false);
        assertEquals("/index.html", pageLinkresult);
    }

    @Test
    public void resolveComponentLink() {
        when(apiClientProvider.getClient().resolveComponentLink(ContentNamespace.Sites,2,3, -1,null,true)).thenReturn("/resolved-component-2");


        String componentLinkresult = linkResolver.resolveLink("tcm:2-3", "2", false);
        assertEquals("/resolved-component-2", componentLinkresult);
    }

    @Test
    public void resolveBinaryLink() {
        when(apiClientProvider.getClient().resolveBinaryLink(ContentNamespace.Sites,2,3,null,true)).thenReturn("/media/baloon.png");

        String binaryLinkresult = linkResolver.resolveLink("tcm:2-3", "2", true);
        assertEquals("/media/baloon.png", binaryLinkresult);
    }
}