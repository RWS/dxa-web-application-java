package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Content;


import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces the feed in atom format
 */
public class AtomFormatter extends FeedFormatter {


    public AtomFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/atom+xml");
    }

    /**
     * Gets a syndication Entry from a teaser
     * @param item (@see Teaser)
     * @return
     * @throws URISyntaxException
     */
    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException {
        Entry si = new Entry();
        if (item.getHeadline() != null)
        {
            si.setTitle(item.getHeadline());
        }
        if (item.getText() != null)
        {
            Content c = new Content();
            c.setValue(item.getText().toString());
            si.setSummary(c);
            List<Content> contents = new ArrayList<Content>();
            contents.add(c);
            si.setContents(contents);

        }
        if (item.getLink() != null && item.getLink().getUrl() !=null && item.getLink().getUrl().startsWith("http"))
        {
            List<Link> links = new ArrayList<Link>();
            Link l = new Link();
            l.setHref(item.getLink().getUrl());
        }
        if (item.getDate() != null)
        {

            si.setPublished(item.getDate().toDate());
        }
        return si;
    }

}
