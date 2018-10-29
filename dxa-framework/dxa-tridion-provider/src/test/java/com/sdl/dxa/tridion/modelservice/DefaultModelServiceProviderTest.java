package com.sdl.dxa.tridion.modelservice;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultModelServiceProviderTest {

    @Mock
    private ModelServiceConfiguration configuration;

    @Mock
    private Localization localization;

    @Mock
    private ModelServiceClient modelServiceClient;

    @InjectMocks
    private DefaultModelServiceProvider service;

    public DefaultModelServiceProviderTest() throws ConfigurationException {
    }

    @Before
    public void init() {

        when(configuration.getPageModelUrl()).thenReturn("/model-service/page");
        when(configuration.getEntityModelUrl()).thenReturn("/model-service/entity");
    }

    @Test
    public void shouldLoadPageModel_AsModel() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given 
        PageRequestDto pageRequest = PageRequestDto.builder(42, "/path")
                .uriType("tcm").includePages(PageRequestDto.PageInclusion.INCLUDE)
                .build();
        PageModelData modelData = new PageModelData();
        modelData.setId("123");
        when(modelServiceClient.getForType(eq("/model-service/page"), eq(PageModelData.class), eq("tcm"), eq(42),
                eq("path"), eq(PageRequestDto.PageInclusion.INCLUDE))).thenReturn(modelData);

        //when
        PageModelData pageModelData = service.loadPageModel(pageRequest);

        //then
        assertEquals(pageModelData, modelData);
    }

    @Test
    public void shouldLoadPageModel_AsString() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        PageRequestDto pageRequest = PageRequestDto.builder(666, "/path")
                .uriType("tcm").includePages(PageRequestDto.PageInclusion.INCLUDE)
                .build();
        when(modelServiceClient.getForType(eq("/model-service/page?raw=true"), eq(String.class), eq("tcm"), eq(666),
                eq("path"), eq(PageRequestDto.PageInclusion.INCLUDE))).thenReturn("hello");

        //when
        String pageModelData = service.loadPageContent(pageRequest);

        //then
        assertEquals(pageModelData, "hello");
    }

    @Test
    public void shouldLoadEntityModel_AsModel() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        EntityRequestDto entityRequestDto = EntityRequestDto.builder(42, 1, 2)
                .uriType("tcm")
                .build();
        EntityModelData actual = new EntityModelData();
        actual.setId("123");
        when(modelServiceClient.getForType(eq("/model-service/entity"), eq(EntityModelData.class), eq("tcm"), eq(42),
                eq(1), eq(2))).thenReturn(actual);

        //when
        EntityModelData entity = service.loadEntity(entityRequestDto);

        //then
        assertEquals(entity, actual);
    }

    @Test
    public void shouldLoadEntityModel_AsModel_FromEntityId() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        EntityModelData actual = new EntityModelData();
        actual.setId("123");
        when(modelServiceClient.getForType(eq("/model-service/entity"), eq(EntityModelData.class), eq("tcm"), eq(666),
                eq(1), eq(2))).thenReturn(actual);

        //when
        EntityModelData entity = service.loadEntity("666", "1-2");

        //then
        assertEquals(entity, actual);
    }
}