package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.auth.Authentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPCAClientProviderTest {

    @Mock
    private PCAClientConfigurationLoader configurationLoader;
    @Mock
    private Authentication auth;

    private DefaultPCAClientProvider pcaClientProvider;

    @Before
    public void setup() {
        when(configurationLoader.getServiceUrl()).thenReturn("http://localhost:8082/cd/api");
        when(configurationLoader.getConfiguration()).thenReturn(new Properties());
        pcaClientProvider = new DefaultPCAClientProvider(configurationLoader, auth);
    }

    @Test
    public void getClient() {
        assertNotNull(pcaClientProvider.getClient());
    }

    @Test
    public void getGraphQLClient() {
        assertNotNull(pcaClientProvider.getGraphQLClient());
    }

}