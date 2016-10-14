package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

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
    public void shouldSortElementsAccordinglyWithOriginalTitles() {
        //given
        SitemapItem item = new TaxonomyNode();

        SitemapItem first = new SitemapItem();
        SitemapItem second = new SitemapItem();
        SitemapItem third = new SitemapItem();
        SitemapItem fourth = new SitemapItem();

        first.setTitle("bbb");
        second.setTitle("ccc");
        //title is changed but it already should not influence on sorting
        second.setTitle("aaa");
        third.setTitle("zzz");
        third.setId("1");
        //if titles are the same, sort by id
        fourth.setTitle("zzz");
        fourth.setId("2");

        item.setItems(Lists.newArrayList(fourth, third, second, first));

        //when
        List<SitemapItem> items = item.getItems();

        //then
        Iterator<SitemapItem> iterator = items.iterator();
        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
        assertEquals(third, iterator.next());
        assertEquals(fourth, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldOnlySortTaxonomyNodeItems() {
        //given 
        SitemapItem item = new SitemapItem();

        SitemapItem first = new SitemapItem();
        SitemapItem second = new SitemapItem();

        first.setTitle("bbb");
        second.setTitle("aaa");

        item.setItems(Lists.newArrayList(first, second));

        //when
        List<SitemapItem> items = item.getItems();

        //then
        Iterator<SitemapItem> iterator = items.iterator();
        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
    }

    @Test
    public void shouldWrapItemsInSortedSet() {
        //when
        Set<SitemapItem> set = new TaxonomyNode().wrapItems(Collections.<SitemapItem>emptySet());
        Set<SitemapItem> set2 = new TaxonomyNode().wrapItems(null);
        Set<SitemapItem> set3 = new TaxonomyNode().wrapItems(Lists.newArrayList(new SitemapItem()));

        //then
        assertTrue(SortedSet.class.isAssignableFrom(set.getClass()) && set.isEmpty());
        assertTrue(SortedSet.class.isAssignableFrom(set2.getClass()) && set2.isEmpty());
        assertTrue(SortedSet.class.isAssignableFrom(set2.getClass()) && !set3.isEmpty());
    }
}