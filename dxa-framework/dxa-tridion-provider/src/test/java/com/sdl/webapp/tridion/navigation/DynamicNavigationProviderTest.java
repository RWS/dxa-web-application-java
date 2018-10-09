package com.sdl.webapp.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.exception.DxaTridionCommonException;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.sdl.webapp.tridion.fields.exceptions.TaxonomyNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DynamicNavigationProviderTest {

    @Mock
    private NavigationModelProvider navigationModelProvider;

    @Mock
    private OnDemandNavigationModelProvider onDemandNavigationModelProvider;

    @Mock
    private StaticNavigationProvider staticNavigationProvider;

    @Mock
    private Localization localization;

    @Mock
    private LinkResolver linkResolver;

    @InjectMocks
    private DynamicNavigationProvider dynamicNavigationProvider;

    private TaxonomyNodeModelData navigationModel = new TaxonomyNodeModelData();

    @Before
    public void init() throws NavigationProviderException {
        when(localization.getId()).thenReturn("42");

        when(linkResolver.resolveLink(eq("/t1k1"), anyString())).thenReturn("resolved");

        navigationModel.setId("t1")
                .addItem(new SitemapItemModelData().setId("t1-p0").setVisible(true).setUrl("/index").setTitle("000 Index"))
                .addItem(new TaxonomyNodeModelData().setId("t1-k1").setVisible(true).setUrl("/t1k1").setTitle("001 t1k1")
                        .addItem(new SitemapItemModelData().setId("t1-p21").setVisible(true).setUrl("/t1p21").setTitle("010 t1p21"))
                        .addItem(new SitemapItemModelData().setId("t1-p22").setVisible(true).setUrl("/t1p22").setTitle("011 t1p22"))
                        .addItem(new SitemapItemModelData().setId("t1-p23").setVisible(false).setUrl("/t1p23").setTitle("012 t1p23")))
                .addItem(new TaxonomyNodeModelData().setId("t1-k2").setVisible(true).setUrl("/t1k2").setTitle("002 t1k2"))
                .addItem(new SitemapItemModelData().setId("t1-p1").setVisible(true).setUrl("/t1p1").setTitle("003 t1p1"))
                .addItem(new TaxonomyNodeModelData().setId("t1-k3").setVisible(false).setUrl("/t1k3").setTitle("004 t1k3")
                        .addItem(new SitemapItemModelData().setId("t1-p24").setVisible(true).setUrl("/t1p24").setTitle("010 t1p24")))
                .addItem(new SitemapItemModelData().setId("t1-p3").setVisible(true).setUrl("").setTitle("005 t1p3"))
                .addItem(new SitemapItemModelData().setId("t1-p4").setVisible(true).setUrl(null).setTitle("006 t1p4"));

        SitemapItem staticModel = new SitemapItem();
        staticModel.setTitle("Static");
        when(staticNavigationProvider.getNavigationModel(eq(localization))).thenReturn(staticModel);

        NavigationLinks staticNavigationLinks = new NavigationLinks();
        staticNavigationLinks.setId("Static");
        when(staticNavigationProvider.getTopNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);
        when(staticNavigationProvider.getContextNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);
        when(staticNavigationProvider.getBreadcrumbNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);
    }

    //region Dynamic Navigation
    @Test
    public void shouldConvertR2Model_ToEntityModel() throws NavigationProviderException {
        //given
        SitemapItemModelData model = new TaxonomyNodeModelData().setId("t1");
        doReturn(Optional.of(model)).when(navigationModelProvider).getNavigationModel(argThat(getDefaultMatcher()));

        //when
        SitemapItem navigationModel = dynamicNavigationProvider.getNavigationModel(localization);

        //then
        assertEquals(model.getId(), navigationModel.getId());
        assertEquals(model.getUrl(), navigationModel.getUrl());
        assertEquals(model.isVisible(), navigationModel.isVisible());
    }

    @Test
    public void shouldFallback_IfModelProviderReturnedEmptyOptional() throws NavigationProviderException {
        //given
        doReturn(Optional.empty()).when(navigationModelProvider).getNavigationModel(argThat(getDefaultMatcher()));

        //when
        SitemapItem navigationModel = dynamicNavigationProvider.getNavigationModel(localization);
        NavigationLinks top = dynamicNavigationProvider.getTopNavigationLinks("path", localization);
        NavigationLinks contextNavigationLinks = dynamicNavigationProvider.getContextNavigationLinks("path", localization);
        NavigationLinks bread = dynamicNavigationProvider.getBreadcrumbNavigationLinks("path", localization);

        //then
        assertEquals("Static", navigationModel.getTitle());
        assertEquals("Static", top.getId());
        assertEquals("Static", contextNavigationLinks.getId());
        assertEquals("Static", bread.getId());
    }

    @Test
    public void shouldProcessNavigationLinks_ForTopNavigation_FilteringHidden() throws NavigationProviderException {
        //given
        when(navigationModelProvider.getNavigationModel(argThat(getDefaultMatcher()))).thenReturn(Optional.of(navigationModel));

        //when
        NavigationLinks links = dynamicNavigationProvider.getTopNavigationLinks(null, localization);

        //then
        assertEquals(4, links.getItems().size());
        assertEquals("t1-p0", links.getItems().get(0).getId());
        assertEquals("t1-k1", links.getItems().get(1).getId());
        assertEquals("resolved", links.getItems().get(1).getUrl());
        assertEquals("t1-k2", links.getItems().get(2).getId());
        assertEquals("t1-p1", links.getItems().get(3).getId());
    }

    @Test
    public void shouldProcessNavigationLinks_ForContextNavigation_FilteringHidden() throws NavigationProviderException {
        //given
        when(navigationModelProvider.getNavigationModel(argThat(getDefaultMatcher()))).thenReturn(Optional.of(navigationModel));

        //when
        NavigationLinks links = dynamicNavigationProvider.getContextNavigationLinks("/t1p22", localization);
        NavigationLinks linksKeyword = dynamicNavigationProvider.getContextNavigationLinks("/t1k1", localization);

        //then
        assertEquals(2, links.getItems().size());
        assertEquals("t1-p21", links.getItems().get(0).getId());
        assertEquals("t1-p22", links.getItems().get(1).getId());

        assertEquals(2, linksKeyword.getItems().size());
        assertEquals("t1-p21", linksKeyword.getItems().get(0).getId());
        assertEquals("t1-p22", linksKeyword.getItems().get(1).getId());
    }

    @Test
    public void shouldReturnEmptyList_ForCurrentContext_IfNothingFound() throws NavigationProviderException {
        //given
        when(navigationModelProvider.getNavigationModel(argThat(getDefaultMatcher()))).thenReturn(Optional.of(navigationModel));

        //when
        NavigationLinks links = dynamicNavigationProvider.getContextNavigationLinks("/unknown", localization);

        //then
        assertEquals(0, links.getItems().size());
    }


    @Test
    public void shouldCollectBreadcrumbs_EvenForHiddenItems() throws NavigationProviderException {
        //given
        when(navigationModelProvider.getNavigationModel(argThat(getDefaultMatcher()))).thenReturn(Optional.of(navigationModel));

        //when
        NavigationLinks links = dynamicNavigationProvider.getBreadcrumbNavigationLinks("/t1p24", localization);
        // TSI-1958
        NavigationLinks linksHidden = dynamicNavigationProvider.getBreadcrumbNavigationLinks("/t1p23", localization);
        // TSI-1956
        NavigationLinks linksSlash = dynamicNavigationProvider.getBreadcrumbNavigationLinks("/t1p24/", localization);

        //then
        assertEquals(linksSlash, links);
        assertEquals(2, links.getItems().size());
        assertEquals("t1-k3", links.getItems().get(0).getId());
        assertEquals("t1-p24", links.getItems().get(1).getId());

        assertEquals(2, linksHidden.getItems().size());
        // todo assert some magic about home paths
    }

    //todo test current context when keywords have index pages


    @Test
    public void shouldFindHome_AsSiblingPage() throws NavigationProviderException {
        //given
        when(localization.getPath()).thenReturn("/t1p1");
        when(navigationModelProvider.getNavigationModel(argThat(getDefaultMatcher()))).thenReturn(Optional.of(navigationModel));

        //when
        NavigationLinks links = dynamicNavigationProvider.getBreadcrumbNavigationLinks("/t1k3", localization);

        //then
        assertEquals(2, links.getItems().size());
        assertEquals("t1-p1", links.getItems().get(0).getId());
        assertEquals("t1-k3", links.getItems().get(1).getId());
    }
    //endregion

    //region On-demand API

    @Test(expected = TaxonomyNotFoundException.class)
    public void shouldThrowException_IfNothingFound() throws DxaItemNotFoundException {
        //given
        when(onDemandNavigationModelProvider.getNavigationSubtree(any())).thenReturn(Optional.empty());

        //when
        dynamicNavigationProvider.getNavigationSubtree("t1", NavigationFilter.DEFAULT, localization);
    }

    @Test(expected = BadRequestException.class)
    public void shouldProceedWithException_IfBadRequest() throws DxaItemNotFoundException {
        //given
        when(onDemandNavigationModelProvider.getNavigationSubtree(any())).thenThrow(new BadRequestException());

        //when
        dynamicNavigationProvider.getNavigationSubtree("t1", NavigationFilter.DEFAULT, localization);

        //then
    }

    @Test(expected = TaxonomyNotFoundException.class)
    public void shouldPassTheRequest_ToTheService() throws DxaItemNotFoundException {
        //given
        when(onDemandNavigationModelProvider.getNavigationSubtree(any())).thenReturn(Optional.empty());
        NavigationFilter navigationFilter = new NavigationFilter().setDescendantLevels(666).setWithAncestors(true);
        String sitemapItemId = "sitemap";

        //when
        dynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);
    }

    @Test
    public void shouldConvertServiceResponse_AndReturnTheResult() throws DxaItemNotFoundException {
        //given
        NavigationFilter navigationFilter = new NavigationFilter().setDescendantLevels(666).setWithAncestors(true);
        String sitemapItemId = "whatever";
        when(onDemandNavigationModelProvider.getNavigationSubtree(any())).thenReturn(Optional.of(Collections.singletonList(navigationModel)));

        //when
        Collection<SitemapItem> subtree = dynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        verify(onDemandNavigationModelProvider).getNavigationSubtree(argThat(new ArgumentMatcher<SitemapRequestDto>() {
            @Override
            public boolean matches(Object argument) {
                SitemapRequestDto dto = (SitemapRequestDto) argument;
                assertEquals(localization.getId(), String.valueOf(dto.getLocalizationId()));
                assertEquals(new DepthCounter(666), dto.getExpandLevels());
                assertEquals(navigationFilter, dto.getNavigationFilter());
                assertEquals(sitemapItemId, dto.getSitemapId());
                return true;
            }
        }));

        assertEquals(1, subtree.size());
        SitemapItem root = subtree.iterator().next();
        assertEquals("t1", root.getId());
        assertTrue(root instanceof TaxonomyNode);
        Set<SitemapItem> items = root.getItems();
        assertEquals(7, items.size());
        SitemapItem t1p0 = items.iterator().next();
        assertEquals("t1-p0", t1p0.getId());
        assertFalse(t1p0 instanceof TaxonomyNode);
    }

    @Test
    public void shouldHandleMultipleItems_AndReturnTheResult() throws DxaItemNotFoundException {
        //given
        when(onDemandNavigationModelProvider.getNavigationSubtree(any())).thenReturn(Optional.of(Arrays.asList(
                new SitemapItemModelData().setId("t1-p1").setVisible(true).setUrl("/index").setTitle("001 Index"),
                new SitemapItemModelData().setId("t1-p2").setVisible(true).setUrl("/index2").setTitle("002 Index 2"))));

        //when
        Collection<SitemapItem> subtree = dynamicNavigationProvider.getNavigationSubtree("whatever", NavigationFilter.DEFAULT, localization);

        //then
        assertEquals(2, subtree.size());
        Iterator<SitemapItem> iterator = subtree.iterator();
        assertEquals("t1-p1", iterator.next().getId());
        assertEquals("t1-p2", iterator.next().getId());
    }

    //endregion

    //TSI-2514
    @Test
    public void shouldRequestTheWholeTree_ForNormalNavigation() throws NavigationProviderException {
        //given
        doReturn(Optional.empty()).when(navigationModelProvider).getNavigationModel(argThat(getDefaultMatcher()));

        //when
        dynamicNavigationProvider.getNavigationModel(localization);

        //then
        verify(navigationModelProvider).getNavigationModel(argThat(new ArgumentMatcher<SitemapRequestDto>() {
            @Override
            public boolean matches(Object argument) {
                SitemapRequestDto dto = (SitemapRequestDto) argument;
                return dto.getLocalizationId() == 42 && dto.getExpandLevels().equals(DepthCounter.UNLIMITED_DEPTH)
                        && dto.getNavigationFilter().equals(new NavigationFilter().setDescendantLevels(-1));
            }
        }));
    }

    @NotNull
    private ArgumentMatcher<SitemapRequestDto> getDefaultMatcher() {
        return new ArgumentMatcher<SitemapRequestDto>() {
            @Override
            public boolean matches(Object argument) {
                return ((SitemapRequestDto) argument).getLocalizationId() == 42;
            }
        };
    }

}