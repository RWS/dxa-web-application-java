package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.model.sorting.SortableSiteMap;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TaxonomyNodeTest extends SitemapItemTest {

    @Test
    public void shouldAddDescriptionIfLinkIsNotNull() {
        //given
        TaxonomyNode node = new TaxonomyNode();
        node.setUrl("url");
        node.setTitle("title");
        node.setDescription("desc");

        //when
        Link link = node.createLink(linkResolver, localization);

        //then
        assertEquals("resolved", link.getUrl());
        assertEquals("title", link.getLinkText());
        assertEquals("desc", link.getAlternateText());
    }

    @Test
    public void shouldSortElementsAccordingToTitles() {
        //given
        ArrayList<SitemapItem> entries = createSitemapItems(true);

        //when
        Collection<SitemapItem> sorted = SortableSiteMap.sortItem(entries, SortableSiteMap.SORT_BY_TITLE_AND_ID);

        //then
        Iterator<SitemapItem> iterator = sorted.iterator();
        assertEquals(entries.get(1), iterator.next());
        assertEquals(entries.get(0), iterator.next());
        assertEquals(entries.get(3), iterator.next());
        assertEquals(entries.get(2), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldSortElementsAccordingTaxonomy() {
        //given
        ArrayList<SitemapItem> entries = createSitemapItems(false);

        //when
        Collection<SitemapItem> sorted = SortableSiteMap.sortItem(entries, SortableSiteMap.SORT_BY_TAXONOMY_AND_KEYWORD);

        //then
        Iterator<SitemapItem> iterator = sorted.iterator();
        assertEquals(entries.get(3), iterator.next());
        assertEquals(entries.get(2), iterator.next());
        assertEquals(entries.get(1), iterator.next());
        assertEquals(entries.get(0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @NotNull
    private ArrayList<SitemapItem> createSitemapItems(boolean titleOrId) {
        SitemapItem first = new SitemapItem();
        SitemapItem second = new SitemapItem();
        SitemapItem third = new SitemapItem();
        SitemapItem fourth = new SitemapItem();
        if (titleOrId) {
            first.setTitle("bbb");
            second.setTitle("aaa");
            third.setTitle("zzz");
            third.setId("2");
            //if titles are the same, sort by id
            fourth.setTitle("zzz");
            fourth.setId("1");
        } else {
            first.setId("t3-k11");
            second.setId("t3-k9");
            third.setId("t2-k7");
            fourth.setId("t1-k5");
        }
        return Lists.newArrayList(first, second, third, fourth);
    }

    @Test
    public void shouldOnlySortTaxonomyNodeItems() {
        //given 
        SitemapItem item = new SitemapItem();

        SitemapItem first = new SitemapItem();
        SitemapItem second = new SitemapItem();

        first.setTitle("bbb");
        second.setTitle("aaa");

        item.setItems(new LinkedHashSet<>(Lists.newArrayList(first, second)));

        //when
        Set<SitemapItem> items = item.getItems();

        //then
        Iterator<SitemapItem> iterator = items.iterator();
        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
    }

    @Test
    public void shouldWrapItemsInSortedSet() {
        //when
        Set<SitemapItem> set = new TaxonomyNode().wrapItems(new LinkedHashSet<>());
        Set<SitemapItem> set2 = new TaxonomyNode().wrapItems(null);
        Set<SitemapItem> set3 = new TaxonomyNode().wrapItems(new LinkedHashSet<>(Lists.newArrayList(new SitemapItem())));

        //then
        assertTrue(LinkedHashSet.class.isAssignableFrom(set.getClass()) && set.isEmpty());
        assertTrue(LinkedHashSet.class.isAssignableFrom(set2.getClass()) && set2.isEmpty());
        assertTrue(LinkedHashSet.class.isAssignableFrom(set2.getClass()) && !set3.isEmpty());
    }
}