package com.sdl.dxa.api.datamodel.model;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SitemapItemModelDataTest {

    @Test
    public void shouldFindSubItemWithUrl() {
        //given
        SitemapItemModelData SitemapItemModelData = new SitemapItemModelData();
        SitemapItemModelData.setUrl("url");

        SitemapItemModelData itemToFind = new SitemapItemModelData();
        itemToFind.setId("id");
        itemToFind.setUrl("path");
        itemToFind.setTitle("title");

        SitemapItemModelData parent = new SitemapItemModelData();
        parent.setId("parent");
        parent.setUrl("parent");
        parent.setTitle("parent");
        parent.setItems(new TreeSet<>(Lists.newArrayList(itemToFind)));

        SitemapItemModelData.setItems(new TreeSet<>(Lists.newArrayList(parent)));

        //when
        SitemapItemModelData found = SitemapItemModelData.findWithUrl("path");
        //TSI-1956
        SitemapItemModelData foundWithSlash = SitemapItemModelData.findWithUrl("path/");

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
}