package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.client.configuration.api.ConfigurationHolder;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GraphQlServiceConfigurationLoaderTest {

    public static final String SERVICE_URL = "http://service.url/context/";
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ContentServiceCapability contentServiceCapability;
    @Mock
    private ConfigurationHolder rootConfigurationHolder;
    @Mock
    private ConfigurationHolder configurationHolder;

    private GraphQlServiceConfigurationLoader configurationLoader;

    @BeforeEach
    public void setup() throws ConfigurationException {
        configurationLoader = spy(new GraphQlServiceConfigurationLoader("cd/api/", false));
        lenient().doReturn(Optional.of(contentServiceCapability)).when(configurationLoader).getContentServiceCapability();
        lenient().when(contentServiceCapability.getUri()).thenReturn("http://localhost:8082/content.svc");
        Properties props = new Properties();
        props.put("key123", "value123");
        lenient().doReturn(props).when(configurationLoader).getCommonProperties();
        lenient().doReturn(rootConfigurationHolder).when(configurationLoader).getRootConfigHolder();
        lenient().when(rootConfigurationHolder.hasConfiguration("/ContentService")).thenReturn(true);
        lenient().when(rootConfigurationHolder.getConfiguration("/ContentService")).thenReturn(configurationHolder);
    }

    @Test
    public void getConfiguration() {
        Properties properties = configurationLoader.getConfiguration();

        assertEquals("http://localhost:8082/cd/api", properties.getProperty("ServiceUri"));
        assertEquals("value123", properties.getProperty("key123"));
    }

    @Test
    public void getServiceUrl() {
        String serviceUrl = configurationLoader.getServiceUrl();

        assertEquals("http://localhost:8082/cd/api", serviceUrl);
    }

    @Test
    public void initializeNotFromRootConfiguration() throws Exception {
        Assertions.assertThrows(ApiClientConfigurationException.class, () -> {
            doReturn(Optional.empty()).when(configurationLoader).getContentServiceCapability();
            when(rootConfigurationHolder.hasConfiguration("/ContentService")).thenReturn(false);

            configurationLoader.initialize();
        });
    }

    @Test
    public void initializeFromRootConfiguration() throws Exception {
        doReturn(Optional.empty()).when(configurationLoader).getContentServiceCapability();
        when(configurationHolder.getValue("ServiceUri")).thenReturn(SERVICE_URL + "content.svc");
        assertFalse(configurationLoader.isInitialized());

        configurationLoader.initialize();

        assertTrue(configurationLoader.isInitialized());
        assertEquals(SERVICE_URL + "cd/api", configurationLoader.getServiceUrl());
    }
}