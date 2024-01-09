package com.sdl.webapp.common.api.formatters;

import com.rometools.rome.feed.atom.Entry;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AtomFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        AtomFormatter atomFormatter = new AtomFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(atomFormatter, "mediaTypes");

        //then
        assertTrue(mediaTypes.contains("application/atom+xml"));
    }

    @Test
    public void shouldGetSyndicationFromTeaser() throws URISyntaxException {
        //given
        WebRequestContext webRequestContext = mock(WebRequestContext.class);
        lenient().when(webRequestContext.getBaseUrl()).thenReturn("base_url/");

        AtomFormatter atomFormatter = new AtomFormatter(null, webRequestContext);
        FeedItem teaser = new FeedItem();
        teaser.setHeadline("headline");
        teaser.setSummary(new RichText("text!"));
        Date now = new Date();
        teaser.setDate(now);
        Link link = new Link();
        teaser.setLink(link);
        link.setUrl("http://url");

        //when
        Entry entryLinkHttp = (Entry) atomFormatter.getSyndicationItem(teaser);

        //then
        assertEquals("headline", entryLinkHttp.getTitle());
        assertEquals("text!", entryLinkHttp.getSummary().getValue());
        assertEquals("text", entryLinkHttp.getSummary().getType());
        assertNotNull(entryLinkHttp.getId());
        assertTrue(entryLinkHttp.getId().startsWith("uuid:"));
        assertNotNull(entryLinkHttp.getUpdated());
        assertEquals(now, entryLinkHttp.getPublished());

        com.rometools.rome.feed.atom.Link linkHttp = entryLinkHttp.getOtherLinks().get(0);
        assertEquals("http://url", linkHttp.getHref());

        //when
        link.setUrl("url2");
        Entry entryLinkNoHttp = (Entry) atomFormatter.getSyndicationItem(teaser);

        //then
        com.rometools.rome.feed.atom.Link linkNoHttp = entryLinkNoHttp.getOtherLinks().get(0);
        assertEquals("base_url/url2", linkNoHttp.getHref());

    }

}