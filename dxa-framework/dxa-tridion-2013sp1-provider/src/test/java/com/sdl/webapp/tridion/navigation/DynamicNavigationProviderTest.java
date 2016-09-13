package com.sdl.webapp.tridion.navigation;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import com.tridion.meta.PageMeta;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.filters.DepthFilter;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
@RunWith(MockitoJUnitRunner.class)
public class DynamicNavigationProviderTest {

    @Mock
    private TaxonomyFactory taxonomyFactory;

    @Mock
    private StaticNavigationProvider staticNavigationProvider;

    @Mock
    private Localization localization;

    @Mock
    private PayloadCacheProvider payloadCacheProvider;

    @InjectMocks
    private DynamicNavigationProvider dynamicNavigationProvider;

    @Before
    public void init() {
        when(localization.getId()).thenReturn("1");
        ReflectionTestUtils.setField(dynamicNavigationProvider, "taxonomyNavigationMarker", "[Taxonomy]");
        ReflectionTestUtils.setField(dynamicNavigationProvider, "sitemapItemTypeTaxonomyNode", "TaxonomyNode");
        ReflectionTestUtils.setField(dynamicNavigationProvider, "sitemapItemTypeStructureGroup", "StructureGroup");
        when(payloadCacheProvider.loadPayloadFromLocalCache(anyString())).thenReturn(new CacheElementImpl<>(null, true));
    }

    @Test
    public void shouldReturnNullOfNoRoot() {
        //given
        when(taxonomyFactory.getTaxonomies(anyString())).thenReturn(new String[]{});

        //when
        String taxonomyId = dynamicNavigationProvider.getNavigationTaxonomyId(localization);

        //then
        verify(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri("1")));
        assertNull(taxonomyId);
    }

    @Test
    public void shouldReturnNullIfNoRootInArray() {
        //given
        when(taxonomyFactory.getTaxonomies(anyString())).thenReturn(new String[]{"withoutMarker", "withoutMarker"});
        Keyword keyword = mockKeyword("taxonomyId", "Name");
        when(taxonomyFactory.getTaxonomyKeyword(anyString())).thenReturn(keyword);


        //when
        String taxonomyId = dynamicNavigationProvider.getNavigationTaxonomyId(localization);

        //then
        verify(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri("1")));
        assertNull(taxonomyId);
    }

    @Test
    public void shouldFindTaxonomyRoot() {
        //given
        when(taxonomyFactory.getTaxonomies(anyString())).thenReturn(new String[]{"withoutMarker", "withMarker"});
        Keyword keyword = mockKeyword("taxonomyId", "Name");
        Keyword keywordRoot = mockKeyword("taxonomyId", "[Taxonomy]Name");
        when(taxonomyFactory.getTaxonomyKeyword(anyString())).thenReturn(keyword);
        when(taxonomyFactory.getTaxonomyKeyword(eq("withMarker"))).thenReturn(keywordRoot);

        //when
        String taxonomyId = dynamicNavigationProvider.getNavigationTaxonomyId(localization);

        //then
        verify(taxonomyFactory).getTaxonomies(eq(TcmUtils.buildPublicationTcmUri("1")));
        assertEquals("taxonomyId", taxonomyId);
    }

    @Test
    public void shouldCreateTaxonomyNode() {
        //given
        Keyword keyword = mockKeyword("10-20", "000 Root");
        when(taxonomyFactory.getTaxonomyKeywords(eq("rootId"), any(DepthFilter.class))).thenReturn(keyword);
        Keyword child1 = mockKeyword("1-2", "child1");
        when(child1.getKeywordURI()).thenReturn("12-13");
        Keyword child11 = mockKeyword("5-6", "child11");
        when(child1.getKeywordChildren()).thenReturn(Collections.singletonList(child11));
        Keyword child2 = mockKeyword("3-4", "child2");
        when(keyword.getKeywordChildren()).thenReturn(Lists.newArrayList(child1, child2));

        //when
        SitemapItem item = dynamicNavigationProvider.createTaxonomyNode("rootId", localization);


        //then
        verify(taxonomyFactory).getTaxonomyKeywords(eq("rootId"), any(DepthFilter.class));
        verify(keyword).getKeywordChildren();
        verify(keyword, atLeastOnce()).getReferencedContentCount();
        verify(child1).getKeywordChildren();
        verify(child2).getKeywordChildren();
        verify(child11).getKeywordChildren();

        assertTrue(item instanceof TaxonomyNode);
        TaxonomyNode node = (TaxonomyNode) item;
        assertEquals("t20", node.getId());
        assertEquals("TaxonomyNode", node.getType());
        assertEquals("Root", node.getTitle());
        assertEquals("keywordKey", node.getKey());
        assertEquals("keywordDesc", node.getDescription());
        assertTrue(node.isTaxonomyAbstract());
        assertEquals(0, node.getClassifiedItemsCount());

        Iterator<SitemapItem> items = node.getItems().iterator();
        SitemapItem item1 = items.next();
        assertEquals("child1", item1.getTitle());
        assertEquals("t2-k13", item1.getId());
        assertEquals("TaxonomyNode", item1.getType());
        assertEquals("child11", item1.getItems().get(0).getTitle());
        assertEquals("child2", items.next().getTitle());
    }

    @Test
    public void shouldConvertToPageMetaDTO() {
        //given
        PageMeta pageMeta = mock(PageMeta.class);
        when(pageMeta.getId()).thenReturn(1);
        when(pageMeta.getTitle()).thenReturn("title");
        when(pageMeta.getURLPath()).thenReturn("url");

        //when
        Object toDto = ReflectionTestUtils.invokeMethod(dynamicNavigationProvider, "toDto", pageMeta);

        //then
        assertTrue(toDto instanceof PageMetaDTO);
        PageMetaDTO dto = (PageMetaDTO) toDto;
        assertEquals(1, dto.getId());
        assertEquals("title", dto.getTitle());
        assertEquals("url", dto.getUrl());
    }

    private Keyword mockKeyword(String taxonomyURI, String name) {
        Keyword keyword = mock(Keyword.class);
        when(keyword.getTaxonomyURI()).thenReturn(taxonomyURI);
        when(keyword.getKeywordName()).thenReturn(name);
        when(keyword.getKeywordURI()).thenReturn(taxonomyURI);
        when(keyword.getKeywordKey()).thenReturn("keywordKey");
        when(keyword.getKeywordDescription()).thenReturn("keywordDesc");
        when(keyword.isKeywordAbstract()).thenReturn(true);
        return keyword;
    }

}