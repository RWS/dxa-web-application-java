package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SitemapItemTest {

    @Mock
    protected Localization localization;

    @Mock
    protected LinkResolver linkResolver;

    @BeforeEach
    public void init() {
        lenient().when(localization.getId()).thenReturn("1");
        lenient().when(linkResolver.resolveLink(anyString(), anyString())).thenReturn("resolved");
    }

    @Test
    public void shouldCreateALinkFromSiteMap() {
        //given
        SitemapItem item = new SitemapItem();
        item.setUrl("url");
        item.setId("id");
        item.setTitle("title");

        //when
        Link link = item.createLink(linkResolver, localization);

        //then
        verify(linkResolver).resolveLink(eq("url"), eq("1"));
        assertEquals("resolved", link.getUrl());
        assertEquals("id", link.getId());
        assertEquals("title", link.getLinkText());
    }

    @Test
    public void shouldSetParentForItems() {
        //given
        SitemapItem sitemapItem = new SitemapItem();
        SitemapItem child = new SitemapItem();

        //when
        sitemapItem.setItems(new LinkedHashSet<>(Lists.newArrayList(child, null)));

        //then
        assertEquals(sitemapItem, child.getParent());
    }

    @Test
    public void shouldSetParentForAddOneItem() {
        //given
        SitemapItem sitemapItem = new SitemapItem();
        SitemapItem child = new SitemapItem();

        //when
        sitemapItem.addItem(child);

        //then
        assertEquals(sitemapItem, child.getParent());
    }

    @Test
    public void shouldSetOriginalTitleWhenSettingTitle() {
        //given
        SitemapItem sitemapItem = new SitemapItem();

        //when
        sitemapItem.setTitle("title");

        //then
        assertEquals("title", sitemapItem.getOriginalTitle());

        //when
        sitemapItem.setTitle("another title");

        //then original title wasn't changed
        assertEquals("title", sitemapItem.getOriginalTitle());
    }

    @Test
    public void shouldWrapCollectionInSet() {
        //when
        Set<SitemapItem> set1 = new SitemapItem().wrapItems(null);
        Set<SitemapItem> set2 = new SitemapItem().wrapItems(new LinkedHashSet<>(Lists.newArrayList(new SitemapItem())));

        //then
        assertTrue(set1.isEmpty());
        assertFalse(set2.isEmpty());
    }

    @Test
    public void shouldRemoveItemFromItemsCollection() {
        //given 
        SitemapItem item = new SitemapItem();
        SitemapItem item1 = new SitemapItem();
        SitemapItem item2 = new SitemapItem();
        item1.setId("1");
        item2.setId("2");

        item.addItem(item1);
        item.addItem(item2);

        //then
        assertTrue(item.getItems().size() == 2);

        //when
        item.removeItem(item2);

        //then
        assertTrue(item.getItems().size() == 1);
        assertTrue(item.getItems().contains(item1));
    }
}