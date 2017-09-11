package com.sdl.webapp.common.api.formatters;

import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;

import javax.servlet.http.HttpServletRequest;

/**
 * Produces the feed in RSS format.
 */
public class RssFormatter extends FeedFormatter {

    public RssFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this._addMediaType("application/rss+xml");
    }

    @Override
    public Object getSyndicationItem(FeedItem teaser) {
        Item item = new Item();

        if (teaser.getHeadline() != null) {
            item.setTitle(teaser.getHeadline());
        }

        if (teaser.getSummary() != null) {
            Description description = new Description();
            description.setValue(teaser.getSummary().toString());
            item.setDescription(description);
        }

        if (teaser.getDate() != null) {
            item.setPubDate(teaser.getDate());
        }

        return item;
    }
}
