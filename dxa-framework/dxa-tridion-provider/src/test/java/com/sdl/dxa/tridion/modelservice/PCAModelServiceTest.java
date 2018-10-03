package com.sdl.dxa.tridion.modelservice;

import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.webapp.common.api.content.ContentProviderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PCAModelServiceTest {

    @Mock
    private ModelServiceConfiguration configuration;

    @InjectMocks
    private PCAModelService service;

    public PCAModelServiceTest() throws ConfigurationException {
    }

    @Before
    public void init() {

        when(configuration.getPageModelUrl()).thenReturn("/model-service/page");
        when(configuration.getEntityModelUrl()).thenReturn("/model-service/entity");
    }

    @Test
    public void shouldLoadPageModel_AsModel() throws ContentProviderException, ItemNotFoundInModelServiceException {

    }

}
