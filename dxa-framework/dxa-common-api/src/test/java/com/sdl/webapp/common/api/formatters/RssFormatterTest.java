package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.model.RichText;
import com.sun.syndication.feed.rss.Item;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RssFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        RssFormatter rssFormatter = new RssFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(rssFormatter, "_mediaTypes");

        //then
        assertTrue(mediaTypes.contains("application/rss+xml"));
    }

    @Test
    public void shouldGetSyndicationFromTeaser() throws URISyntaxException {
        //given
        RssFormatter rssFormatter = new RssFormatter(null, null);
        Teaser teaser = new Teaser();
        teaser.setHeadline("headline");
        teaser.setText(new RichText("text"));
        DateTime now = DateTime.now();
        teaser.setDate(now);

        //when
        Item item = (Item) rssFormatter.getSyndicationItemFromTeaser(teaser);

        //then
        assertEquals("headline", item.getTitle());
        assertEquals("text", item.getDescription().getValue());
        assertEquals(now.toDate(), item.getPubDate());
    }

}