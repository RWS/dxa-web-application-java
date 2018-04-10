package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelServicePageProviderTest {

    @Mock
    private ModelServiceClient modelServiceClient;

    @Mock
    private ModelServiceConfiguration modelServiceConfiguration;

    @InjectMocks
    private ModelServicePageProvider modelServicePageProvider;

    @Before
    public void init() {
        when(modelServiceConfiguration.getPageModelUrl()).thenReturn("/conf");
        modelServicePageProvider.setContentIsCompressed("false");
        modelServicePageProvider.setContentIsBase64Encoded(false);
    }

    @Test
    public void shouldLoadPageContent_InModelService_ForDD4TRawContent() throws SerializationException, ItemNotFoundException, ItemNotFoundInModelServiceException {
        //given
        String content = "content";
        //noinspection unchecked
        when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyString(), eq("INCLUDE")))
                .thenReturn(content);

        //when
        String page = modelServicePageProvider.getPageContentByURL("/path", 42);

        //then
        verify(modelServiceClient).getForType(eq("/conf?modelType=DD4T"), eq(String.class), eq("tcm"), eq(42), eq("/path"), eq("INCLUDE"));
        assertEquals(content, page);
    }

    @Test
    public void shouldWrapDxa404Exception() throws ItemNotFoundInModelServiceException, SerializationException {
        //given
        ItemNotFoundInModelServiceException exception = new ItemNotFoundInModelServiceException("Msg");
        //noinspection unchecked
        when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyString(), eq("INCLUDE")))
                .thenThrow(exception);

        //when
        try {
            modelServicePageProvider.getPageContentByURL("/path", 42);
        } catch (ItemNotFoundException e) {
            //then
            assertEquals(exception, e.getCause());
        }
    }
}