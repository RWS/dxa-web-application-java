package com.sdl.dxa.tridion.navigation.dynamic;

import com.google.common.collect.ImmutableMap;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyRelationManager;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DynamicNavigationModelProviderImpl.class)
public class DynamicNavigationModelProviderTest {

    @Mock
    private PageMetaFactory pageMetaFactory;

    @Mock
    private WebTaxonomyFactory taxonomyFactory;

    @Mock
    private TaxonomyRelationManager relationManager;

    @InjectMocks
    private DynamicNavigationModelProviderImpl navigationModelProvider;

    private ImmutableMap<String, Keyword> navigation;

    private Map<String, Keyword> knownKeywords = new HashMap<>();

    private ImmutableMap<Object, Object> navigationModel;

    private String[] taxonomies;

    @Before
    public void init() throws Exception {
        ReflectionTestUtils.setField(navigationModelProvider, "taxonomyNavigationMarker", "[Taxonomy]");
        ReflectionTestUtils.setField(navigationModelProvider, "sitemapItemTypePage", "Page");
        ReflectionTestUtils.setField(navigationModelProvider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        ReflectionTestUtils.setField(navigationModelProvider, "sitemapItemTypeStructureGroup", "StructureGroup");

        taxonomies = new String[]{"tcm:42-1-512", "tcm:42-22-512", "tcm:42-33-512"};

//        @formatter:off
        navigationModel = ImmutableMap.builder()
               .put("t2", new KeywordData("No Marker", 2, -1))
               .put("t3", new KeywordData("No Marker 2", 3, -1))
               .put("t1", new KeywordData("[Taxonomy]Root", 1, -1))
                   .put("t1-k0", new KeywordData("011 k0", 1, 0))
                       .put("t1-p11", new PageData("011 p11", 11))
                       .put("t1-k12", new KeywordData("012 k12", 1, 12))
                           .put("t1-k21", new KeywordData("021 k21", 1, 21))
                               .put("t1-p31", new PageData("031 p31", 31,"/p31/index"))
                               .put("t1-p33", new PageData("033 p33", 33))
                           .put("t1-p22", new PageData("022 p22", 22))
                           .put("t1-p24", new PageData("024 p24", 24))
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

        Keyword fullNavigation = keywordInit("t1",
                keywordInit("t1-k0",
                        page("t1-p11"),
                        keywordInit("t1-k12",
                                keywordInit("t1-k21",
                                        page("t1-p31"),
                                        page("t1-p33")
                                ),
                                page("t1-p22"),
                                page("t1-p24"),
                                keywordInit("t1-k23",
                                        page("t1-p32")
                                )
                        ),
                        page("t1-p13"),
                        keywordInit("t1-k14",
                                page("t1-p22")
                        ),
                        page("t1-p15")
                )
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

        // all the downstream is prepared, upstream needs to be prepared just before tests
        for (Map.Entry<String, Keyword> entry : knownKeywords.entrySet()) {
            prepareTaxonomyFactory(entry.getValue(), entry.getValue().getKeywordURI(), DepthFilter.FILTER_DOWN);
        }

        Set<String> uris = new HashSet<>(Arrays.asList(tcmUris));

        for (Map.Entry<String, Keyword> entry : knownKeywords.entrySet()) {
            if (!uris.contains(entry.getKey())) {
                Keyword keyword = entry.getValue();
                doReturn(Collections.emptyList()).when(keyword).getKeywordChildren();
                doReturn(new PageMeta[0]).when(pageMetaFactory).getTaxonomyPages(same(keyword), anyBoolean());
            }
        }
    }

    private void prepareUpstreamForKeyword(Keyword keyword, String keywordUri) throws StorageException {
        prepareTaxonomyFactory(keyword, keywordUri, DepthFilter.FILTER_UP);
    }

    private void prepareUpstreamForPage(String pageUri, Keyword... keyword) throws StorageException {
        assertTrue(TcmUtils.getItemType(pageUri) == TcmUtils.PAGE_ITEM_TYPE);
        prepareRelationManager(pageUri, keyword);
    }

    private void prepareTaxonomyFactory(Keyword keyword, String keywordUri, int direction) throws StorageException {
        assertTrue(TcmUtils.getItemType(keywordUri) == TcmUtils.KEYWORD_ITEM_TYPE || TcmUtils.getItemType(keywordUri) == TcmUtils.CATEGORY_ITEM_TYPE);
        doReturn(keyword).when(taxonomyFactory).getTaxonomyKeywords(anyString(), argThat(depthFilterMatcher(direction)), eq(keywordUri));
    }

    private void prepareRelationManager(String pageUri, Keyword... keyword) throws StorageException {
        doReturn(keyword).when(relationManager).getTaxonomyKeywords(anyString(), eq(pageUri), any(Keyword[].class),
                argThat(depthFilterMatcher(DepthFilter.FILTER_UP)), eq(ItemTypes.PAGE));
    }

    @NotNull
    private ArgumentMatcher<DepthFilter> depthFilterMatcher(int direction) {
        return depthFilterMatcher(direction, 666);
    }

    private ArgumentMatcher<DepthFilter> depthFilterMatcher(int direction, int depth) {
        return new ArgumentMatcher<DepthFilter>() {
            @Override
            public boolean matches(Object argument) {
                Pattern pattern = Pattern.compile("[^\\d]+\\((?<depth>-?\\d+),(?<direction>\\d)\\)");
                Matcher matcher = pattern.matcher(((DepthFilter) argument).toTaxonomyFilterUriRepresentation());
                return matcher.matches() && matcher.group("direction").equals(String.valueOf(direction))
                        && (depth == 666 || matcher.group("depth").equals(String.valueOf(depth)));
            }
        };
    }

    private void verifyFiltering(int direction, boolean wasCalled) {
        assertTrue(direction == DepthFilter.FILTER_UP || direction == DepthFilter.FILTER_DOWN);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // https://stackoverflow.com/a/32829162/740771
        VerificationMode verificationMode = wasCalled ? atLeast(0) : never();

        verify(taxonomyFactory, verificationMode).getTaxonomyKeywords(captor.capture(), argThat(depthFilterMatcher(direction)));
        verify(taxonomyFactory, verificationMode).getTaxonomyKeywords(captor.capture(), argThat(depthFilterMatcher(direction)), anyString());
        verify(relationManager, verificationMode).getTaxonomyKeywords(captor.capture(), anyString(), any(Keyword[].class),
                argThat(depthFilterMatcher(direction)), eq(ItemTypes.PAGE));

        if (wasCalled) {
            assertTrue("At least one of filtering methods with direction " + direction + "expected to be called at least once", !captor.getAllValues().isEmpty());
        }
    }

    @Test
    public void shouldReturnEmptyOptional_WhenNoTaxonomyRootFound() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(100).build();

        //when
        Optional<TaxonomyNodeModelData> model = navigationModelProvider.getNavigationModel(requestDto);

        //then
        assertFalse(model.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptional_WhenNoMarkerFound() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(666).build();

        //when
        Optional<TaxonomyNodeModelData> model = navigationModelProvider.getNavigationModel(requestDto);

        //then
        assertFalse(model.isPresent());
    }

    @Test
    public void shouldReturnNavigationModel_AndCreateTaxonomyModelOutOfIt() {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42).navigationFilter(new NavigationFilter().setDescendantLevels(-1)).build();

        //when
        Optional<TaxonomyNodeModelData> optional = navigationModelProvider.getNavigationModel(requestDto);

        //then
        assertTrue(optional.isPresent());
        SitemapItemModelData sitemapItemModelData = optional.get();
        assertTrue(sitemapItemModelData instanceof TaxonomyNodeModelData);
        TaxonomyNodeModelData model = (TaxonomyNodeModelData) sitemapItemModelData;

        assertEquals("t1", model.getId());
        assertEquals("[Taxonomy]Root", model.getTitle());
        Keyword keyword = navigation.get(taxonomies[0]);
        assertEquals(keyword.getKeywordChildren().size() + keyword.getReferencedContentCount(), model.getItems().size());

        assertIdAndItemsSize(model, "t1", 1);

        SitemapItemModelData t1k0 = model.getItems().first();
        assertIdAndItemsSize(t1k0, "t1-k0", 5);
        Iterator<SitemapItemModelData> rootItems = t1k0.getItems().iterator();

        SitemapItemModelData t1p11 = rootItems.next();
        assertIdAndItemsSize(t1p11, "t1-p11", 0);
        assertEquals("p11", t1p11.getTitle());
        assertEquals("011 p11", t1p11.getOriginalTitle());

        SitemapItemModelData t1k12 = rootItems.next();
        assertIdAndItemsSize(t1k12, "t1-k12", 4);
        SitemapItemModelData t1k21 = t1k12.getItems().first();
        assertIdAndItemsSize(t1k21, "t1-k21", 2);
        SitemapItemModelData t1p31 = t1k21.getItems().first();
        assertIdAndItemsSize(t1p31, "t1-p31", 0);
        SitemapItemModelData t1p33 = t1k21.getItems().last();
        assertIdAndItemsSize(t1p33, "t1-p33", 0);

        assertIdAndItemsSize(rootItems.next(), "t1-p13", 0);
        assertIdAndItemsSize(rootItems.next(), "t1-k14", 1);
        assertIdAndItemsSize(rootItems.next(), "t1-p15", 0);

        verify(taxonomyFactory, atLeastOnce()).getTaxonomyKeywords(eq("tcm:42-1-512"), argThat(depthFilterMatcher(DepthFilter.FILTER_DOWN, -1)));
    }

//    ==============================================================
//region OnDemandNavigationModelProvider tests
//    ==============================================================

//            * Type  Ancest.  Desc.   Name of test
//            * k     true     0 //shouldExpandAncestors_Keyword_DescendantsZero
//            * k     true     1 //shouldExpandAncestors_Keyword_DescendantsOne
//            * k     false    0 //shouldNotExpandAncestors_Keyword_DescendantsZero
//            * k     false    1 //shouldNotExpandAncestors_Keyword_DescendantsOne
//            * p     true     0 //shouldExpandAncestors_Page_DescendantsZero
//            * p     true     1 //shouldExpandAncestors_Page_DescendantsOne
//            * p     false    0 //shouldNotExpandAncestors_Page_DescendantsZero
//            * p     false    1 //shouldNotExpandAncestors_Page_DescendantsOne
//            * pm    true     0 //shouldExpandAncestors_MultiPage_DescendantsZero
//            * pm    true     1 //shouldExpandAncestors_MultiPage_DescendantsOne
//            * pm    false    0 //shouldNotExpandAncestors_MultiPage_DescendantsZero
//            * pm    false    1 //shouldNotExpandAncestorsMultiPageDescendantsOne
//            *
//            * root  false    1 //shouldNotExpandAncestors_Root_DescendantsOne
//            * root  true     1 //shouldExpandAncestors_Root_DescendantsOne
//            * root  false    0 //shouldNotExpandAncestors_Root_DescendantsZero
//            * root  true     0 //shouldExpandAncestors_Root_DescendantsZero
//
//    NB! For some special reason Taxonomy Roots expanding does [.getDescendantLevels() - 1], so descendantLevels =- 1, but pages are still in-/excluded with normal value
//            * ----  ----     2 //shouldExpandTaxonomyRoots_DescendantsTwo
//            * ----  ----     1 //shouldExpandTaxonomyRoots_DescendantsOne
//            * ----  ----     0 //shouldExpandTaxonomyRoots_DescendantsZero

    @Test
    public void shouldExpandTaxonomyRoots_DescendantsTwo() throws StorageException {
        //given
        prepareDownstream("tcm:42-1-512");
        doReturn(new PageMeta[]{page("t1-p11")}).when(pageMetaFactory).getTaxonomyPages(same(knownKeywords.get("tcm:42-0-1024")), anyBoolean());

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .navigationFilter(new NavigationFilter().setDescendantLevels(2))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 3);
        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 1);

        SitemapItemModelData t1k0 = get(root.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, false);
    }

    @Test
    public void shouldExpandTaxonomyRoots_DescendantsOne() throws StorageException {
        prepareDownstream();

        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .navigationFilter(new NavigationFilter().setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 3);
        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, false);
    }

    @Test
    public void shouldExpandTaxonomyRoots_DescendantsZero() throws StorageException {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .navigationFilter(new NavigationFilter().setDescendantLevels(0))
                .build();

        //when
        assertEquals(Optional.of(Collections.emptyList()), navigationModelProvider.getNavigationSubtree(requestDto));
        verifyFiltering(DepthFilter.FILTER_DOWN, false);
        verifyFiltering(DepthFilter.FILTER_UP, false);

    }

    @Test
    public void shouldExpandAncestors_Root_DescendantsZero() throws StorageException {
        //given
        prepareDownstream("tcm:42-1-512");
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(0))
                .build();

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class, () -> {
            verifyFiltering(DepthFilter.FILTER_DOWN, false);
            verifyFiltering(DepthFilter.FILTER_UP, false);
        });
    }

    @Test
    public void shouldNotExpandAncestors_Root_DescendantsZero() throws StorageException {
        //given
        prepareDownstream("tcm:42-1-512");
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(0))
                .build();

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class, () -> {
            verifyFiltering(DepthFilter.FILTER_DOWN, false);
            verifyFiltering(DepthFilter.FILTER_UP, false);
        });
    }

    @Test
    public void shouldExpandAncestors_Root_DescendantsOne() throws StorageException {
        //given
        prepareDownstream("tcm:42-1-512");
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(1))
                .build();

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class, () -> {
            verifyFiltering(DepthFilter.FILTER_DOWN, false);
            verifyFiltering(DepthFilter.FILTER_UP, false);
        });
    }

    @Test
    public void shouldNotExpandAncestors_Root_DescendantsOne() throws StorageException {
        //given
        prepareDownstream("tcm:42-1-512");
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 1);
        assertTrue(get(subtree, 0).getItems().size() == 0);
    }

    @Test
    public void shouldNotExpandAncestors_MultiPage_DescendantsOne() throws StorageException {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p22")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(1))
                .build();

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldNotExpandAncestors_MultiPage_DescendantsZero() throws StorageException {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p22")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(0))
                .build();

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldExpandAncestors_MultiPage_DescendantsOne() throws StorageException {
        //given
        prepareUpstreamForPage("tcm:42-22-64",
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k12",
                                        page("t1-p22")
                                )
                        )
                ),
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k14",
                                        page("t1-p22")
                                )
                        )
                )
        );
        prepareDownstream("tcm:42-14-1024", "tcm:42-12-1024", "tcm:42-0-1024", "tcm:42-1-512");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p22")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 1);
        SitemapItemModelData t1 = get(subtree, 0);
        assertIdAndItemsSize(t1, "t1", 1);

        SitemapItemModelData t1k0 = get(t1.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 5);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 1);
        assertIdAndItemsSize(t1k12, "t1-k12", 4);

        SitemapItemModelData t1k21 = get(t1k12.getItems(), 0);
        assertIdAndItemsSize(t1k21, "t1-k21", 0);

        SitemapItemModelData t1k14 = get(t1k0.getItems(), 3);
        assertIdAndItemsSize(t1k14, "t1-k14", 1);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldExpandAncestors_MultiPage_DescendantsZero() throws StorageException {
        //given
        prepareUpstreamForPage("tcm:42-22-64",
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k12",
                                        page("t1-p22")
                                )
                        )
                ),
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k14",
                                        page("t1-p22")
                                )
                        )
                )
        );

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p22")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(0))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 1);
        SitemapItemModelData t1 = get(subtree, 0);
        assertIdAndItemsSize(t1, "t1", 1);

        SitemapItemModelData t1k0 = get(t1.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 2);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 0);
        assertIdAndItemsSize(t1k12, "t1-k12", 0);

        SitemapItemModelData t1k14 = get(t1k0.getItems(), 1);
        assertIdAndItemsSize(t1k14, "t1-k14", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, false);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldNotExpandAncestors_Page_DescendantsOne() throws StorageException {
        //given
        prepareDownstream("tcm:42-12-1024", "tcm:42-1-512");
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p24")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(1))
                .build();

        //when
        //then
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldNotExpandAncestors_Page_DescendantsZero() throws StorageException {
        //given
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p24")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(0))
                .build();

        //when
        //then
        //exception
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldExpandAncestors_Page_DescendantsOne() throws StorageException {
        //given
        prepareUpstreamForPage("tcm:42-24-64",
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k12",
                                        page("t1-p24")
                                )
                        )
                ));
        prepareDownstream("tcm:42-12-1024", "tcm:42-0-1024", "tcm:42-1-512");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p24")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 1);
        SitemapItemModelData t1 = get(subtree, 0);
        assertIdAndItemsSize(t1, "t1", 1);

        SitemapItemModelData t1k0 = get(t1.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 5);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 1);
        assertIdAndItemsSize(t1k12, "t1-k12", 4);

        SitemapItemModelData t1k21 = get(t1k12.getItems(), 0);
        assertIdAndItemsSize(t1k21, "t1-k21", 0);

        SitemapItemModelData t1p24 = get(t1k12.getItems(), 3);
        assertIdAndItemsSize(t1p24, "t1-p24", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldExpandAncestors_Page_DescendantsZero() throws StorageException {
        //given
        prepareUpstreamForPage("tcm:42-24-64",
                keyword("t1",
                        keyword("t1-k0",
                                keyword("t1-k12",
                                        page("t1-p24")
                                )
                        )
                ));

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-p24")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(0))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 1);
        SitemapItemModelData t1 = get(subtree, 0);
        assertIdAndItemsSize(t1, "t1", 1);

        SitemapItemModelData t1k0 = get(t1.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 1);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 0);
        assertIdAndItemsSize(t1k12, "t1-k12", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, false);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldNotExpandAncestors_Keyword_DescendantsOne() throws StorageException {
        //given
        prepareDownstream("tcm:42-21-1024", "tcm:42-12-1024", "tcm:42-1-512");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        assertTrue(subtree.size() == 2);
        assertIdAndItemsSize(get(subtree, 0), "t1-p31", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, false);
    }

    @NotNull
    private Collection<SitemapItemModelData> getOptionalSubtree(SitemapRequestDto requestDto) {
        Optional<Collection<SitemapItemModelData>> optional = navigationModelProvider.getNavigationSubtree(requestDto);
        assertTrue(optional.isPresent());
        return optional.get();
    }

    @Test
    public void shouldNotExpandAncestors_Keyword_DescendantsZero() {
        //given 
        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(false).setDescendantLevels(0))
                .build();

        //when
        //then
        //exception
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(requestDto), BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldExpandAncestors_Keyword_DescendantsOne() throws StorageException {
        //given
        prepareUpstreamForKeyword(keyword("t1",
                keyword("t1-k0",
                        keyword("t1-k12",
                                keyword("t1-k21",
                                        page("t1-p31"), // to verify how we find index page in keyword and set this url to keyword
                                        page("t1-p33")
                                )
                        )
                )
        ), "tcm:42-21-1024");

        prepareDownstream("tcm:42-21-1024", "tcm:42-12-1024", "tcm:42-0-1024", "tcm:42-1-512");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(1))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        //only root, because of ancestors true
        assertTrue(subtree.size() == 1);

        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 1);

        SitemapItemModelData t1k0 = get(root.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 5);

        SitemapItemModelData t1k2 = get(t1k0.getItems(), 0);
        assertIdAndItemsSize(t1k2, "t1-p11", 0);

        SitemapItemModelData t1k14 = get(t1k0.getItems(), 3);
        assertIdAndItemsSize(t1k14, "t1-k14", 0);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 1);
        assertIdAndItemsSize(t1k12, "t1-k12", 4);

        SitemapItemModelData t1k21 = get(t1k12.getItems(), 0);
        assertIdAndItemsSize(t1k21, "t1-k21", 2);
        //should find index page and put its url to keyword
        assertEquals("/p31", t1k21.getUrl());

        assertIdAndItemsSize(get(t1k21.getItems(), 0), "t1-p31", 0);
        assertIdAndItemsSize(get(t1k21.getItems(), 1), "t1-p33", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, true);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldExpandAncestors_Keyword_DescendantsZero() throws StorageException {
        //given
        prepareUpstreamForKeyword(keyword("t1",
                keyword("t1-k0",
                        keyword("t1-k12",
                                keyword("t1-k21")
                        )
                )
        ), "tcm:42-21-1024");

        SitemapRequestDto requestDto = SitemapRequestDto.builder(42)
                .sitemapId("t1-k21")
                .navigationFilter(new NavigationFilter().setWithAncestors(true).setDescendantLevels(0))
                .build();

        //when
        Collection<SitemapItemModelData> subtree = getOptionalSubtree(requestDto);

        //then
        //only root, because of ancestors true
        assertTrue(subtree.size() == 1);

        SitemapItemModelData root = get(subtree, 0);
        assertIdAndItemsSize(root, "t1", 1);

        SitemapItemModelData t1k0 = get(root.getItems(), 0);
        assertIdAndItemsSize(t1k0, "t1-k0", 1);

        SitemapItemModelData t1k12 = get(t1k0.getItems(), 0);
        assertIdAndItemsSize(t1k12, "t1-k12", 1);

        SitemapItemModelData t1k21 = get(t1k12.getItems(), 0);
        assertIdAndItemsSize(t1k21, "t1-k21", 0);

        verifyFiltering(DepthFilter.FILTER_DOWN, false);
        verifyFiltering(DepthFilter.FILTER_UP, true);
    }

    @Test
    public void shouldReturnEmptyList_IfRootsNotFound_BecauseOfInvalidLocalizationId() {
        //when
        Optional<Collection<SitemapItemModelData>> subtree = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(404).build());

        //then
        assertTrue(subtree.isPresent());
        assertTrue(subtree.get().isEmpty());
    }

    @Test
    public void shouldReturnEmptyList_IfRootsNotFound() {
        //when
        Optional<Collection<SitemapItemModelData>> subtree = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(404).build());

        //then
        assertTrue(subtree.isPresent());
        assertTrue(subtree.get().isEmpty());
    }

    @Test
    public void shouldReturnEmptyOptional_WhenRequestNonExistingNode() throws StorageException {
        //given

        //when
        Optional<Collection<SitemapItemModelData>> t2 = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("t2").build());
        Optional<Collection<SitemapItemModelData>> t2k404 = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("t2-k404")
                .navigationFilter(new NavigationFilter().setWithAncestors(true)).build());
        Optional<Collection<SitemapItemModelData>> t2k404_2 = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("t2-k404")
                .navigationFilter(new NavigationFilter().setWithAncestors(false)).build());
        Optional<Collection<SitemapItemModelData>> t2p404 = navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("t2-p404")
                .navigationFilter(new NavigationFilter().setWithAncestors(true)).build());


        assertFalse(t2.isPresent());
        assertFalse(t2k404.isPresent());
        assertFalse(t2k404_2.isPresent());
        assertFalse(t2p404.isPresent());
    }

    @Test
    public void shouldThrowException_WhenRequestForPage_Descendants() {
        //given

        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(
                SitemapRequestDto.builder(42).sitemapId("t1-p24").navigationFilter(new NavigationFilter().setWithAncestors(false)).build()),
                BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
    }

    @Test
    public void shouldThrowException_IfUriIsWrongFormat() {
        //when
        assertThrows(() -> navigationModelProvider.getNavigationSubtree(SitemapRequestDto.builder(42).sitemapId("hello world").build()),
                BadRequestException.class,
                () -> {
                    verifyFiltering(DepthFilter.FILTER_DOWN, false);
                    verifyFiltering(DepthFilter.FILTER_UP, false);
                });
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

    private PageMeta page(String pageId) {
        PageData pageData = (PageData) navigationModel.get(pageId);

        PageMeta pageMeta = mock(PageMeta.class);
        doReturn(pageData.pageId).when(pageMeta).getId();
        doReturn(pageData.title).when(pageMeta).getTitle();
        // page urls are derived from titles by removing sequence number, replacing spaces with single slash, and making it lower case
        doReturn(pageData.url == null ? "/" + PathUtils.removeSequenceFromPageTitle(pageData.title).replaceAll(" +", "/").toLowerCase() : pageData.url).when(pageMeta).getURLPath();
        doReturn(new Date()).when(pageMeta).getLastPublicationDate();

        return pageMeta;
    }

    private void assertThrows(Action shouldThrow, Class<? extends Exception> exception, Action assertsAfter) {
        try {
            shouldThrow.perform();
            fail("Exception " + exception + "was expected");
        } catch (Exception ex) {
            assertEquals(ex.getClass(), exception);
            assertsAfter.perform();
        }
    }

    @FunctionalInterface
    private interface Action {

        void perform();
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class PageData {

        final String title;

        final int pageId;

        String url;
    }

    @AllArgsConstructor
    private static class KeywordData {

        String title;

        int taxonomyId;

        int keywordId;
    }
}