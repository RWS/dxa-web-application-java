package com.sdl.dxa.tridion.navigation.dynamic;

import com.google.common.collect.ImmutableMap;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyRelationManager;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.AllArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DynamicNavigationProviderImpl.class)
public class DynamicNavigationProviderTest {

    @Mock
    private PageMetaFactory pageMetaFactory;

    @Mock
    private WebTaxonomyFactory taxonomyFactory;

    @Mock
    private TaxonomyRelationManager relationManager;

    @InjectMocks
    private DynamicNavigationProviderImpl navigationProvider;

    private ImmutableMap<String, Keyword> navigation;

    private Map<String, Keyword> knownKeywords = new HashMap<>();

    private Map<Integer, PageMeta> knownPages = new HashMap<>();

    private ImmutableMap<Object, Object> navigationModel;

    private Keyword fullNavigation;

    private String[] taxonomies;

    @Before
    public void init() throws Exception {
        ReflectionTestUtils.setField(navigationProvider, "taxonomyNavigationMarker", "[Taxonomy]");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypePage", "Page");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypeStructureGroup", "StructureGroup");

        taxonomies = new String[]{"tcm:42-1-512", "tcm:42-22-512", "tcm:42-33-512"};

//        @formatter:off
        navigationModel = ImmutableMap.builder()
               .put("t2", new KeywordData("No Marker", 2, -1))
               .put("t3", new KeywordData("No Marker 2", 3, -1))
               .put("t1", new KeywordData("[Taxonomy]Root", 1, -1))
                   .put("t1-p11", new PageData("011 p11", 11))
                   .put("t1-k12", new KeywordData("012 k12", 1, 12))
                       .put("t1-k21", new KeywordData("021 k21", 1, 21))
                           .put("t1-p31", new PageData("031 p31", 31))
                       .put("t1-p22", new PageData("022 p22", 22))
                       .put("t1-k23", new KeywordData("023 k23", 1, 23))
                           .put("t1-p32", new PageData("032 p32", 32))
                   .put("t1-p13", new PageData("013 p13", 13))
                   .put("t1-k14", new KeywordData("014 k14", 1, 14))
// DUPLICATE!           .put("t1-p22", new PageData("022 p22", 22))
                   .put("t1-p15", new PageData("Hidden", 15))
//                        todo page(6, "")
//                        todo page(null)
                .build();
//        @formatter:on

        fullNavigation = keywordInit("t1",
                pageInit("t1-p11"),
                keywordInit("t1-k12",
                        keywordInit("t1-k21",
                                pageInit("t1-p31")
                        ),
                        pageInit("t1-p22"),
                        keywordInit("t1-k23",
                                pageInit("t1-p32")
                        )
                ),
                pageInit("t1-p13"),
                keywordInit("t1-k14",
                        pageInit("t1-p22")
                ),
                pageInit("t1-p15")
        );

        navigation = ImmutableMap.<String, Keyword>builder()
                .put(taxonomies[0], fullNavigation)
                .put(taxonomies[1], keyword("t2"))
                .put(taxonomies[2], keyword("t3"))
                .build();

        doReturn(new String[0]).when(taxonomyFactory).getTaxonomies(anyString());
        doReturn(taxonomies).when(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri(42)));
        doReturn(Arrays.copyOfRange(taxonomies, 1, taxonomies.length))
                .when(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri(666)));

        for (String taxonomy : taxonomies) {
            doReturn(navigation.get(taxonomy)).when(taxonomyFactory).getTaxonomyKeywords(eq(taxonomy), any(DepthFilter.class));
            doReturn(navigation.get(taxonomy)).when(taxonomyFactory).getTaxonomyKeywords(eq(taxonomy), any(DepthFilter.class), Matchers.isNull(String.class));
        }

        PowerMockito.whenNew(PageMetaFactory.class).withArguments(42).thenReturn(pageMetaFactory);
    }

    private void prepareDownstream(String... tcmUris) throws StorageException {
        for (String uri : tcmUris) {
            Keyword keyword = knownKeywords.get(uri);
            prepareFactory(keyword, keyword.getKeywordURI(), DepthFilter.FILTER_DOWN);
        }
    }

    private void prepareUpstream(Keyword keyword, String keywordUri) throws StorageException {
        prepareFactory(keyword, keywordUri, DepthFilter.FILTER_UP);
    }

    private void prepareFactory(Keyword keyword, String keywordUri, int direction) throws StorageException {
        Pattern pattern = Pattern.compile("[^\\d]+\\((?<depth>-?\\d+),(?<direction>\\d)\\)");
        doReturn(keyword).when(taxonomyFactory).getTaxonomyKeywords(anyString(), argThat(new ArgumentMatcher<DepthFilter>() {
            @Override
            public boolean matches(Object argument) {
                Matcher matcher = pattern.matcher(((DepthFilter) argument).toTaxonomyFilterUriRepresentation());
                return matcher.matches() && matcher.group("direction").equals(String.valueOf(direction));
            }
        }), eq(keywordUri));
    }

    @Test
    public void shouldReturnEmptyOptional_WhenNoTaxonomyRootFound() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(100).build();

        //when
        Optional<SitemapItemModelData> model = navigationProvider.getNavigationModel(requestDto);

        //then
        assertFalse(model.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptional_WhenNoMarkerFound() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(666).build();

        //when
        Optional<SitemapItemModelData> model = navigationProvider.getNavigationModel(requestDto);

        //then
        assertFalse(model.isPresent());
    }

    @Test
    public void shouldReturnNavigationModel_AndCreateTaxonomyModelOutOfIt() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42).build();

        //when
        Optional<SitemapItemModelData> optional = navigationProvider.getNavigationModel(requestDto);

        //then
        assertTrue(optional.isPresent());
        SitemapItemModelData sitemapItemModelData = optional.get();
        assertTrue(sitemapItemModelData instanceof TaxonomyNodeModelData);
        TaxonomyNodeModelData model = (TaxonomyNodeModelData) sitemapItemModelData;

        assertSitemapItem(model, "t1", "[Taxonomy]Root");
        Keyword keyword = navigation.get(taxonomies[0]);
        assertEquals(keyword.getKeywordChildren().size() + keyword.getReferencedContentCount(), model.getItems().size());

        Iterator<SitemapItemModelData> rootItems = model.getItems().iterator();

        SitemapItemModelData t1p11 = rootItems.next();
        assertIdAndItemsSize(t1p11, "t1-p11", 0);
        assertEquals("p11", t1p11.getTitle());
        assertEquals("011 p11", t1p11.getOriginalTitle());

        SitemapItemModelData t1k12 = rootItems.next();
        assertIdAndItemsSize(t1k12, "t1-k12", 3);
        SitemapItemModelData t1k21 = t1k12.getItems().first();
        assertIdAndItemsSize(t1k21, "t1-k21", 1);
        SitemapItemModelData t1p31 = t1k21.getItems().first();
        assertIdAndItemsSize(t1p31, "t1-p31", 0);

        assertIdAndItemsSize(rootItems.next(), "t1-p13", 0);
        assertIdAndItemsSize(rootItems.next(), "t1-k14", 1);
        assertIdAndItemsSize(rootItems.next(), "t1-p15", 0);
    }

//    ==============================================================
//region OnDemandNavigationProvider tests
//    ==============================================================

//            * Type  Ancest.  Desc.   Name of test
//            * k     true     0 //shouldExpandAncestors_Keyword_DescendantsZero
//            * k     true     1 //shouldExpandAncestors_Keyword_DescendantsOne
//            * k     false    0 //shouldNotExpandAncestorsKeywordDescendantsZero
//            * k     false    1 //shouldNotExpandAncestorsKeywordDescendantsOne
//            * p     true     0 //shouldExpandAncestorsPageDescendantsZero
//            * p     true     1 //shouldExpandAncestorsPageDescendantsOne
//            * p     false    0 //shouldNotExpandAncestorsPageDescendantsZero
//            * p     false    1 //shouldNotExpandAncestorsPageDescendantsOne
//            * pm    true     0 //shouldExpandAncestorsMultiPageDescendantsZero
//            * pm    true     1 //shouldExpandAncestorsMultiPageDescendantsOne
//            * pm    false    0 //shouldNotExpandAncestorsMultiPageDescendantsZero
//            * pm    false    1 //shouldNotExpandAncestorsMultiPageDescendantsOne
//            *
//            * root  false    1 //shouldReturnEmptyList_NotExpandingAncestors_ForTaxonomyRoot   /subtree/t1

    @Test
    public void shouldExpandAncestors_Keyword_DescendantsOne() throws StorageException {
        //given
        prepareUpstream(keyword("t1",
                keyword("t1-k12",
                        keyword("t1-k21")
                )
        ), "tcm:42-21-1024");
        prepareDownstream("tcm:42-1-512", "tcm:42-12-1024", "tcm:42-21-1024");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = navigationProvider.getNavigationSubtree(requestDto);

        //then
        //only root, because of ancestors true
        assertTrue(subtree.size() == 1);

        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 5);

        SitemapItemModelData t1k12 = get(root.getItems(), 1);
        assertIdAndItemsSize(t1k12, "t1-k12", 3);

        SitemapItemModelData t1k21 = get(t1k12.getItems(), 0);
        assertIdAndItemsSize(t1k21, "t1-k21", 1);

        assertIdAndItemsSize(get(t1k21.getItems(), 0), "t1-p31", 0);
    }

    @Test
    public void shouldExpandAncestors_Keyword_DescendantsZero() throws StorageException {
        //given
        prepareUpstream(keyword("t1",
                keyword("t1-k12",
                        keyword("t1-k21")
                )), "tcm:42-21-1024");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(0))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = navigationProvider.getNavigationSubtree(requestDto);

        //then
        //only root, because of ancestors true
        assertTrue(subtree.size() == 1);

        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 1);

        SitemapItemModelData t1k2 = get(root.getItems(), 0);
        assertIdAndItemsSize(t1k2, "t1-k12", 1);

        SitemapItemModelData t1k3 = get(t1k2.getItems(), 0);
        assertIdAndItemsSize(t1k3, "t1-k21", 0);
    }


    //endregion

    private void assertIdAndItemsSize(SitemapItemModelData modelData, String id, int itemsSize) {
        assertEquals(String.format("ID of %s should be %s", modelData.getId(), id), id, modelData.getId());
        assertTrue(String.format("Items size of %s should be %s but is %s", modelData.getId(),
                itemsSize, modelData.getItems() == null ? "null" : modelData.getItems().size()),
                (itemsSize == 0 && (modelData.getItems() == null || modelData.getItems().isEmpty()))
                        || modelData.getItems().size() == itemsSize);
    }

    private <T> T get(Collection<T> collection, int index) {
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    private Keyword keywordInit(String keywordId, Object... children) throws StorageException {
        Keyword keyword = keyword(keywordId, children);
        knownKeywords.put(keyword.getKeywordURI(), keyword);
        return keyword;
    }

    private Keyword keyword(String keywordId, Object... children) throws StorageException {
        KeywordData keywordData = (KeywordData) navigationModel.get(keywordId);

        Keyword keyword = mock(Keyword.class);
        doReturn(keywordData.title).when(keyword).getKeywordName();
        doReturn(String.format("tcm:42-%s-512", keywordData.taxonomyId)).when(keyword).getTaxonomyURI();
        if (keywordData.keywordId == -1) {
            doReturn(String.format("tcm:42-%s-512", keywordData.taxonomyId)).when(keyword).getKeywordURI();
        } else {
            doReturn(String.format("tcm:42-%s-1024", keywordData.keywordId)).when(keyword).getKeywordURI();
        }
        doReturn(false).when(keyword).isKeywordAbstract();
        doReturn("").when(keyword).getKeywordKey();

        List<Keyword> keywords = Stream.of(children).filter(Keyword.class::isInstance).map(Keyword.class::cast).collect(Collectors.toList());
        List<PageMeta> pages = Stream.of(children).filter(PageMeta.class::isInstance).map(PageMeta.class::cast).collect(Collectors.toList());
        doReturn(keywords).when(keyword).getKeywordChildren();
        doReturn(pages.toArray(new PageMeta[pages.size()])).when(pageMetaFactory).getTaxonomyPages(same(keyword), anyBoolean());

        keywords.forEach(k -> doReturn(keyword).when(k).getParentKeyword());

        doReturn(keywords.isEmpty()).when(keyword).hasKeywordChildren();
        doReturn(pages.size()).when(keyword).getReferencedContentCount();

        return keyword;
    }

    private PageMeta pageInit(String pageId) {
        PageMeta page = page(pageId);
        knownPages.put(page.getId(), page);
        return page;
    }

    private PageMeta page(String pageId) {
        PageData pageData = (PageData) navigationModel.get(pageId);

        PageMeta pageMeta = mock(PageMeta.class);
        doReturn(pageData.pageId).when(pageMeta).getId();
        doReturn(pageData.title).when(pageMeta).getTitle();
        // page urls are derived from titles by removing sequence number, replacing spaces with single slash, and making it lower case
        doReturn("/" + PathUtils.removeSequenceFromPageTitle(pageData.title).replaceAll(" +", "/").toLowerCase()).when(pageMeta).getURLPath();
        doReturn(new Date()).when(pageMeta).getLastPublicationDate();

        return pageMeta;
    }

    private void assertSitemapItem(SitemapItemModelData data, String id, String title) {
        assertEquals(id, data.getId());
        assertEquals(title, data.getTitle());
    }

    @AllArgsConstructor
    private static class PageData {

        String title;

        int pageId;
    }

    @AllArgsConstructor
    private static class KeywordData {

        String title;

        int taxonomyId;

        int keywordId;
    }
}