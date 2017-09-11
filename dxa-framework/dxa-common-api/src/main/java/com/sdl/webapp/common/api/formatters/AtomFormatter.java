package com.sdl.webapp.common.api.formatters;

import com.google.common.collect.Lists;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;


/**
 * Produces the feed in ATOM format.
 */
public class AtomFormatter extends FeedFormatter {

    public AtomFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this._addMediaType("application/atom+xml");
    }

    @Override
    public Object getSyndicationItem(FeedItem feedItem) {
        Entry entry = new Entry();

        if (feedItem.getHeadline() != null) {
            entry.setTitle(feedItem.getHeadline());
        }

        if (feedItem.getSummary() != null) {
            Content content = new Content();
            content.setValue(feedItem.getSummary().toString());
            content.setType("text");

            entry.setSummary(content);
            entry.setId("uuid:" + UUID.randomUUID().toString());
            entry.setUpdated(new Date());
        }

        if (feedItem.getLink() != null) {
            String url = feedItem.getLink().getUrl();
            if (url != null) {
                Link link = new Link();
                link.setHref(url.startsWith("http") ? url : context.getBaseUrl() + url);

                entry.setOtherLinks(Lists.newArrayList(link));
            }
        }

        if (feedItem.getDate() != null) {
            entry.setPublished(feedItem.getDate());
        }

        return entry;
    }

}
