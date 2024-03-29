package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class DefaultApiClientProviderTest {

    @Autowired
    private GraphQlServiceConfigurationLoader configurationLoader;
    @Mock
    private Authentication auth;

    private DefaultApiClientProvider apiClientProvider;

    @BeforeEach
    public void setup() throws ConfigurationException {
        MockitoAnnotations.initMocks(this);
        lenient().when(configurationLoader.getServiceUrl()).thenReturn("http://localhost:8082/cd/api");
        lenient().when(configurationLoader.getConfiguration()).thenReturn(new Properties());
        lenient().when(configurationLoader.getCacheConfiguration()).thenReturn(new Properties());
        apiClientProvider = new DefaultApiClientProvider(configurationLoader, auth);
    }

    @Test
    public void getClient() {
        assertNotNull(apiClientProvider.getClient());
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {
        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }
        @Bean
        @Primary
        public GraphQlServiceConfigurationLoader configurationLoader() {
            return Mockito.mock(GraphQlServiceConfigurationLoader.class);
        }
    }
}
