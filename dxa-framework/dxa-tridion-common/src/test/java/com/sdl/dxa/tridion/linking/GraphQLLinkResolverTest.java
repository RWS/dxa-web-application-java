package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLLinkResolverTest {

    @Mock
    private ApiClientProvider apiClientProvider;

    @Mock
    private ApiClient publicContentApi;

    @InjectMocks
    private GraphQLLinkResolver linkResolver = new GraphQLLinkResolver();

    @Before
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