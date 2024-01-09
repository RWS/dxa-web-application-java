package com.sdl.webapp.common.api.formatters;

import com.rometools.rome.feed.rss.Item;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.model.RichText;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RssFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        RssFormatter rssFormatter = new RssFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(rssFormatter, "mediaTypes");

        //then
        assertTrue(mediaTypes.contains("application/rss+xml"));
    }

    @Test
    public void shouldGetSyndicationFromTeaser() throws URISyntaxException {
        //given
        RssFormatter rssFormatter = new RssFormatter(null, null);
        FeedItem teaser = new FeedItem();
        teaser.setHeadline("headline");
        teaser.setSummary(new RichText("text"));
        Date now = new Date();
        teaser.setDate(now);

        //when
        Item item = (Item) rssFormatter.getSyndicationItem(teaser);

        //then
        assertEquals("headline", item.getTitle());
        assertEquals("text", item.getDescription().getValue());
        assertEquals(now, item.getPubDate());
    }

}