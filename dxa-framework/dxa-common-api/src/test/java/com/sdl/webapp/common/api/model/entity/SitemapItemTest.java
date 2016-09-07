package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
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
    public void shouldRemoveSequenceFromTitle() {
        //given 
        SitemapItem sitemapItem = new SitemapItem();

        //when
        sitemapItem.setTitle("012 Title");

        //then
        assertEquals("Title", sitemapItem.getTitle());
    }

    @Test
    public void shouldRemoveNothingButSequenceFromTitle() {
        //given
        SitemapItem sitemapItem = new SitemapItem();

        //when
        sitemapItem.setTitle("0121 Title");
        String title1 = sitemapItem.getTitle();

        sitemapItem.setTitle("21 Title");
        String title2 = sitemapItem.getTitle();

        sitemapItem.setTitle("Title");
        String title3 = sitemapItem.getTitle();

        sitemapItem.setTitle(null);
        String titleNull = sitemapItem.getTitle();


        //then
        assertEquals("0121 Title", title1);
        assertEquals("21 Title", title2);
        assertEquals("Title", title3);
        assertEquals(null, titleNull);
    }

}