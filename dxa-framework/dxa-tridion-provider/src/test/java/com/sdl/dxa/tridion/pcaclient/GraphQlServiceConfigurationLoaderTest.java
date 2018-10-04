package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.discovery.datalayer.model.ContentServiceCapability;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQlServiceConfigurationLoaderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ContentServiceCapability contentServiceCapability;

    private GraphQlServiceConfigurationLoader configurationLoader;

    @Before
    public void setup() throws ConfigurationException {
        configurationLoader = spy(new GraphQlServiceConfigurationLoader());
        doReturn(Optional.of(contentServiceCapability)).when(configurationLoader).getContentServiceCapability();
        when(contentServiceCapability.getUri()).thenReturn("http://localhost:8082/content.svc");
        Properties props = new Properties();
        props.put("key123", "value123");
        doReturn(props).when(configurationLoader).getProps();
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
}