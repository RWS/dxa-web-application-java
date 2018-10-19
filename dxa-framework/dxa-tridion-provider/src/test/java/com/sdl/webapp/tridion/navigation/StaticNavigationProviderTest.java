package com.sdl.webapp.tridion.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.modelservice.DefaultModelServiceProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import static com.sdl.webapp.tridion.navigation.StaticNavigationProvider.TYPE_STRUCTURE_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaticNavigationProviderTest {

    private static final String NAVIGATION_JSON = "/navigation.json";

    private static final String NORMALIZED_PATH = "mypage" + NAVIGATION_JSON;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Localization localization;

    @Mock
    private LinkResolver linkResolver;

    @Mock
    private DefaultModelServiceProvider defaultModelService;

    @InjectMocks
    @Spy
    private StaticNavigationProvider provider;

    private static SitemapItem getSiteMapGroup(String title, String url) {
        SitemapItem siteMap = getSiteMap(title, url, true);
        siteMap.setType(TYPE_STRUCTURE_GROUP);
        return siteMap;
    }

    private static SitemapItem getSiteMap(String title, String url, boolean visible) {
        SitemapItem sitemapItem = new SitemapItem();
        sitemapItem.setTitle(title);
        sitemapItem.setUrl(url);
        sitemapItem.setVisible(visible);
        sitemapItem.setItems(Collections.emptySet());
        return sitemapItem;
    }

    @Before
    public void before() throws IOException, ContentProviderException {
        ReflectionTestUtils.setField(provider, "navigationModelUrl", NAVIGATION_JSON);

        when(linkResolver.resolveLink(anyString(), anyString())).thenAnswer(invocation ->
                invocation.getArgumentAt(0, String.class));

        when(localization.localizePath(eq(NAVIGATION_JSON))).thenReturn(NORMALIZED_PATH);
        when(localization.getId()).thenReturn("1");

        SitemapItem parentGroup = getSiteMapGroup("parent", "parent");
        SitemapItem child1Group = getSiteMapGroup("child1", "parent/child1");
        SitemapItem child11Group = getSiteMapGroup("child11", "parent/child1/child11");

        SitemapItem parent = getSiteMap("parent", "parent", true);

        SitemapItem child1 = getSiteMap("child1", "parent/child1", true);
        SitemapItem child11 = getSiteMap("child11", "parent/child1/child11", true);

        SitemapItem child2 = getSiteMap("child2", "parent/child2", false);

        parentGroup.setItems(new LinkedHashSet<>(Lists.newArrayList(parent, child1Group, child2)));
        child1Group.setItems(new LinkedHashSet<>(Lists.newArrayList(child1, child11)));
        child11Group.setItems(new LinkedHashSet<>(Lists.newArrayList(child11)));

        // CM orders navigation model and the collection IS already sorted once we load it in DXA
        when(objectMapper.readValue(any(InputStream.class), eq(SitemapItem.class))).thenReturn(parentGroup);

        when(defaultModelService.loadPageContent(any(PageRequestDto.class))).thenReturn("");
    }

    @Test
    public void shouldGetNavigationModel() throws ContentProviderException {
        //when
        SitemapItem sitemapItem = provider.getNavigationModel(localization);

        //then
        verify(localization).localizePath(eq(NAVIGATION_JSON));
        verify(provider).getPageContent(eq(NORMALIZED_PATH), eq(localization));
        verify(linkResolver, times(2)).resolveLink(eq("parent"), eq("1"));
        assertEquals("parent", sitemapItem.getUrl());
        List<SitemapItem> items = new ArrayList<>(sitemapItem.getItems());
        assertEquals("parent", items.get(0).getUrl());
        assertEquals("parent", items.get(0).getTitle());
        assertEquals("parent/child1", items.get(1).getUrl());
        assertEquals("child1", items.get(1).getTitle());
        assertEquals("parent/child2", items.get(2).getUrl());
        assertEquals("child2", items.get(2).getTitle());
    }

    @Test(expected = ContentProviderException.class)
    @SuppressWarnings("unchecked")
    public void shouldGetNavigationModeFactoryException() throws ContentProviderException {
        //given
        doThrow(ContentProviderException.class).when(provider).getPageContent(anyString(), any(Localization.class));

        //when
        provider.getNavigationModel(localization);

        //then
        //exception
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NavigationProviderException.class)
    public void shouldRethrowExceptionInGetNavigationModel() throws NavigationProviderException {
        //given
        when(localization.localizePath(anyString())).thenThrow(ContentProviderException.class);

        //when
        provider.getNavigationModel(localization);

        //then
        //exception
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NavigationProviderException.class)
    public void shouldRethrowExceptionInGetNavigationModel2() throws NavigationProviderException {
        //given
        when(localization.localizePath(anyString())).thenThrow(IOException.class);

        //when
        provider.getNavigationModel(localization);

        //then
        //exception
    }

    @Test
    public void shouldGetNavigationLinksForTopLevel() throws NavigationProviderException {
        //when
        NavigationLinks links = provider.getTopNavigationLinks("", localization);

        //then
        Iterator<Link> iterator = links.getItems().iterator();
        Link next = iterator.next();
        assertEquals("parent", next.getUrl());
        assertEquals("parent", next.getLinkText());

        next = iterator.next();
        assertEquals("parent/child1", next.getUrl());
        assertEquals("child1", next.getLinkText());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldGetNavigationLinksForChildLevel() throws NavigationProviderException {
        //when
        NavigationLinks links = provider.getContextNavigationLinks("parent", localization);
        NavigationLinks links2 = provider.getContextNavigationLinks("parent/child1", localization);

        //then
        Iterator<Link> iterator = links.getItems().iterator();
        Link next = iterator.next();
        assertEquals("parent", next.getUrl());
        assertEquals("parent", next.getLinkText());
        next = iterator.next();
        assertEquals("parent/child1", next.getUrl());
        assertEquals("child1", next.getLinkText());
        assertFalse(iterator.hasNext());

        iterator = links2.getItems().iterator();
        next = iterator.next();
        assertEquals("parent/child1", next.getUrl());
        assertEquals("child1", next.getLinkText());
        next = iterator.next();
        assertEquals("parent/child1/child11", next.getUrl());
        assertEquals("child11", next.getLinkText());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldGetBreadcrumbs() throws NavigationProviderException {
        //when
        NavigationLinks parent = provider.getBreadcrumbNavigationLinks("parent", localization);
        NavigationLinks child1 = provider.getBreadcrumbNavigationLinks("parent/child1", localization);
        NavigationLinks child11 = provider.getBreadcrumbNavigationLinks("parent/child1/child11", localization);

        //then
        Iterator<Link> iterator = parent.getItems().iterator();
        assertEquals("parent", iterator.next().getUrl());
        assertFalse(iterator.hasNext());

        iterator = child1.getItems().iterator();
        assertEquals("parent", iterator.next().getUrl());
        assertEquals("parent/child1", iterator.next().getUrl());
        assertFalse(iterator.hasNext());

        iterator = child11.getItems().iterator();
        assertEquals("parent", iterator.next().getUrl());
        assertEquals("parent/child1", iterator.next().getUrl());
        assertEquals("parent/child1/child11", iterator.next().getUrl());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldSaveOrderDefinedByCMInNavigationJson() throws ContentProviderException, IOException {
        //given
        InputStream navigation = new ClassPathResource("navigation.json").getInputStream();
        String navigationJson = IOUtils.toString(navigation);
        doReturn(new ByteArrayInputStream(navigationJson.getBytes())).when(provider).getPageContent(anyString(), eq(localization));

        ReflectionTestUtils.setField(provider, "objectMapper", new DxaSpringInitialization().objectMapper());

        //when
        SitemapItem navigationModel = provider.getNavigationModel(localization);

        //then
        Iterator<SitemapItem> iterator = navigationModel.getItems().iterator();
        assertEquals("Home", iterator.next().getTitle());
        assertEquals("Articles", iterator.next().getTitle());
        assertEquals("Further Information", iterator.next().getTitle());
        assertEquals("About", iterator.next().getTitle());
        assertEquals("Impress", iterator.next().getTitle());
        assertEquals("Search Results", iterator.next().getTitle());
        assertEquals("Sitemap", iterator.next().getTitle());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldBuildCorrectPageRequest() throws ContentProviderException {
        //given

        //when
        provider.getPageContent("/path", localization);

        //then
        verify(defaultModelService).loadPageContent(eq(PageRequestDto.builder(localization.getId(), "/path").build()));
    }

}