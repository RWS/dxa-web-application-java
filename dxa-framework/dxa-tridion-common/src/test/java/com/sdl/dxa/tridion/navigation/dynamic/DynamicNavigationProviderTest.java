package com.sdl.dxa.tridion.navigation.dynamic;

import com.google.common.collect.ImmutableMap;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyRelationManager;
import com.tridion.taxonomies.filters.DepthFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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

    @Before
    public void init() throws Exception {
        ReflectionTestUtils.setField(navigationProvider, "taxonomyNavigationMarker", "[Taxonomy]");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypePage", "Page");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        ReflectionTestUtils.setField(navigationProvider, "sitemapItemTypeStructureGroup", "StructureGroup");


        String[] taxonomies = {"tcm:42-1-512", "tcm:42-22-512", "tcm:42-33-512"};
        // page urls are derived from titles by removing sequence number, replacing spaces with single slash, and making it lower case
        ImmutableMap<Object, Object> navigation = ImmutableMap.builder()
                .put(taxonomies[0], keyword("[Taxonomy]Root", 1, -1,
                        page(1, "000 Index"),
                        keyword("002 Child", 1, 2,
                                keyword("020 Child Child2", 1, 3),
                                keyword("021 Child Child2 2", 1, 4),
                                page(2, "022 Child2")
                        ),
                        page(3, "001 About"),
                        page(4, "003 Imitation Home"),
                        page(5, "Hidden")
//                        todo page(6, "")
//                        todo page(null)
                ))
                .put(taxonomies[1], keyword("No Marker", 22, 22))
                .put(taxonomies[2], keyword("No Marker 2", 33, 33))
                .build();

        doReturn(new String[0]).when(taxonomyFactory).getTaxonomies(Matchers.anyString());
        doReturn(taxonomies).when(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri(42)));
        doReturn(Arrays.copyOfRange(taxonomies, 1, taxonomies.length))
                .when(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri(666)));

        for (String taxonomy : taxonomies) {
            doReturn(navigation.get(taxonomy)).when(taxonomyFactory).getTaxonomyKeywords(eq(taxonomy), any(DepthFilter.class));
        }

        PowerMockito.whenNew(PageMetaFactory.class).withArguments(42).thenReturn(pageMetaFactory);

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
        assertEquals(5, model.getItems().size());

        Iterator<SitemapItemModelData> rootItems = model.getItems().iterator();

        SitemapItemModelData indexPage = rootItems.next();
        assertSitemapItem(indexPage, "t1-p1", "Index");
        assertEquals("000 Index", indexPage.getOriginalTitle());

        SitemapItemModelData aboutPage = rootItems.next();
        assertSitemapItem(aboutPage, "t1-p3", "About");

        SitemapItemModelData childKeyword = rootItems.next();
        assertSitemapItem(childKeyword, "t1-k2", "Child");

        SitemapItemModelData imitationHomePage = rootItems.next();
        assertSitemapItem(imitationHomePage, "t1-p4", "Imitation Home");

        SitemapItemModelData hiddenPage = rootItems.next();
        assertSitemapItem(hiddenPage, "t1-p5", "Hidden");

        assertEquals(3, childKeyword.getItems().size());
        Iterator<SitemapItemModelData> items = childKeyword.getItems().iterator();
        assertSitemapItem(items.next(), "t1-k3", "Child Child2");
        assertSitemapItem(items.next(), "t1-k4", "Child Child2 2");
        assertSitemapItem(items.next(), "t1-p2", "Child2");
    }

    private Keyword keyword(String keywordName, int taxonomyId, int keywordId, Object... children) throws StorageException {
        Keyword keyword = mock(Keyword.class);
        doReturn(keywordName).when(keyword).getKeywordName();
        doReturn(String.format("tcm:42-%s-512", taxonomyId)).when(keyword).getTaxonomyURI();
        if (keywordId == -1) {
            doReturn(String.format("tcm:42-%s-512", taxonomyId)).when(keyword).getKeywordURI();
        } else {
            doReturn(String.format("tcm:42-%s-1024", keywordId)).when(keyword).getKeywordURI();
        }
        doReturn(false).when(keyword).isKeywordAbstract();
        doReturn("").when(keyword).getKeywordKey();

        List<Keyword> keywords = Stream.of(children).filter(Keyword.class::isInstance).map(Keyword.class::cast).collect(Collectors.toList());
        List<PageMeta> pages = Stream.of(children).filter(PageMeta.class::isInstance).map(PageMeta.class::cast).collect(Collectors.toList());
        doReturn(keywords).when(keyword).getKeywordChildren();
        doReturn(pages.toArray(new PageMeta[pages.size()])).when(pageMetaFactory).getTaxonomyPages(same(keyword), anyBoolean());

        doReturn(keywords.isEmpty()).when(keyword).hasKeywordChildren();
        doReturn(pages.size()).when(keyword).getReferencedContentCount();
        return keyword;
    }

    private PageMeta page(int pageId, String title) {
        PageMeta pageMeta = mock(PageMeta.class);
        doReturn(pageId).when(pageMeta).getId();
        doReturn(title).when(pageMeta).getTitle();
        doReturn("/" + PathUtils.removeSequenceFromPageTitle(title).replaceAll(" +", "/").toLowerCase()).when(pageMeta).getURLPath();
        doReturn(new Date()).when(pageMeta).getLastPublicationDate();
        return pageMeta;
    }

    private void assertSitemapItem(SitemapItemModelData data, String id, String title) {
        assertEquals(id, data.getId());
        assertEquals(title, data.getTitle());
    }
}