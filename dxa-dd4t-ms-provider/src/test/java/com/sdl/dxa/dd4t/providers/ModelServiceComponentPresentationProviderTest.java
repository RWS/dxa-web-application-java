package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.web.model.componentpresentation.ComponentPresentationImpl;
import com.tridion.dcp.ComponentPresentation;
import org.apache.commons.io.IOUtils;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelServiceComponentPresentationProviderTest {

    @Mock
    private ModelServiceClient modelServiceClient;

    @Mock
    private ModelServiceConfiguration modelServiceConfiguration;

    @InjectMocks
    private ModelServiceComponentPresentationProvider componentPresentationProvider;

    @Before
    public void init() throws ItemNotFoundInModelServiceException, IOException {
        when(modelServiceConfiguration.getEntityModelUrl()).thenReturn("/conf");
        componentPresentationProvider.setContentIsCompressed("false");
        componentPresentationProvider.setContentIsBase64Encoded(false);

        //noinspection unchecked
        when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(IOUtils.toString(new ClassPathResource("dcp.json").getInputStream(), "UTF-8"));
    }

    @Test
    public void shouldDeserializeIntoComponentPresentation() throws ItemNotFoundException {
        //given
        ComponentPresentationImpl expected = new ComponentPresentationImpl(1, 2, 3, 4, "content", "file", true);

        //when
        ComponentPresentation componentPresentation = componentPresentationProvider.getComponentPresentation(1, 2, 3);

        //then
        assertThat(expected, new ReflectionEquals(componentPresentation));
    }
}