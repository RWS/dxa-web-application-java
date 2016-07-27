package com.sdl.webapp.common.api.formatters;

import com.google.common.collect.Lists;
import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

/**
 * Produces the feed in ATOM format.
 */
public class AtomFormatter extends FeedFormatter {

    public AtomFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/atom+xml");
    }

    @Override
    public Object getSyndicationItemFromTeaser(@NotNull Teaser teaser) throws URISyntaxException {
        Entry entry = new Entry();

        if (teaser.getHeadline() != null) {
            entry.setTitle(teaser.getHeadline());
        }

        if (teaser.getText() != null) {
            Content content = new Content();
            content.setValue(teaser.getText().toString());
            content.setType("text");

            entry.setSummary(content);
            entry.setId("uuid:" + UUID.randomUUID().toString());
            entry.setUpdated(new Date());
        }

        if (teaser.getLink() != null) {
            String url = teaser.getLink().getUrl();
            if (url != null) {
                Link link = new Link();
                link.setHref(url.startsWith("http") ? url : context.getBaseUrl() + url);

                entry.setOtherLinks(Lists.newArrayList(link));
            }
        }

        if (teaser.getDate() != null) {
            entry.setPublished(teaser.getDate().toDate());
        }

        return entry;
    }

}
