package com.sdl.dxa.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestDynamicNavigationModelProviderTest {

    @Mock
    private ModelServiceConfiguration configuration;

    @Mock
    private ModelServiceClient serviceClient;

    @InjectMocks
    private RestDynamicNavigationModelProvider provider;

    @Before
    public void init() {
        when(configuration.getNavigationApiUrl()).thenReturn("/nav");
        when(configuration.getOnDemandApiUrl()).thenReturn("/od");
    }

    @Test
    public void shouldCall_ModelService_ForNavigationModel() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        when(serviceClient.getForType(eq("/nav"), eq(TaxonomyNodeModelData.class), eq(42))).thenReturn(new TaxonomyNodeModelData().setKey("123"));

        //when
        Optional<TaxonomyNodeModelData> data = provider.getNavigationModel(SitemapRequestDto.builder(42).build());

        //then
        assertTrue(data.isPresent());
        assertEquals("123", data.get().getKey());
    }

    @Test
    public void shouldReturnOptionalEmpty_When404Exception_ForNavigationModel() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        when(serviceClient.getForType(eq("/nav"), eq(TaxonomyNodeModelData.class), eq(42))).thenThrow(new ItemNotFoundInModelServiceException());

        //when
        Optional<TaxonomyNodeModelData> data = provider.getNavigationModel(SitemapRequestDto.builder(42).build());

        //then
        assertFalse(data.isPresent());
    }

    @Test
    public void shouldCall_ModelService_ForNavigationSubtree() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        NavigationFilter navigationFilter = new NavigationFilter().setWithAncestors(true).setDescendantLevels(666);
        SitemapItemModelData[] value = {new TaxonomyNodeModelData(), new SitemapItemModelData()};
        when(serviceClient.getForType(eq("/od"), eq(SitemapItemModelData[].class), eq(42), eq("t1"), eq(navigationFilter.isWithAncestors()), eq(navigationFilter.getDescendantLevels())))
                .thenReturn(value);
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .navigationFilter(navigationFilter)
                .build();

        //when
        Optional<Collection<SitemapItemModelData>> data = provider.getNavigationSubtree(requestDto);

        //then
        assertTrue(data.isPresent());
        assertEquals(2, data.get().size());
    }

    @Test
    public void shouldReturnOptionalEmpty_When404Exception_ForNavigationSubtree() throws ContentProviderException, ItemNotFoundInModelServiceException {
        //given
        when(serviceClient.getForType(eq("/od"), eq(SitemapItemModelData[].class), eq(42), eq("t1"), any(), any()))
                .thenThrow(new ItemNotFoundInModelServiceException());
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .build();

        //when
        Optional<Collection<SitemapItemModelData>> data = provider.getNavigationSubtree(requestDto);

        //then
        assertFalse(data.isPresent());
    }

    @Test(expected = BadRequestException.class)
    public void shouldProceed_WithException_WhenRequestIsBad_NavModel() throws DxaItemNotFoundException, ItemNotFoundInModelServiceException {
        //given
        when(serviceClient.getForType(eq("/nav"), eq(TaxonomyNodeModelData.class), eq(42))).thenThrow(new BadRequestException());

        //when
        provider.getNavigationModel(SitemapRequestDto.builder(42).build());

        //then
    }

    @Test(expected = BadRequestException.class)
    public void shouldProceed_WithException_WhenRequestIsBad_Subtree() throws DxaItemNotFoundException, ItemNotFoundInModelServiceException {
        //given
        when(serviceClient.getForType(eq("/od"), eq(SitemapItemModelData[].class), eq(42), eq("t1"), any(), any()))
                .thenThrow(new BadRequestException());

        //when
        provider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("t1").build());

        //then
    }
}