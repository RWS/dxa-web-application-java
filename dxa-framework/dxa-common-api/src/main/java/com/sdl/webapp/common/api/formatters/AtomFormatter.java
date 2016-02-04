package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Produces the feed in atom format
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class AtomFormatter extends FeedFormatter {


    /**
     * <p>Constructor for AtomFormatter.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @param context a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    public AtomFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/atom+xml");
    }

    /**
     * {@inheritDoc}
     *
     * Gets a syndication Entry from a teaser
     */
    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException {
        Entry si = new Entry();
        if (item.getHeadline() != null) {
            si.setTitle(item.getHeadline());
        }
        if (item.getText() != null) {
            Content c = new Content();
            c.setValue(item.getText().toString());
            c.setType("text");
            si.setSummary(c);
            si.setId("uuid:" + UUID.randomUUID().toString());
            si.setUpdated(new Date());
        }
        if (item.getLink() != null && item.getLink().getUrl() != null && item.getLink().getUrl().startsWith("http")) {
            List<Link> links = new ArrayList<>();
            Link l = new Link();
            if (item.getLink().getUrl().startsWith("http")) {
                l.setHref(item.getLink().getUrl());
            } else {
                l.setHref(context.getBaseUrl() + item.getLink().getUrl());
            }
            links.add(l);
            si.setOtherLinks(links);

        }
        if (item.getDate() != null) {

            si.setPublished(item.getDate().toDate());
        }
        return si;
    }

}
