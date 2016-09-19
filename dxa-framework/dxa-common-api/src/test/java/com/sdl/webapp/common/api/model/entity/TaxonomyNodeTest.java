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
    public void shouldPutHomeElementAtFirstPlaceWhenSorting() {
        //given
        SitemapItem item = new TaxonomyNode();

        SitemapItem first = new SitemapItem();
        SitemapItem second = new SitemapItem();
        SitemapItem home = new SitemapItem();
        first.setTitle("aaa");
        second.setTitle("bbb");
        home.setTitle("zzz");
        home.setHome(true);

        item.setItems(Lists.newArrayList(first, home, second));

        //when
        List<SitemapItem> items = item.getItems();

        //then
        Iterator<SitemapItem> iterator = items.iterator();
        assertEquals(home, iterator.next());
        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
        assertFalse(iterator.hasNext());
    }
}