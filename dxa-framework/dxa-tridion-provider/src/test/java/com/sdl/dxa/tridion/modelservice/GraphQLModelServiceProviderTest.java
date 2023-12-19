package com.sdl.dxa.tridion.modelservice;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.graphql.GraphQLProvider;
import com.sdl.web.pca.client.contentmodel.enums.ContentType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraphQLModelServiceProviderTest {

    @Mock
    private GraphQLProvider graphQLProvider;

    @InjectMocks
    private GraphQLModelServiceProvider modelServiceProvider;

    @Test
    public void loadPageModel() throws Exception {
        //Given
        PageModelData pageModelData = new PageModelData();
        PageRequestDto request = PageRequestDto.builder(5, "/index").build();

        //When
        when(graphQLProvider.loadPage(any(), any(), any())).thenReturn(pageModelData);
        PageModelData result = modelServiceProvider.loadPageModel(request);

        //Then
        assertEquals(pageModelData, result);
        verify(graphQLProvider).loadPage(eq(PageModelData.class), eq(request), eq(ContentType.MODEL));
    }

    @Test
    public void loadPageContent() throws Exception {
        //Given
        PageRequestDto request = PageRequestDto.builder(5, "/index").build();
        String expected = "result";

        //When
        when(graphQLProvider.loadPage(any(), any(), any())).thenReturn(expected);
        String result = modelServiceProvider.loadPageContent(request);

        //Then
        assertEquals(expected, result);
        verify(graphQLProvider).loadPage(eq(String.class), eq(request), eq(ContentType.RAW));
    }

    @Test
    public void loadEntity() throws Exception {
        //Given
        EntityRequestDto request = EntityRequestDto.builder(5, "333-444").build();

        //When
        EntityModelData response = new EntityModelData();
        when(graphQLProvider.getEntityModelData(any())).thenReturn(response);

        //Then
        EntityModelData result = modelServiceProvider.loadEntity(request);
        assertEquals(response, result);
    }
}
