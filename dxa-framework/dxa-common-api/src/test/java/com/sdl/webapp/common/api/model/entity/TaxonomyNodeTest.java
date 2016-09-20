package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
}