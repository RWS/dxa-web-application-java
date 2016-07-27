package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sun.syndication.feed.atom.Entry;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AtomFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        AtomFormatter atomFormatter = new AtomFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(atomFormatter, "_mediaTypes");

        //then
        assertTrue(mediaTypes.contains("application/atom+xml"));
    }

    @Test
    public void shouldGetSyndicationFromTeaser() throws URISyntaxException {
        //given
        WebRequestContext webRequestContext = mock(WebRequestContext.class);
        when(webRequestContext.getBaseUrl()).thenReturn("base_url/");

        AtomFormatter atomFormatter = new AtomFormatter(null, webRequestContext);
        Teaser teaser = new Teaser();
        teaser.setHeadline("headline");
        teaser.setText(new RichText("text!"));
        DateTime now = DateTime.now();
        teaser.setDate(now);
        Link link = new Link();
        teaser.setLink(link);
        link.setUrl("http://url");

        //when
        Entry entryLinkHttp = (Entry) atomFormatter.getSyndicationItemFromTeaser(teaser);

        //then
        assertEquals("headline", entryLinkHttp.getTitle());
        assertEquals("text!", entryLinkHttp.getSummary().getValue());
        assertEquals("text", entryLinkHttp.getSummary().getType());
        assertNotNull(entryLinkHttp.getId());
        assertTrue(entryLinkHttp.getId().startsWith("uuid:"));
        assertNotNull(entryLinkHttp.getUpdated());
        assertEquals(now.toDate(), entryLinkHttp.getPublished());

        com.sun.syndication.feed.atom.Link linkHttp = (com.sun.syndication.feed.atom.Link) entryLinkHttp.getOtherLinks().get(0);
        assertEquals("http://url", linkHttp.getHref());

        //when
        link.setUrl("url2");
        Entry entryLinkNoHttp = (Entry) atomFormatter.getSyndicationItemFromTeaser(teaser);

        //then
        com.sun.syndication.feed.atom.Link linkNoHttp = (com.sun.syndication.feed.atom.Link) entryLinkNoHttp.getOtherLinks().get(0);
        assertEquals("base_url/url2", linkNoHttp.getHref());

    }

}