package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Produces the feed in rss format
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class RssFormatter extends FeedFormatter {


    /**
     * <p>Constructor for RssFormatter.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @param context a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    public RssFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/rss+xml");

    }

    /**
     * {@inheritDoc}
     *
     * Returns the formatted data. Additional model processing can be implemented in extending classes
     */
    @Override
    public Object formatData(Object model) {

        return getData(model);
    }

    /**
     * {@inheritDoc}
     *
     * Gets a syndication Entry from a teaser
     */
    public Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException {
        Item si = new Item();
        if (item.getHeadline() != null) {
            si.setTitle(item.getHeadline());
        }
        if (item.getText() != null) {
            Description d = new Description();
            d.setValue(item.getText().toString());
            si.setDescription(d);
        }
        if (item.getDate() != null) {

            si.setPubDate(item.getDate().toDate());
        }
        return si;
    }
}
