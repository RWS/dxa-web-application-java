package com.sdl.webapp.tridion.navigation;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.api.navigation.TaxonomySitemapItemUrisHolder;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.sdl.webapp.common.api.navigation.TaxonomySitemapItemUrisHolder.parse;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for On-demand for each use case:
 * <pre>
 * Type  Ancest.  Desc.   Name of test
 * k     true     0 //shouldExpandAncestorsKeywordDescendantsZero
 * k     true     1 //shouldExpandAncestorsKeywordDescendantsOne
 * k     false    0 //shouldNotExpandAncestorsKeywordDescendantsZero
 * k     false    1 //shouldNotExpandAncestorsKeywordDescendantsOne
 * p     true     0 //shouldExpandAncestorsPageDescendantsZero
 * p     true     1 //shouldExpandAncestorsPageDescendantsOne
 * p     false    0 //shouldNotExpandAncestorsPageDescendantsZero
 * p     false    1 //shouldNotExpandAncestorsPageDescendantsOne
 * pm    true     0 //shouldExpandAncestorsMultiPageDescendantsZero
 * pm    true     1 //shouldExpandAncestorsMultiPageDescendantsOne
 * pm    false    0 //shouldNotExpandAncestorsMultiPageDescendantsZero
 * pm    false    1 //shouldNotExpandAncestorsMultiPageDescendantsOne
 *
 * root  false    1 //shouldReturnEmptyList_NotExpandingAncestors_ForTaxonomyRoot   /subtree/t1
 * </pre>
 */
public class AbstractDynamicNavigationProviderTest {

    private StaticNavigationProvider staticNavigationProvider = mock(StaticNavigationProvider.class);

    private Localization localization = mock(Localization.class);

    private LinkResolver linkResolver = mock(LinkResolver.class);

    private PayloadCacheProvider payloadCacheProvider = mock(PayloadCacheProvider.class);

    private CacheElement<Object> cacheElement = spy(new CacheElementImpl<>(null, true));

    private AbstractDynamicNavigationProvider defaultDynamicNavigationProvider = getTestProvider(false, getNavigationModel(), "taxonomyId");

    @NotNull
    private AbstractDynamicNavigationProvider getTestProvider(final boolean callRealNavigationModel, final SitemapItem model, final String taxonomyId) {
        AbstractDynamicNavigationProvider provider = new AbstractDynamicNavigationProvider(staticNavigationProvider, linkResolver, payloadCacheProvider) {
            @Override
            public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
                return callRealNavigationModel ? super.getNavigationModel(localization) : createTaxonomyNode(taxonomyId, localization);
            }

            @Override
            protected Set<SitemapItem> expandTaxonomyRoots(NavigationFilter navigationFilter, Localization localization) {
                return null;
            }

            @Override
            protected Set<SitemapItem> expandDescendants(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
                return null;
            }

            @Override
            protected SitemapItem createTaxonomyNode(String taxonomyId, Localization localization) {
                return model;
            }

            @Override
            protected String getNavigationTaxonomyId(Localization localization) {
                return taxonomyId;
            }

            @Nullable
            @Override
            protected TaxonomyNode expandAncestorsForKeyword(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
                return null;
            }

            @Override
            protected List<SitemapItem> collectAncestorsForPage(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
                return null;
            }
        };

        ReflectionTestUtils.setField(provider, "sitemapItemTypePage", "Page");
        ReflectionTestUtils.setField(provider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        return spy(provider);
    }

    @Before
    public void init() throws NavigationProviderException, DxaException {
        when(localization.getId()).thenReturn("1");

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

        when(payloadCacheProvider.loadPayloadFromLocalCache(anyString())).thenReturn(cacheElement);


        doReturn(Collections.<SitemapItem>emptySet()).when(defaultDynamicNavigationProvider).expandDescendants(
                any(TaxonomySitemapItemUrisHolder.class), any(NavigationFilter.class), any(Localization.class));


        mockSingleAncestorForKeyword("t1-k22", taxonomyNode("t1", "/", true, set(
                taxonomyNode("t1-k2", "/child", true, set(
                        taxonomyNode("t1-k22", "/child/child_2", true, set())
                ))
        )));

        //region Descendants
        doReturn(Collections.<SitemapItem>emptySet()).when(defaultDynamicNavigationProvider).expandDescendants(
                any(TaxonomySitemapItemUrisHolder.class), any(NavigationFilter.class), any(Localization.class));

        mockDescendants("t1", set(
                siteMapItem("t1-p1", true, "/index"),
                taxonomyNode("t1-k2", "/child", true, set()),
                siteMapItem("t1-p3", true, "/about"),
                siteMapItem("t1-p4", true, "/imitation_home"),
                siteMapItem("t1-p5", false, "/hidden"),
                siteMapItem("t1-p6", true, ""),
                siteMapItem("t1-p7", true, null)
        ));

        mockDescendants("t1-k2", set(
                taxonomyNode("t1-k22", "/child/child_2", true, set()),
                taxonomyNode("t1-p23", "/child/child_3", true, set()),
                taxonomyNode("t1-k24", "/child/child_4", true, set())
        ));

        mockDescendants("t1-k22", set(
                siteMapItem("t1-p220", true, "/child/child_2/index"),
                siteMapItem("t1-p221", true, "/child/child_2/child_2_1"),
                siteMapItem("t1-p222", true, "/child/child_2/child_2_2")
        ));

        //TSI-1980, should never be called
        mockDescendants("t1-p211", set(
                siteMapItem("t1-p2211", true, "/child/child_2/child_2_1/child_2_1_1")
        ));

        mockDescendants("t1-k24", set(
                siteMapItem("t1-p222", true, "/child/child_2/child_2_2")
        ));
        //endregion

        mockAncestorsForPage("t1-p220", list(
                taxonomyNode("t1", "/", true, set(
                        taxonomyNode("t1-k2", "/child", true, set(
                                taxonomyNode("t1-k22", "/child/child_2", true, set())
                        ))
                ))
        ));

        mockAncestorsForPage("t1-p222", list(
                taxonomyNode("t1", "/", true, set(
                        taxonomyNode("t1-k2", "/child", true, set(
                                taxonomyNode("t1-k22", "/child/child_2", true, set())
                        ))
                )),
                taxonomyNode("t1", "/", true, set(
                        taxonomyNode("t1-k2", "/child", true, set(
                                taxonomyNode("t1-k24", "/child/child_4", true, set())
                        ))
                ))
        ));
    }

    @After
    public void finish() {
        //TSI-1980, never called because it's a page, don't attempt to get descendants for page
        verify(defaultDynamicNavigationProvider, never())
                .expandDescendants(argThat(keyToItemsMatcher("t1-p211")), any(NavigationFilter.class), any(Localization.class));
    }

    @NotNull
    private TaxonomyNode getNavigationModel() {
        return taxonomyNode("t1", "/", true, getSitemapItems());
    }

    @NotNull
    private Set<SitemapItem> getSitemapItems() {
        /*
        ROOT:
            +/index
            +/child
                +/child/child_2
                    +/child/child_2/index
                    +/child/child_2/child_2_1
                    +/child/child_2/child_2_2
                +/child/child_3
                +/child/child_4
                    +/child/child_2/child_2_2
            +/about
            +/imitation_home
            -/hidden
            +""
            +null
        */

        return set(
                siteMapItem("t1-p1", true, "/index"),
                taxonomyNode("t1-k2", "/child", true,
                        set(
                                taxonomyNode("t1-k22", "/child/child_2", true, set(
                                        siteMapItem("t1-p220", true, "/child/child_2/index"),
                                        siteMapItem("t1-p221", true, "/child/child_2/child_2_1"),
                                        siteMapItem("t1-p222", true, "/child/child_2/child_2_2"))
                                ),
                                siteMapItem("t1-p23", true, "/child/child_3"),
                                taxonomyNode("t1-k24", "/child/child_4", true, set(
                                        //duplicate page (sic!)
                                        siteMapItem("t1-p222", true, "/child/child_2/child_2_2"))
                                )
                        )),
                siteMapItem("t1-p3", true, "/about"),
                siteMapItem("t1-p4", true, "/imitation_home"),
                siteMapItem("t1-p5", false, "/hidden"),
                siteMapItem("t1-p6", true, ""),
                siteMapItem("t1-p7", true, null));
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
        //TSI-2001
        verify(cacheElement, never()).setPayload(Matchers.anyObject());
        verify(cacheElement, never()).setExpired(anyBoolean());

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
        Set<SitemapItem> items = getSitemapItems();

        //when
        List<Link> links = defaultDynamicNavigationProvider.prepareItemsAsVisibleNavigation(localization, items, true).getItems();

        //then
        assertTrue(links.size() == 4);
        assertEquals("resolved-/index", links.get(0).getUrl());
    }

    @Test
    public void shouldFindTheIndexPageFromSitemaps() {
        //given 
        String url = "hello-world/index.html";
        String expected = "hello-world";

        //when
        String index = defaultDynamicNavigationProvider.findIndexPageUrl(newArrayList(
                siteMapItem("t1-p1", true, "qwe"),
                siteMapItem("t1-p2", true, url)));
        String notFound = defaultDynamicNavigationProvider.findIndexPageUrl(newArrayList(
                siteMapItem("t1-p1", true, "qwe"),
                siteMapItem("t1-p2", true, "asd")));

        //then
        assertEquals(expected, index);
        assertNull(notFound);
    }

    @Test
    public void shouldProcessNavigationFromTheTop() throws NavigationProviderException {
        prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getTopNavigationLinks("/", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                Set<SitemapItem> list = (Set<SitemapItem>) item;

                return list.size() == 7;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Top navigation shouldn't filter any item out");
            }
        });
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContextForTaxonomyWithIndex() throws NavigationProviderException {
        prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getContextNavigationLinks("/child/child_2", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                Set<SitemapItem> list = (Set<SitemapItem>) item;

                //child_2 has an index page, so we're getting its siblings instead of child_3
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/child/child_2/index") &&
                        iterator.next().getUrl().equals("/child/child_2/child_2_1") &&
                        iterator.next().getUrl().equals("/child/child_2/child_2_2") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level");
            }
        });
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContextAndGetParentItems() throws NavigationProviderException {
        prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getContextNavigationLinks("/child/child_3", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                Set<SitemapItem> list = (Set<SitemapItem>) item;

                //child_3 has a parent, so we expect its parent's children
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/child/child_2") &&
                        iterator.next().getUrl().equals("/child/child_3") &&
                        iterator.next().getUrl().equals("/child/child_4") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level");
            }
        });
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContextOnTheRoot() throws NavigationProviderException {
        NavigationLinks links = prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getContextNavigationLinks("/index", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                Set<SitemapItem> list = (Set<SitemapItem>) item;

                //we expect filtering to be called with whole root level list of items
                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/index") &&
                        iterator.next().getUrl().equals("/child") &&
                        iterator.next().getUrl().equals("/about") &&
                        iterator.next().getUrl().equals("/imitation_home") &&
                        iterator.next().getUrl().equals("/hidden") &&
                        iterator.next().getId().equals("t1-p6") &&
                        iterator.next().getId().equals("t1-p7") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should find current context level");
            }
        });

        //then
        //although links are filtered and resolved
        Iterator<Link> iterator = links.getItems().iterator();
        assertTrue(iterator.next().getUrl().equals("resolved-/index"));
        assertTrue(iterator.next().getUrl().equals("resolved-/child"));
        assertTrue(iterator.next().getUrl().equals("resolved-/about"));
        assertTrue(iterator.next().getUrl().equals("resolved-/imitation_home"));
        assertFalse(iterator.hasNext());

        //also when
        //TSI-1956
        NavigationLinks linksIndexInPath = defaultDynamicNavigationProvider.getContextNavigationLinks("/index/", localization);

        //also then
        assertEquals(links, linksIndexInPath);
    }

    @Test
    public void shouldProcessNavigationFromTheCurrentContextWhenNothingFound() throws NavigationProviderException {
        prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getContextNavigationLinks("not exist", localization);
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

        NavigationLinks links = prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/child/child_2", localization);
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

        //also when
        //TSI-1956
        NavigationLinks linksIndexInPath = defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/child/child_2/", localization);

        //also then
        assertEquals(links, linksIndexInPath);
    }

    @Test //TSI-1958
    public void shouldIncludeEvenHiddenElementsIntoBreadcrumb() throws NavigationProviderException {
        NavigationLinks links = prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/hidden", localization);
            }
        }, new BaseMatcher<List<SitemapItem>>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                List<SitemapItem> list = (List<SitemapItem>) item;

                Iterator<SitemapItem> iterator = list.iterator();
                return iterator.next().getUrl().equals("/hidden") &&
                        !iterator.hasNext();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Context navigation should include hidden elements for breadcrumbs");
            }
        });

        assertFalse("Hidden element not filtered", links.getItems().isEmpty());
        assertTrue("The only element is resolved-/hidden", links.getItems().get(0).getUrl().equals("resolved-/hidden"));
    }

    @Test
    public void shouldFindHomeIfItsASibling() throws NavigationProviderException {
        when(localization.getPath()).thenReturn("/imitation_home");

        prepareItemsAsVisibleNavigationCalledWith(new Action() {
            @Override
            public NavigationLinks perform() throws NavigationProviderException {
                return defaultDynamicNavigationProvider.getBreadcrumbNavigationLinks("/about", localization);
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
        Set<SitemapItem> children = new LinkedHashSet<>(newArrayList(siteMapItem("t1-k1", true, "child1")));

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

    @Test
    public void shouldExpandTaxonomyRootsIfSiteMapIsEmpty() {
        //given
        NavigationFilter filter = mock(NavigationFilter.class);
        Localization localization = mock(Localization.class);

        //when
        defaultDynamicNavigationProvider.getNavigationSubtree("", filter, localization);

        //then
        verify(defaultDynamicNavigationProvider).expandTaxonomyRoots(eq(filter), eq(localization));
    }

    @Test
    public void shouldReturnEmptyListIfSiteMapIdIsWrong() {
        //given
        NavigationFilter filter = mock(NavigationFilter.class);
        Localization localization = mock(Localization.class);

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree("wrong id", filter, localization);

        //then
        verify(defaultDynamicNavigationProvider, never()).collectAncestorsForPage(any(TaxonomySitemapItemUrisHolder.class), any(NavigationFilter.class), any(Localization.class));
        verify(defaultDynamicNavigationProvider, never()).expandDescendants(any(TaxonomySitemapItemUrisHolder.class), any(NavigationFilter.class), any(Localization.class));
        verify(defaultDynamicNavigationProvider, never()).expandTaxonomyRoots(any(NavigationFilter.class), any(Localization.class));
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldTryExpandAncestorsKeywordWithNullResult() {
        //given 
        NavigationFilter navigationFilter = getNavigationFilter(true, 1);
        String sitemapItemId = "t1-k2";

        when(defaultDynamicNavigationProvider.expandAncestorsForKeyword(any(TaxonomySitemapItemUrisHolder.class), eq(navigationFilter), eq(localization)))
                .thenReturn(null);

        //when
        Collection<SitemapItem> emptyList = defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        verify(defaultDynamicNavigationProvider).expandAncestorsForKeyword(eq(parse(sitemapItemId, localization)), eq(navigationFilter), eq(localization));
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void shouldExpandAncestorsPageWithNoResult() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(true, 0);
        String sitemapItemId = "t1-p1";

        AbstractDynamicNavigationProvider testProvider = getTestProvider(false, getNavigationModel(), "t1");

        when(testProvider.collectAncestorsForPage(any(TaxonomySitemapItemUrisHolder.class), eq(navigationFilter), eq(localization)))
                .thenReturn(Collections.<SitemapItem>emptyList());

        //when
        Collection<SitemapItem> emptyList = testProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        verify(testProvider).collectAncestorsForPage(eq(parse(sitemapItemId, localization)), eq(navigationFilter), eq(localization));
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfExpandingAncestorsForNotPageNorKeyword() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(true, 1);
        String sitemapItemId = "t1";

        //when
        Collection<SitemapItem> emptyList = defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void shouldReturnEmptyList_NotExpandingAncestors_ForTaxonomyRoot() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String sitemapItemId = "t1";

        //when
        Collection<SitemapItem> emptyList = defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        assertTrue(emptyList.size() == 7);
    }

    @Test
    public void shouldExpandDescendantsIfFilterSaysSo() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String sitemapItemId = "t1-k2";

        //when
        defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        verify(defaultDynamicNavigationProvider).expandDescendants(eq(parse(sitemapItemId, localization)), eq(navigationFilter), eq(localization));
    }

    @Test
    public void shouldSkipExpandingDescendantsWithPage() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String sitemapItemId = "t1-p2";

        //when
        defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        verify(defaultDynamicNavigationProvider, never()).expandDescendants(eq(parse(sitemapItemId, localization)), eq(navigationFilter), eq(localization));
    }

    //region KEYWORDS
    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsKeywordDescendantsZero() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(true, 0);
        String currentContext = "t1-k22";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 1);
        SitemapItem level1 = root.getItems().iterator().next();
        assertEquals("t1-k2", level1.getId());

        assertTrue(level1.getItems().size() == 1);
        SitemapItem level2 = level1.getItems().iterator().next();
        assertEquals("t1-k22", level2.getId());

        assertFalse(((TaxonomyNode) level2).isWithChildren());
        assertTrue(level2.getItems().size() == 0);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsKeywordDescendantsOne() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(true, 1);
        String currentContext = "t1-k22";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 7);
        SitemapItem level1 = get(root.getItems(), 1);
        assertEquals("t1-k2", level1.getId());

        assertTrue(level1.getItems().size() == 3);
        SitemapItem level2 = get(level1.getItems(), 0);
        assertEquals("t1-k22", level2.getId());

        assertTrue(level2.getItems().size() == 3);
    }


    @Test
    public void shouldNotExpandAncestorsKeywordDescendantsZero() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 0);
        String currentContext = "t1-k22";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldNotExpandAncestorsKeywordDescendantsOne() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String currentContext = "t1-k22";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.size() == 3);
        assertEquals("t1-p220", get(list, 0).getId());
    }
    //endregion

    //region SINGLE PAGE
    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsPageDescendantsZero() {
        //given 
        NavigationFilter navigationFilter = getNavigationFilter(true, 0);
        String sitemapItemId = "t1-p220";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, localization);

        //then
        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 1);

        SitemapItem level1 = get(root.getItems(), 0);
        assertTrue(level1.getItems().size() == 1);

        SitemapItem level2 = get(level1.getItems(), 0);
        assertFalse(((TaxonomyNode) level2).isWithChildren());
        assertTrue(level2.getItems().size() == 0);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsPageDescendantsOne() {
        // given
        NavigationFilter navigationFilter = getNavigationFilter(true, 1);
        String currentContext = "t1-p220";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        //one root node with all children expanded because first we want ancestors, second we want 1 level of children
        //basically the whole tree

        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 7);
        SitemapItem level1 = get(root.getItems(), 1);
        assertEquals("t1-k2", level1.getId());

        assertTrue(level1.getItems().size() == 3);
        SitemapItem level2 = get(level1.getItems(), 0);
        assertEquals("t1-k22", level2.getId());

        assertTrue(level2.getItems().size() == 3);

        assertTrue(get(level2.getItems(), 0).getItems().isEmpty());
    }

    @Test
    public void shouldNotExpandAncestorsPageDescendantsZero() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 0);
        String currentContext = "t1-p220";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldNotExpandAncestorsPageDescendantsOne() {
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String currentContext = "t1-p220";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.isEmpty());
    }
    //endregion

    //region MULTI PAGE
    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsMultiPageDescendantsZero() {
        // given
        NavigationFilter navigationFilter = getNavigationFilter(true, 0);
        String currentContext = "t1-p222";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 1);
        SitemapItem level1 = get(root.getItems(), 0);
        assertEquals("t1-k2", level1.getId());

        assertTrue(level1.getItems().size() == 2);
        assertEquals("t1-k22", get(level1.getItems(), 0).getId());
        assertEquals("t1-k24", get(level1.getItems(), 1).getId());

        assertTrue(get(level1.getItems(), 0).getItems().size() == 0);
        assertFalse(((TaxonomyNode) get(level1.getItems(), 0)).isWithChildren());
        assertTrue(get(level1.getItems(), 1).getItems().size() == 0);
        assertFalse(((TaxonomyNode) get(level1.getItems(), 1)).isWithChildren());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldExpandAncestorsMultiPageDescendantsOne() {
        // given
        NavigationFilter navigationFilter = getNavigationFilter(true, 1);
        String currentContext = "t1-p222";


        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        //only root, because of ancestors true
        assertTrue(list.size() == 1);

        SitemapItem root = get(list, 0);
        assertEquals("t1", root.getId());

        assertTrue(root.getItems().size() == 7);
        SitemapItem level1 = get(root.getItems(), 1);
        assertEquals("t1-k2", level1.getId());

        assertTrue(level1.getItems().size() == 3);

        //"t1-k22"
        assertTrue(get(level1.getItems(), 0).getItems().size() == 3);
        assertTrue(get(level1.getItems(), 2).getItems().size() == 1);
    }

    @Test
    public void shouldNotExpandAncestorsMultiPageDescendantsZero() {
        //given
        NavigationFilter navigationFilter = getNavigationFilter(false, 0);
        String currentContext = "t1-p222";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldNotExpandAncestorsMultiPageDescendantsOne() {
        NavigationFilter navigationFilter = getNavigationFilter(false, 1);
        String currentContext = "t1-p222";

        //when
        Collection<SitemapItem> list = defaultDynamicNavigationProvider.getNavigationSubtree(currentContext, navigationFilter, localization);

        //then
        assertTrue(list.isEmpty());
    }
    //endregion

    private void mockDescendants(final String key, final Set<SitemapItem> items) {
        BaseMatcher<TaxonomySitemapItemUrisHolder> keyMatcher = keyToItemsMatcher(key);

        doReturn(items).when(defaultDynamicNavigationProvider).expandDescendants(argThat(keyMatcher), any(NavigationFilter.class), any(Localization.class));
    }

    private void mockSingleAncestorForKeyword(final String key, final SitemapItem root) {
        BaseMatcher<TaxonomySitemapItemUrisHolder> keyMatcher = keyToItemsMatcher(key);

        doReturn(root).when(defaultDynamicNavigationProvider).expandAncestorsForKeyword(argThat(keyMatcher), any(NavigationFilter.class), any(Localization.class));
    }

    private void mockAncestorsForPage(final String key, final List<SitemapItem> items) {
        BaseMatcher<TaxonomySitemapItemUrisHolder> keyMatcher = keyToItemsMatcher(key);

        doReturn(items).when(defaultDynamicNavigationProvider).collectAncestorsForPage(argThat(keyMatcher), any(NavigationFilter.class), any(Localization.class));
    }

    @NotNull
    private BaseMatcher<TaxonomySitemapItemUrisHolder> keyToItemsMatcher(final String key) {
        return new BaseMatcher<TaxonomySitemapItemUrisHolder>() {
            @Override
            public boolean matches(Object item) {
                return Objects.equals(item, TaxonomySitemapItemUrisHolder.parse(key, localization));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches a key " + key);
            }
        };
    }

    private NavigationLinks prepareItemsAsVisibleNavigationCalledWith(Action action, Matcher<List<SitemapItem>> matcher) throws NavigationProviderException {
        //given
        when(defaultDynamicNavigationProvider.getNavigationModel(any(Localization.class))).thenReturn(getNavigationModel());

        //when
        NavigationLinks links = action.perform();

        //then
        verify(defaultDynamicNavigationProvider).prepareItemsAsVisibleNavigation(eq(localization), argThat(matcher), anyBoolean());

        return links;
    }

    private List<SitemapItem> list(SitemapItem... items) {
        return Arrays.asList(items);
    }

    private Set<SitemapItem> set(SitemapItem... items) {
        return new LinkedHashSet<>(list(items));
    }

    private SitemapItem siteMapItem(String id, boolean visible, String url) {
        SitemapItem sitemapItem = new SitemapItem();
        fillSitemapItem(sitemapItem, id, url, visible);
        return sitemapItem;
    }

    private TaxonomyNode taxonomyNode(String id, String url, boolean visible, Set<SitemapItem> items) {
        TaxonomyNode node = new TaxonomyNode();
        node.setItems(items);
        fillSitemapItem(node, id, url, visible);
        return node;
    }

    private void fillSitemapItem(SitemapItem sitemapItem, String id, String url, boolean visible) {
        sitemapItem.setVisible(visible);
        sitemapItem.setUrl(url);
        sitemapItem.setId(id);
        String title = visible && !Strings.isNullOrEmpty(url) ?
                Strings.padStart(id.replaceFirst("t1(-[kp](\\d+))?", "$2"), 3, '0') + " " + id : id;
        sitemapItem.setTitle(title);
    }

    @NotNull
    private NavigationFilter getNavigationFilter(boolean ancestors, int levels) {
        NavigationFilter navigationFilter = new NavigationFilter();
        navigationFilter.setWithAncestors(ancestors);
        navigationFilter.setDescendantLevels(levels);
        return navigationFilter;
    }

    private <T> T get(Collection<T> set, int index) {
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    private interface Action {

        NavigationLinks perform() throws NavigationProviderException;
    }
}