package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.dto.FeedItem;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Produces the feed in RSS format.
 */
public class RssFormatter extends FeedFormatter {

    public RssFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/rss+xml");
    }

    @Override
    public Object getSyndicationItem(FeedItem teaser) throws URISyntaxException {
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
