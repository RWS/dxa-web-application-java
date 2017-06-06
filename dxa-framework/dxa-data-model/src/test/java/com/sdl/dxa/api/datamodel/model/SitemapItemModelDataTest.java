package com.sdl.dxa.api.datamodel.model;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Iterator;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SitemapItemModelDataTest {

    @Test
    public void shouldFindSubItemWithUrl() {
        //given
        SitemapItemModelData sitemapItemModelData = new SitemapItemModelData();
        sitemapItemModelData.setUrl("url");

        SitemapItemModelData itemToFind = new SitemapItemModelData();
        itemToFind.setId("id");
        itemToFind.setUrl("path");
        itemToFind.setTitle("title");

        SitemapItemModelData parent = new SitemapItemModelData();
        parent.setId("parent");
        parent.setUrl("parent");
        parent.setTitle("parent");
        parent.setItems(new TreeSet<>(Lists.newArrayList(itemToFind)));

        sitemapItemModelData.setItems(new TreeSet<>(Lists.newArrayList(parent)));

        //when
        SitemapItemModelData found = sitemapItemModelData.findWithUrl("path");
        //TSI-1956
        SitemapItemModelData foundWithSlash = sitemapItemModelData.findWithUrl("path/");

        //then
        assertNotNull(found);
        assertEquals(itemToFind, found);
        assertEquals(itemToFind, foundWithSlash);
    }

    @Test
    public void shouldReturnNullIfNoSubItemFound() {
        //given
        SitemapItemModelData SitemapItemModelData = new SitemapItemModelData();
        SitemapItemModelData.setItems(new TreeSet<>());

        //when
        SitemapItemModelData found = SitemapItemModelData.findWithUrl("path");

        //then
        assertNull(found);
    }

    @Test
    public void shouldSetParent_ToAllChildren_InRightOrder() {
        //given 
        TreeSet<SitemapItemModelData> items = new TreeSet<>();
        SitemapItemModelData child4 = new SitemapItemModelData().setId("1");
        SitemapItemModelData child3 = new SitemapItemModelData().setId("666").setTitle("002 Title");
        SitemapItemModelData child2 = new SitemapItemModelData().setId("667").setTitle("001 Title");
        SitemapItemModelData child1 = new SitemapItemModelData().setId("1").setTitle("001 Title");
        items.add(child4);
        items.add(child3);
        items.add(child2);
        items.add(child1);

        //when
        SitemapItemModelData parent = new SitemapItemModelData().setItems(items);

        //then
        Iterator<SitemapItemModelData> iterator = parent.getItems().iterator();
        assertEquals(child1, iterator.next());
        assertEquals(child2, iterator.next());
        assertEquals(child3, iterator.next());
        assertEquals(child4, iterator.next());
    }
}