package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.tridion.pcaclient.DefaultPCAClientProvider;
import com.sdl.dxa.tridion.pcaclient.PCAClientConfigurationLoader;
import com.sdl.dxa.tridion.pcaclient.PCAClientProvider;
import com.sdl.web.pca.client.DefaultPublicContentApi;
import com.sdl.web.pca.client.PublicContentApi;
import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PCALinkResolverTest {

    @Mock
    private PCAClientProvider pcaClientProvider;

    @Mock
    private PublicContentApi publicContentApi;

    @InjectMocks
    private PCALinkResolver linkResovler = new PCALinkResolver();

    @Before
    public void setup(){
        when(pcaClientProvider.getClient()).thenReturn(publicContentApi);

        when(pcaClientProvider.getClient().resolvePageLink(ContentNamespace.Sites,2,3,false)).thenReturn("/index.html");

        when(pcaClientProvider.getClient().resolveComponentLink(ContentNamespace.Sites,2,3,null,null,false)).thenReturn("/resolved-component-2");

        when(pcaClientProvider.getClient().resolveBinaryLink(ContentNamespace.Sites,2,3,null,false)).thenReturn("/media/baloon.png");

    }

    @Test
    public void resolveLink() {
        String pageLinkresult = linkResovler.resolveLink("tcm:2-3-64", "2", false);
        assertEquals(pageLinkresult, "/index.html");

        String componentLinkresult = linkResovler.resolveLink("tcm:2-3", "2", false);
        assertEquals(componentLinkresult, "/resolved-component-2");

        String binaryLinkresult = linkResovler.resolveLink("tcm:2-3", "2", true);
        assertEquals(binaryLinkresult, "/media/baloon.png");
    }
}