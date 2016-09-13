package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SitemapItemTest {

    @Mock
    protected Localization localization;

    @Mock
    protected LinkResolver linkResolver;

    @Before
    public void init() {
        when(localization.getId()).thenReturn("1");
        when(linkResolver.resolveLink(anyString(), anyString())).thenReturn("resolved");
    }

    @Test
    public void shouldCreateALinkFromSiteMap() {
        //given
        SitemapItem item = new SitemapItem();
        item.setUrl("url");
        item.setTitle("title");

        //when
        Link link = item.createLink(linkResolver, localization);

        //then
        verify(linkResolver).resolveLink(eq("url"), eq("1"));
        assertEquals("resolved", link.getUrl());
        assertEquals("title", link.getLinkText());
    }

    @Test
    public void shouldFindSubItemWithUrl() {
        //given 
        SitemapItem sitemapItem = new SitemapItem();
        sitemapItem.setUrl("url");
        SitemapItem itemToFind = new SitemapItem();
        itemToFind.setUrl("path");
        itemToFind.setTitle("title");
        SitemapItem parent = new SitemapItem();
        parent.setUrl("parent");
        parent.setItems(Lists.newArrayList(itemToFind));
        sitemapItem.setItems(Lists.newArrayList(parent));

        //when
        SitemapItem found = sitemapItem.findWithUrl("path");

        //then
        assertNotNull(found);
        assertEquals(itemToFind, found);
    }

    @Test
    public void shouldReturnNullIfNoSubItemFound() {
        //given
        SitemapItem sitemapItem = new SitemapItem();
        sitemapItem.setItems(Collections.<SitemapItem>emptyList());

        //when
        SitemapItem found = sitemapItem.findWithUrl("path");

        //then
        assertNull(found);
    }

    @Test
    public void shouldSetParentForItems() {
        //given
        SitemapItem sitemapItem = new SitemapItem();
        SitemapItem child = new SitemapItem();

        //when
        sitemapItem.setItems(Lists.newArrayList(child));

        //then
        assertEquals(sitemapItem, child.getParent());
    }

}