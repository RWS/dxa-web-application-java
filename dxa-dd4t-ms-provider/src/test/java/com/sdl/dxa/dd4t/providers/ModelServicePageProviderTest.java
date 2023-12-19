package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceClientConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModelServicePageProviderTest {

    @Mock
    private ModelServiceClient modelServiceClient;

    @Mock
    private ModelServiceClientConfiguration modelServiceClientConfiguration;

    @InjectMocks
    private ModelServicePageProvider modelServicePageProvider;

    @BeforeEach
    public void init() {
        lenient().when(modelServiceClientConfiguration.getPageModelUrl()).thenReturn("/conf");
        modelServicePageProvider.setContentIsCompressed("false");
        modelServicePageProvider.setContentIsBase64Encoded(false);
    }

    @Test
    public void shouldLoadPageContent_InModelService_ForDD4TRawContent() throws SerializationException, ItemNotFoundException, ItemNotFoundInModelServiceException {
        //given
        String content = "content";
        //noinspection unchecked
        lenient().when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyString(), eq("INCLUDE")))
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
        lenient().when(modelServiceClient.getForType(anyString(), any(Class.class), anyString(), anyInt(), anyString(), eq("INCLUDE")))
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