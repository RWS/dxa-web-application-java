package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.FeedFormatter;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Item;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Administrator on 9/29/2015.
 */
public class RssFormatter extends FeedFormatter {


    public RssFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/rss+xml");

    }

    @Override
    public Object formatData(Object model) {

        return getData(model);
    }

    public Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException {
        Item si = new Item();
        if (item.getHeadline() != null)
        {
            si.setTitle(item.getHeadline());
        }
        if (item.getText() != null)
        {
            Content c = new Content();
            c.setValue(item.getText().toString());
            si.setContent(c);
        }
        if (item.getLink() != null && item.getLink().getUrl() !=null && item.getLink().getUrl().startsWith("http"))
        {
            si.setLink(item.getLink().getUrl());
        }
        if (item.getDate() != null)
        {

            si.setPubDate(item.getDate().toDate());
        }
        return si;
    }
}
