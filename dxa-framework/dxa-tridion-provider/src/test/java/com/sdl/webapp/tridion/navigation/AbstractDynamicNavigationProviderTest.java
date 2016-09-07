package com.sdl.webapp.tridion.navigation;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDynamicNavigationProviderTest {

    @Mock
    private StaticNavigationProvider staticNavigationProvider;

    @Mock
    private Localization localization;

    @Mock
    private LinkResolver linkResolver;

    @Spy
    private AbstractDynamicNavigationProvider dynamicNavigationProvider = new AbstractDynamicNavigationProvider(staticNavigationProvider, linkResolver) {
        @Override
        protected List<SitemapItem> getTopNavigationLinksInternal(String taxonomyId, Localization localization) {
            return null;
        }

        @Override
        protected SitemapItem createTaxonomyNode(String taxonomyId, Localization localization) {
            return null;
        }

        @Override
        protected String getNavigationTaxonomyId(Localization localization) {
            return null;
        }
    };

    @Before
    public void init() throws NavigationProviderException, DxaException {
        SitemapItem staticSitemapItem = new SitemapItem();
        staticSitemapItem.setTitle("Static");

        when(staticNavigationProvider.getNavigationModel(eq(localization))).thenReturn(staticSitemapItem);

        NavigationLinks staticNavigationLinks = new NavigationLinks();
        staticNavigationLinks.setId("Static");

        when(staticNavigationProvider.getTopNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);

        when(dynamicNavigationProvider.getNavigationTaxonomyId(any(Localization.class))).thenReturn("taxonomyId");

        ReflectionTestUtils.setField(dynamicNavigationProvider, "staticNavigationProvider", staticNavigationProvider);
        ReflectionTestUtils.setField(dynamicNavigationProvider, "linkResolver", linkResolver);

        when(linkResolver.resolveLink(anyString(), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "resolved-" + invocation.getArguments()[0];
            }
        });
    }

    @Test
    public void shouldGetNavigationModel() throws NavigationProviderException, DxaException {
        //when
        dynamicNavigationProvider.getNavigationModel(localization);

        //then
        verify(dynamicNavigationProvider).getNavigationTaxonomyId(eq(localization));
        verify(dynamicNavigationProvider).createTaxonomyNode(eq("taxonomyId"), eq(localization));
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIdIsNull() throws NavigationProviderException, DxaException {
        //given
        when(dynamicNavigationProvider.getNavigationTaxonomyId(any(Localization.class))).thenReturn(null);

        //when
        SitemapItem sitemapItem = dynamicNavigationProvider.getNavigationModel(localization);

        //then
        verify(staticNavigationProvider).getNavigationModel(eq(localization));
        assertEquals("Static", sitemapItem.getTitle());

        //when
        NavigationLinks navigationLinks = dynamicNavigationProvider.getTopNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getTopNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());


        //then for all cases
        verify(dynamicNavigationProvider, times(2)).getNavigationTaxonomyId(eq(localization));
    }

    @Test
    public void shouldFilterItemsAndResolveUrls() throws NavigationProviderException {
        //given
        when(dynamicNavigationProvider.getTopNavigationLinksInternal(anyString(), any(Localization.class)))
                .thenReturn(Lists.newArrayList(siteMapItem(true, "qwe"), siteMapItem(false, "asd"), siteMapItem(true, ""), siteMapItem(true, null)));

        //when
        List<Link> links = dynamicNavigationProvider.getTopNavigationLinks("path", localization).getItems();

        //then
        verify(dynamicNavigationProvider).getTopNavigationLinksInternal(eq("taxonomyId"), eq(localization));
        assertTrue(links.size() == 1);
        assertEquals("resolved-qwe", links.get(0).getUrl());
    }

    @Test
    public void shouldFindTheIndexPageFromSitemaps() {
        //given 
        String url = "hello-world/index.html";

        //when
        String index = dynamicNavigationProvider.findIndexPageUrl(Lists.newArrayList(siteMapItem(true, "qwe"), siteMapItem(true, url)));
        String notFound = dynamicNavigationProvider.findIndexPageUrl(Lists.newArrayList(siteMapItem(true, "qwe"), siteMapItem(true, "asd")));

        //then
        assertEquals(url, index);
        assertNull(notFound);
    }

    private SitemapItem siteMapItem(boolean visible, String url) {
        SitemapItem sitemapItem = new SitemapItem();
        sitemapItem.setVisible(visible);
        sitemapItem.setUrl(url);
        return sitemapItem;
    }


}