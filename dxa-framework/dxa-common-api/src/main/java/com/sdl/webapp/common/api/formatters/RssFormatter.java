package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Produces the feed in rss format
 */
public class RssFormatter extends FeedFormatter {


    public RssFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/rss+xml");

    }

    /**
     * Returns the formatted data. Additional model processing can be implemented in extending classes
     *
     * @param model
     * @return
     */
    @Override
    public Object formatData(Object model) {

        return getData(model);
    }

    /**
     * Gets a syndication Entry from a teaser
     *
     * @param item (@see Teaser)
     * @return
     * @throws URISyntaxException
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
