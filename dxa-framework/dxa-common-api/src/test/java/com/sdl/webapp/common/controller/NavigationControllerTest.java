package com.sdl.webapp.common.controller;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NavigationControllerTest {

    @Mock
    private NavigationProvider navigationProvider;

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    @Spy
    private NavigationController navigationController;

    private static SitemapItem getStaticModel() {
        SitemapItem model = getSitemap("home", "/", "Home", "StructureGroup");

        SitemapItem child1 = getSitemap("child1", "/child1", "Child1", "Page");
        SitemapItem child2 = getSitemap("child2", "/child2", "Child2", "StructureGroup");

        model.setItems(Lists.newArrayList(child1, child2));

        model.setMvcData(MvcDataCreator.creator().fromQualifiedName("Area:View").create());

        return model;
    }

    private static SitemapItem getSitemap(String id, String url, String title, String type) {
        SitemapItem item = new SitemapItem();
        item.setTitle(title);
        item.setUrl(url);
        item.setId(id);
        item.setType(type);
        return item;
    }

    public void mockWithModel(SitemapItem staticModel) throws NavigationProviderException {
        doReturn(staticModel).when(navigationController).getEntityFromRequest(any(HttpServletRequest.class), eq("entityId"));
        when(navigationProvider.getNavigationModel(any(Localization.class))).thenReturn(staticModel);
    }

    @Test
    public void shouldInjectHomeItemForStaticNavigationWhenHandlingSitemap() throws NavigationProviderException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        mockWithModel(getStaticModel());

        //when
        String view = navigationController.handleGetSiteMap(request, "entityId");
        SitemapItem item = (SitemapItem) request.getAttribute("entity");

        //then
        Iterator<SitemapItem> top = item.getItems().iterator();
        SitemapItem home = top.next();
        assertEquals("home", home.getId());
        assertEquals("child1", home.getItems().get(0).getId());
        assertEquals("child2", top.next().getId());

        assertEquals(view, "Area/Entity/View");
    }

    @Test
    public void shouldNotChangeNavigationModelForDynamicSitemap() throws NavigationProviderException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        SitemapItem staticModel = getStaticModel();
        staticModel.setType("AnyOtherType");
        mockWithModel(staticModel);

        //when
        navigationController.handleGetSiteMap(request, "entityId");
        SitemapItem item = (SitemapItem) request.getAttribute("entity");

        //then
        Iterator<SitemapItem> top = item.getItems().iterator();
        assertEquals("home", item.getId());
        assertEquals("child1", top.next().getId());
        assertEquals("child2", top.next().getId());
    }
}