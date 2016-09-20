package com.sdl.webapp.tridion.navigation;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;
import java.util.List;

import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractDynamicNavigationProviderTest {

    private StaticNavigationProvider staticNavigationProvider = mock(StaticNavigationProvider.class);

    private Localization localization = mock(Localization.class);

    private LinkResolver linkResolver = mock(LinkResolver.class);

    private PayloadCacheProvider payloadCacheProvider = mock(PayloadCacheProvider.class);

    private AbstractDynamicNavigationProvider defaultDynamicNavigationProvider = getTestProvider(false, getNavigationModel(), "taxonomyId");

    @NotNull
    private AbstractDynamicNavigationProvider getTestProvider(final boolean callRealNavigationModel, final SitemapItem model, final String taxonomyId) {
        AbstractDynamicNavigationProvider provider = new AbstractDynamicNavigationProvider(staticNavigationProvider, linkResolver, payloadCacheProvider) {
            @Override
            public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
                return callRealNavigationModel ? super.getNavigationModel(localization) : createTaxonomyNode(taxonomyId, localization);
            }

            @Override
            protected SitemapItem createTaxonomyNode(String taxonomyId, Localization localization) {
                return model;
            }

            @Override
            protected String getNavigationTaxonomyId(Localization localization) {
                return taxonomyId;
            }
        };

        ReflectionTestUtils.setField(provider, "sitemapItemTypePage", "Page");
        ReflectionTestUtils.setField(provider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        return spy(provider);
    }

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

        when(linkResolver.resolveLink(anyString(), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "resolved-" + invocation.getArguments()[0];
            }
        });

        when(payloadCacheProvider.loadPayloadFromLocalCache(anyString())).thenReturn(new CacheElementImpl<>(null, true));
    }

    @Test
    public void shouldGetNavigationModel() throws NavigationProviderException, DxaException {
        //when
        AbstractDynamicNavigationProvider testProvider = getTestProvider(true, getNavigationModel(), "taxonomyId");
        testProvider.getNavigationModel(localization);

        //then
        verify(testProvider).getNavigationTaxonomyId(eq(localization));
        verify(testProvider).createTaxonomyNode(eq("taxonomyId"), eq(localization));
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIdIsNull() throws NavigationProviderException, DxaException {
        //given
        AbstractDynamicNavigationProvider testProvider = getTestProvider(true, getNavigationModel(), null);

        //when
        SitemapItem sitemapItem = testProvider.getNavigationModel(localization);

        //then
        verify(staticNavigationProvider).getNavigationModel(eq(localization));
        verify(testProvider).getNavigationTaxonomyId(eq(localization));
        assertEquals("Static", sitemapItem.getTitle());
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIsNotAvailable() throws NavigationProviderException, DxaException {
        //given
        AbstractDynamicNavigationProvider testProvider = getTestProvider(false, new SitemapItem(), "taxonomyId");
        verifyCallbackForNavigationLinksTests(testProvider);
    }

    @Test
    public void shouldFallbackToStaticIfTaxonomyIsNull() throws NavigationProviderException, DxaException {
        //given
        AbstractDynamicNavigationProvider testProvider = getTestProvider(false, null, "taxonomyId");
        verifyCallbackForNavigationLinksTests(testProvider);
    }

    private void verifyCallbackForNavigationLinksTests(AbstractDynamicNavigationProvider testProvider) throws NavigationProviderException {
        //when
        NavigationLinks navigationLinks = testProvider.getTopNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getTopNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //when
        navigationLinks = testProvider.getContextNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getContextNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //when
        navigationLinks = testProvider.getBreadcrumbNavigationLinks("1", localization);

        //then
        verify(staticNavigationProvider).getBreadcrumbNavigationLinks(eq("1"), eq(localization));
        assertEquals("Static", navigationLinks.getId());

        //then for two cases
        verify(testProvider, times(3)).getNavigationModel(eq(localization));
    }

    @Test
    public void shouldFilterItemsAndResolveUrls() throws NavigationProviderException {
        //given
        List<SitemapItem> items = getSitemapItems();

        //when
        List<Link> links = defaultDynamicNavigationProvider.prepareItemsAsVisibleNavigation(localization, items).getItems();

        //then
        assertTrue(links.size() == 3);
        assertEquals("resolved-/index", links.get(0).getUrl());
    }

    @Test
    public void shouldFindTheIndexPageFromSitemaps() {
        //given 
        String url = "hello-world/index.html";
        String expected = "hello-world";

        //when
        String index = defaultDynamicNavigationProvider.findIndexPageUrl(Lists.newArrayList(siteMapItem(true, "qwe"), siteMapItem(true, url)));
        String notFound = defaultDynamicNavigationProvider.findIndexPageUrl(Lists.newArrayList(siteMapItem(true, "qwe"), siteMapItem(true, "asd")));

        //then
        assertEquals(expected, index);
        assertNull(notFound);
    }

    @Test
    public void shouldProcessNavigationFromTheTop() throws NavigationProviderException {
        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                defaultDynamicNavigationProvider.getTopNavigationLinks("/", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                return list.size() == 7;
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
                defaultDynamicNavigationProvider.getContextNavigationLinks("/child/child_2", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //we expect parent instead of child
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/child/child_2") &&
                        iterator.next().getUrl().equals("/child/child_3") &&
                        !iterator.hasNext();
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
                defaultDynamicNavigationProvider.getContextNavigationLinks("not exist", localization);
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
        when(localization.getPath()).thenReturn("/");

        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/child/child_2", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //we expect parent instead of child
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/child") &&
                        iterator.next().getUrl().equals("/child/child_2") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level for breadcrumbs");
            }
        });
    }

    @Test
    public void shouldFindHomeIfItsASibling() throws NavigationProviderException {
        when(localization.getPath()).thenReturn("/imitation_home");

        verifyProcessNavigationWithMatcher(new Action() {
            @Override
            public void perform() throws NavigationProviderException {
                defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/about", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                //home link is a sibling of current level, but we still expect it in a breadcrumb
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/imitation_home") &&
                        iterator.next().getUrl().equals("/about") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find home if it's not a root level");
            }
        });
    }

    @Test
    public void shouldCreateSiteMapItemFromPageMeta() {
        //given
        PageMetaDTO.PageMetaDTOBuilder builder = PageMetaDTO.builder().id(13).title("000 title").url("url.html");
        PageMetaDTO pageMeta = builder.build();
        PageMetaDTO pageMetaNoSequence = builder.title("title").build();
        PageMetaDTO pageMetaNoUrl = builder.url("").build();

        //when
        SitemapItem sitemapItem = defaultDynamicNavigationProvider.createSitemapItemFromPage(pageMeta, "42");
        SitemapItem sitemapItemNoSequence = defaultDynamicNavigationProvider.createSitemapItemFromPage(pageMetaNoSequence, "42");
        SitemapItem sitemapItemNoUrl = defaultDynamicNavigationProvider.createSitemapItemFromPage(pageMetaNoUrl, "42");

        //then
        assertEquals("t42-p13", sitemapItem.getId());
        assertEquals("Page", sitemapItem.getType());
        //sequence is NOT stripped
        assertEquals("000 title", sitemapItem.getTitle());
        //extension is stripped
        assertEquals("url", sitemapItem.getUrl());
        assertTrue(sitemapItem.isVisible());

        assertFalse(sitemapItemNoSequence.isVisible());
        assertFalse(sitemapItemNoUrl.isVisible());
    }

    @Test
    public void shouldCreateTaxonomyNodeFromKeyword() {
        //given 
        KeywordDTO.KeywordDTOBuilder builder = KeywordDTO.builder()
                .keywordUri("1-2")
                .taxonomyUri("3-4")
                .name("000 Root")
                .key("key")
                .withChildren(true)
                .referenceContentCount(1)
                .description("description")
                .keywordAbstract(true);

        KeywordDTO keyword = builder.build();
        KeywordDTO keywordRoot = builder.taxonomyUri("1-2").build();

        KeywordDTO withoutChildren = builder.withChildren(false).referenceContentCount(0).build();
        KeywordDTO withChildren1 = builder.withChildren(true).referenceContentCount(0).build();
        KeywordDTO withChildren2 = builder.withChildren(false).referenceContentCount(1).build();

        String taxonomyId = "42";
        String taxonomyNodeUrl = "node-url.html";
        List<SitemapItem> children = Lists.newArrayList(siteMapItem(true, "child1"));

        //when
        TaxonomyNode node = defaultDynamicNavigationProvider.createTaxonomyNodeFromKeyword(keyword, taxonomyId, taxonomyNodeUrl,
                children);

        TaxonomyNode nodeRoot = defaultDynamicNavigationProvider.createTaxonomyNodeFromKeyword(keywordRoot, taxonomyId, taxonomyNodeUrl,
                children);

        TaxonomyNode nodeWithoutChildren = defaultDynamicNavigationProvider.createTaxonomyNodeFromKeyword(withoutChildren, taxonomyId, taxonomyNodeUrl,
                children);

        TaxonomyNode nodeWithChildren1 = defaultDynamicNavigationProvider.createTaxonomyNodeFromKeyword(withChildren1, taxonomyId, taxonomyNodeUrl,
                children);

        TaxonomyNode nodeWithChildren2 = defaultDynamicNavigationProvider.createTaxonomyNodeFromKeyword(withChildren2, taxonomyId, taxonomyNodeUrl,
                children);

        //then
        assertEquals(getTaxonomySitemapIdentifier(taxonomyId, KEYWORD, "2"), node.getId());
        assertEquals("TaxonomyNode", node.getType());
        assertEquals("node-url", node.getUrl());
        assertEquals("000 Root", node.getTitle());
        assertTrue(node.isVisible());
        assertEquals(children, node.getItems());
        assertEquals("key", node.getKey());
        assertTrue(node.isWithChildren());
        assertEquals("description", node.getDescription());
        assertTrue(node.isTaxonomyAbstract());
        assertEquals(1, node.getClassifiedItemsCount());

        assertEquals(getTaxonomySitemapIdentifier(taxonomyId), nodeRoot.getId());
        assertFalse(nodeWithoutChildren.isWithChildren());
        assertTrue(nodeWithChildren1.isWithChildren());
        assertTrue(nodeWithChildren2.isWithChildren());
    }

    private void verifyProcessNavigationWithMatcher(Action action, Matcher<List<SitemapItem>> matcher) throws NavigationProviderException {
        //given
        when(defaultDynamicNavigationProvider.getNavigationModel(any(Localization.class))).thenReturn(getNavigationModel());

        //when
        action.perform();

        //then
        verify(defaultDynamicNavigationProvider).prepareItemsAsVisibleNavigation(eq(localization), argThat(matcher));
    }

    @NotNull
    private SitemapItem getNavigationModel() {
        SitemapItem model = new TaxonomyNode();
        model.setItems(getSitemapItems());
        return model;
    }

    @NotNull
    private List<SitemapItem> getSitemapItems() {
        /*
        +/index
        +/child
            +/child/child_2
            +/child/child_3
        +/about
        +/imitation_home
        -/hidden
        +""
        +null
        */

        SitemapItem home = siteMapItem(true, "/index");

        SitemapItem child = new SitemapItem();
        child.setUrl("/child");
        child.setItems(Lists.newArrayList(siteMapItem(true, "/child/child_2"), siteMapItem(true, "/child/child_3")));
        return Lists.newArrayList(
                home,
                child,
                siteMapItem(true, "/about"),
                siteMapItem(true, "/imitation_home"),
                siteMapItem(false, "/hidden"),
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