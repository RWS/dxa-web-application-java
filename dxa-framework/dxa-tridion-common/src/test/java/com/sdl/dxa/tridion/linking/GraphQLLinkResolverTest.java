package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.DefaultApiClient;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLLinkResolverTest {

    @Mock
    private ApiClientProvider apiClientProvider;

    @Mock
    private ApiClient publicContentApi;

    @InjectMocks
    private GraphQLLinkResolver linkResovler = new GraphQLLinkResolver();

    @Before
    public void setup(){
        when(apiClientProvider.getClient()).thenReturn(publicContentApi);
    }

    @Test
    public void resolvePageLink() {

        when(apiClientProvider.getClient().resolvePageLink(ContentNamespace.Sites,2,3,false)).thenReturn("/index.html");

        String pageLinkresult = linkResovler.resolveLink("tcm:2-3-64", "2", false);
        assertEquals(pageLinkresult, "/index.html");
    }

    @Test
    public void resolveComponentLink() {
        when(apiClientProvider.getClient().resolveComponentLink(ContentNamespace.Sites,2,3,null,null,false)).thenReturn("/resolved-component-2");


        String componentLinkresult = linkResovler.resolveLink("tcm:2-3", "2", false);
        assertEquals(componentLinkresult, "/resolved-component-2");
    }

    @Test
    public void resolveBinaryLink() {
        when(apiClientProvider.getClient().resolveBinaryLink(ContentNamespace.Sites,2,3,null,false)).thenReturn("/media/baloon.png");

        String binaryLinkresult = linkResovler.resolveLink("tcm:2-3", "2", true);
        assertEquals(binaryLinkresult, "/media/baloon.png");
    }
}