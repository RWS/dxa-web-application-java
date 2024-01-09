package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceClientConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.web.model.componentpresentation.ComponentPresentationImpl;
import com.tridion.dcp.ComponentPresentation;
import org.apache.commons.io.IOUtils;
import org.dd4t.core.exceptions.ItemNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ModelServiceComponentPresentationProviderTest {

    @Mock
    private ModelServiceClient modelServiceClient;

    @Mock
    private ModelServiceClientConfiguration modelServiceClientConfiguration;

    @InjectMocks
    private ModelServiceComponentPresentationProvider componentPresentationProvider;

    @BeforeEach
    public void init() throws ItemNotFoundInModelServiceException, IOException {
        lenient().when(modelServiceClientConfiguration.getEntityModelUrl()).thenReturn("/conf");
        componentPresentationProvider.setContentIsCompressed("false");
        componentPresentationProvider.setContentIsBase64Encoded(false);

        //noinspection unchecked
        lenient().when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(IOUtils.toString(new ClassPathResource("dcp.json").getInputStream(), "UTF-8"));
    }

    @Test
    public void shouldDeserializeIntoComponentPresentation() throws ItemNotFoundException {
        //given
        ComponentPresentationImpl expected = new ComponentPresentationImpl(1, 2, 3, 4, "content", "file", true);

        //when
        ComponentPresentation componentPresentation = componentPresentationProvider.getComponentPresentation(1, 2, 3);

        //then
        assertThat(expected, samePropertyValuesAs(componentPresentation));
    }
}