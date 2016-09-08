package com.sdl.webapp.tridion.navigation;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.exceptions.DxaException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
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
import static org.mockito.Matchers.argThat;
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
        when(staticNavigationProvider.getContextNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);
        when(staticNavigationProvider.getBreadcrumbNavigationLinks(anyString(), eq(localization))).thenReturn(staticNavigationLinks);

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
        verify(dynamicNavigationProvider).getNavigationTaxonomyId(eq(localization));
        assertEquals("Static", sitemapItem.getTitle());
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIsNotAvailable() throws NavigationProviderException, DxaException {
        //given
        when(dynamicNavigationProvider.getNavigationModel(any(Localization.class))).thenReturn(new SitemapItem());

        verifyCallbackForNavigationLinksTests();
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIsNull() throws NavigationProviderException, DxaException {
        //given
        when(dynamicNavigationProvider.getNavigationModel(any(Localization.class))).thenReturn(null);

        verifyCallbackForNavigationLinksTests();
    }

    private void verifyCallbackForNavigationLinksTests() throws NavigationProviderException {
        //when
        NavigationLinks navigationLinks = dynamicNavigationProvider.getTopNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getTopNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //when
        navigationLinks = dynamicNavigationProvider.getContextNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getContextNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //when
        navigationLinks = dynamicNavigationProvider.getBreadcrumbNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getBreadcrumbNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //then for two cases
        verify(dynamicNavigationProvider, times(3)).getNavigationModel(eq(localization));
    }

    @Test
    public void shouldFilterItemsAndResolveUrls() throws NavigationProviderException {
        //given
        List<SitemapItem> items = getSitemapItems();

        //when
        List<Link> links = dynamicNavigationProvider.prepareItemsAsVisibleNavigation(localization, items).getItems();

        //then
        assertTrue(links.size() == 2);
        assertEquals("resolved-qwe.html", links.get(0).getUrl());
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

    @Test
    public void shouldProcessNavigationFromTheTop() throws NavigationProviderException {
        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                dynamicNavigationProvider.getTopNavigationLinks("qwe", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                return list.size() == 5;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Top navigation shouldn't filter any item out");
            }
        });
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContext() throws NavigationProviderException {
        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                dynamicNavigationProvider.getContextNavigationLinks("child_2", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //we expect parent instead of child
                return list.size() == 2 && list.get(0).getUrl().equals("child_2.html") && list.get(1).getUrl().equals("child_3.html");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level");
            }
        });
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContextWhenNothingFound() throws NavigationProviderException {
        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                dynamicNavigationProvider.getContextNavigationLinks("not exist", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                return list.size() == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should process empty list if nothing found");
            }
        });
    }

    @Test
    public void shouldProcessNavigationForBreadcrumbs() throws NavigationProviderException {
        when(localization.getPath()).thenReturn("qwe.html");

        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                dynamicNavigationProvider.getBreadcrumbNavigationLinks("child_2", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //we expect parent instead of child
                return list.size() == 3 &&
                        list.get(0).getUrl().equals("qwe.html") &&
                        list.get(1).getUrl().equals("child.html") &&
                        list.get(2).getUrl().equals("child_2.html");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level for breadcrumbs");
            }
        });
    }

    @Test
    public void shouldFindHomeIfItsASibling() throws NavigationProviderException {
        when(localization.getPath()).thenReturn("root.html");

        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                dynamicNavigationProvider.getBreadcrumbNavigationLinks("qwe.html", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //we expect parent instead of child
                return list.get(0).getUrl().equalsIgnoreCase("root.html");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find home if it's not a root level");
            }
        });
    }

    private void verifyProcessNavigationWithMatcher(Action action, Matcher<List<SitemapItem>> matcher) throws NavigationProviderException {
        //given
        when(dynamicNavigationProvider.getNavigationModel(any(Localization.class))).thenReturn(getNavigationModel());

        //when
        action.perform();

        //then
        verify(dynamicNavigationProvider).prepareItemsAsVisibleNavigation(eq(localization), argThat(matcher));
    }

    @NotNull
    private SitemapItem getNavigationModel() {
        SitemapItem model = new TaxonomyNode();
        model.setItems(getSitemapItems());
        return model;
    }

    @NotNull
    private List<SitemapItem> getSitemapItems() {
        SitemapItem item = siteMapItem(true, "qwe.html");
        SitemapItem child = new SitemapItem();
        child.setUrl("child.html");
        child.setItems(Lists.newArrayList(siteMapItem(true, "child_2.html"), siteMapItem(true, "child_3.html")));
        item.setItems(Lists.newArrayList(child));
        return Lists.newArrayList(
                item,
                siteMapItem(true, "root.html"),
                siteMapItem(false, "asd"),
                siteMapItem(true, ""),
                siteMapItem(true, null));
    }

    private SitemapItem siteMapItem(boolean visible, String url) {
        SitemapItem sitemapItem = new SitemapItem();
        sitemapItem.setVisible(visible);
        sitemapItem.setUrl(url);
        return sitemapItem;
    }

    private interface Action {

        void perform() throws NavigationProviderException;
    }
}