package com.sdl.dxa.tridion.navigation;


import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.SitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.TaxonomySitemapItem;
import com.sdl.web.pca.client.exception.ApiClientException;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.sdl.web.pca.client.contentmodel.generated.Ancestor.INCLUDE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLRestDynamicNavigationModelProviderTest {
    private static final TaxonomyNodeModelData[] EMPTY = new TaxonomyNodeModelData[0];
    private static final int CLASSIFIED_ITEMS_COUNT = 5;
    private static final String DESCRIPTION = "Description";
    private static final String ID = "Id";
    private static final String KEY = "key";
    private static final String ORIGINAL_TITLE = "originalTitle";
    private static final String PUBLISHED_DATE = "2018-06-25T14:01:16.95+03:00";
    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final boolean ABSTRACT = true;
    private static final boolean HAS_CHILD_NODES = true;
    private static final boolean VISIBLE = true;
    private static final int LOCALIZATION_ID = 1234;
    private static final int DEPTH_COUNTER = 9;
    private static final String SITEMAP_ID = "sitemap Id";

    @Mock
    private ApiClientProvider clientProvider;

    @Mock
    private ApiClient pcaClient;

    @Spy
    @InjectMocks
    private GraphQLRestDynamicNavigationModelProvider provider;

    private SitemapRequestDto requestDto;

    @Before
    public void setUp() {
        when(clientProvider.getClient()).thenReturn(pcaClient);
        NavigationFilter withAncestorsFilter = new NavigationFilter();
        withAncestorsFilter.setWithAncestors(true);
        requestDto = SitemapRequestDto
                .builder(LOCALIZATION_ID)
                .expandLevels(new DepthCounter(DEPTH_COUNTER))
                .sitemapId(SITEMAP_ID)
                .navigationFilter(withAncestorsFilter)
                .build();
    }

    private void verifyCreatedObject(TaxonomyNodeModelData target, boolean verifyBooleanFields, boolean verifyChildren) {
        assertEquals(CLASSIFIED_ITEMS_COUNT, target.getClassifiedItemsCount());
        assertEquals(DESCRIPTION + ID, target.getDescription());
        assertEquals(ID, target.getId());
        assertEquals(KEY + ID, target.getKey());
        assertEquals(ORIGINAL_TITLE + ID, target.getOriginalTitle());
        assertEquals(DateTime.parse(PUBLISHED_DATE), target.getPublishedDate());
        assertEquals(TITLE + ID, target.getTitle());
        assertEquals(TYPE + ID, target.getType());
        assertEquals(URL + ID, target.getUrl());
        if (verifyBooleanFields) {
            assertTrue(target.isTaxonomyAbstract());
            assertTrue(target.isWithChildren());
            assertTrue(target.isVisible());
        }
        if (verifyChildren) {
            assertEquals(3, target.getItems().size());
            assertEquals(ID + "_" + ID + "_1", target.getItems().first().getId());
            assertEquals(0, target.getItems().first().getItems().size());
            assertEquals(ID + "_" + ID + "_3", target.getItems().last().getId());
            assertEquals(1, target.getItems().last().getItems().size());
            assertEquals(ID + "_" + ID + "_" + ID + "_1", target.getItems().last().getItems().first().getId());
        }
    }

    @NotNull
    private TaxonomySitemapItem createTaxonomySitemapItem(@NonNull String id, boolean withChildren) {
        //create a source object to copy. it has 3 children, last of them has its own child
        TaxonomySitemapItem source = new TaxonomySitemapItem();
        source.setClassifiedItemsCount(CLASSIFIED_ITEMS_COUNT);
        source.setDescription(DESCRIPTION + id);
        source.setId(id);
        source.setKey(KEY + id);
        source.setOriginalTitle(ORIGINAL_TITLE + id);
        source.setPublishedDate(PUBLISHED_DATE);
        source.setTitle(TITLE + id);
        source.setType(TYPE + id);
        source.setUrl(URL + id);
        source.setAbstract(ABSTRACT);
        source.setHasChildNodes(HAS_CHILD_NODES);
        source.setVisible(VISIBLE);
        if (!withChildren) return source;

        List<SitemapItem> items = new ArrayList<>();
        source.setItems(items);
        items.add(createTaxonomySitemapItem(id + "_" + id + "_1", false));
        items.add(createTaxonomySitemapItem(id + "_" + id + "_2", false));
        TaxonomySitemapItem thirdChild = createTaxonomySitemapItem(id + "_" + id + "_3", false);
        items.add(thirdChild);
        List<SitemapItem> itemsForThirdChild = new ArrayList<>();
        itemsForThirdChild.add(createTaxonomySitemapItem(id + "_" + id + "_" + id + "_1", false));
        thirdChild.setItems(itemsForThirdChild);

        return source;
    }

    @Test
    public void checkFieldsInConvert() {
        TaxonomySitemapItem source = createTaxonomySitemapItem(ID, true);

        TaxonomyNodeModelData target = provider.convert(source);

        verifyCreatedObject(target, true, true);
    }

    @Test
    public void checkVisibleFieldsInConvert() {
        TaxonomySitemapItem source = createTaxonomySitemapItem(ID, true);
        source.setVisible(false);

        TaxonomyNodeModelData target = provider.convert(source);

        verifyCreatedObject(target, false, true);
        assertTrue(target.isTaxonomyAbstract());
        assertTrue(target.isWithChildren());
        assertFalse(target.isVisible());
    }

    @Test
    public void checkAbstractFieldsInConvert() {
        TaxonomySitemapItem source = createTaxonomySitemapItem(ID, true);
        source.setAbstract(false);

        TaxonomyNodeModelData target = provider.convert(source);

        verifyCreatedObject(target, false, true);
        assertFalse(target.isTaxonomyAbstract());
        assertTrue(target.isWithChildren());
        assertTrue(target.isVisible());
    }

    @Test
    public void checkWithChildrenFieldsInConvert() {
        TaxonomySitemapItem source = createTaxonomySitemapItem(ID, true);
        source.setHasChildNodes(false);

        TaxonomyNodeModelData target = provider.convert(source);

        verifyCreatedObject(target, false, true);
        assertTrue(target.isTaxonomyAbstract());
        assertFalse(target.isWithChildren());
        assertTrue(target.isVisible());
    }

    @Test
    public void getNavigationModel() {
        doReturn(createTaxonomySitemapItem(ID, false)).when(pcaClient).getSitemap(eq(ContentNamespace.Sites),
                eq(LOCALIZATION_ID), eq(DEPTH_COUNTER), any(ContextData.class));

        TaxonomyNodeModelData result = provider.getNavigationModel(requestDto).get();

        verify(pcaClient).getSitemap(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID), eq(DEPTH_COUNTER), any(ContextData.class));
        verifyCreatedObject(result, true, false);
    }

    @Test
    public void getNavigationModelException() {
        doThrow(new ApiClientException()).when(pcaClient).getSitemap(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID),
                eq(DEPTH_COUNTER), any(ContextData.class));

        assertFalse(provider.getNavigationModel(requestDto).isPresent());

        verify(pcaClient).getSitemap(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID), eq(DEPTH_COUNTER), any(ContextData.class));
        verify(provider, never()).convert(any(TaxonomySitemapItem.class));
    }

    @Test
    public void getNavigationSubtree() {
        TaxonomySitemapItem[] result = new TaxonomySitemapItem[]{createTaxonomySitemapItem(ID, true)};
        doReturn(result).when(pcaClient).getSitemapSubtree(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID), eq(SITEMAP_ID),
                eq(DEPTH_COUNTER), eq(INCLUDE), any(ContextData.class));

        Collection<SitemapItemModelData> sitemapItemModelData = provider.getNavigationSubtree(requestDto).get();

        verifyCreatedObject(sitemapItemModelData.toArray(EMPTY)[0], true, true);
        verify(pcaClient).getSitemapSubtree(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID), eq(SITEMAP_ID), eq(DEPTH_COUNTER),
                eq(INCLUDE), any(ContextData.class));
        verify(provider, times(5)).convert(any(TaxonomySitemapItem.class));
    }

    @Test
    public void getNavigationSubtreeException() {
        doThrow(new ApiClientException()).when(pcaClient).getSitemapSubtree(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID),
                eq(SITEMAP_ID), eq(DEPTH_COUNTER), eq(INCLUDE), any(ContextData.class));

        assertFalse(provider.getNavigationSubtree(requestDto).isPresent());

        verify(pcaClient).getSitemapSubtree(eq(ContentNamespace.Sites), eq(LOCALIZATION_ID), eq(SITEMAP_ID), eq(DEPTH_COUNTER),
                eq(INCLUDE), any(ContextData.class));
    }
}