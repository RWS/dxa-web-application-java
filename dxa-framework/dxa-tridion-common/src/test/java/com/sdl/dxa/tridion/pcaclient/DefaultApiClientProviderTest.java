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
public class DefaultApiClientProviderTest {

    @Mock
    private ApiClientConfigurationLoader configurationLoader;
    @Mock
    private Authentication auth;

    private DefaultApiClientProvider apiClientProvider;

    @Before
    public void setup() {
        when(configurationLoader.getServiceUrl()).thenReturn("http://localhost:8082/cd/api");
        when(configurationLoader.getConfiguration()).thenReturn(new Properties());
        apiClientProvider = new DefaultApiClientProvider(configurationLoader, auth);
    }

    @Test
    public void getClient() {
        assertNotNull(apiClientProvider.getClient());
    }

    @Test
    public void getGraphQLClient() {
        assertNotNull(apiClientProvider.getGraphQLClient());
    }

}