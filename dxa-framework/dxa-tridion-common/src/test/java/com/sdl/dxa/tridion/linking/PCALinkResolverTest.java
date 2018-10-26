package com.sdl.dxa.tridion.linking;

import com.sdl.dxa.tridion.pcaclient.DefaultPCAClientProvider;
import com.sdl.dxa.tridion.pcaclient.PCAClientConfigurationLoader;
import com.sdl.dxa.tridion.pcaclient.PCAClientProvider;
import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PCALinkResolverTest {

    @Mock
    private PCALinkResolver pcaLinkResolver;

    @Mock
    private PCAClientConfigurationLoader pcaClientConfigurationLoader;

    @Mock
    private PCAClientProvider pcaClientProvider;

    @Mock
    private Authentication authentication;

    @Before
    public void setup(){
        when(pcaClientConfigurationLoader.getServiceUrl()).thenReturn("http://10.100.93.148:8081/cd/api");
        when(pcaClientConfigurationLoader.getConfiguration()).thenReturn(new Properties());
        pcaClientProvider = new DefaultPCAClientProvider(pcaClientConfigurationLoader, authentication);
    }

    @Test
    public void resolveLink() {
        Assert.hasText("http://localhost:8882/", pcaLinkResolver.resolveLink("http://localhost:8882/","5",true));
    }

    @Test
    public void _componentResolver() {
        String result = pcaClientProvider.getClient().resolveComponentLink(ContentNamespace.Sites, 5, 301, null, null, false);
        assertEquals("http://dxatest.ams.dev:8098/about/office-location.html", result);
    }

    @Test
    public void _pageResolver() {
       String result = pcaClientProvider.getClient().resolvePageLink(ContentNamespace.Sites, 5, 320, false);
       assertEquals("http://dxatest.ams.dev:8098/articles/news/news3.html", result);
    }

    @Test
    public void _binaryResolver() {
        String result = pcaClientProvider.getClient().resolveBinaryLink(ContentNamespace.Sites, 5, 289, null, false);
        assertEquals("http://dxatest.ams.dev:8098/media/wine-cellar_tcm5-289.jpg", result);
    }
}