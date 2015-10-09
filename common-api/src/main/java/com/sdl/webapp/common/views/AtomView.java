package com.sdl.webapp.common.views;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.DataFormatter;
import com.sdl.webapp.common.api.model.PageModel;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

import com.sun.syndication.feed.atom.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: TW Document
 */
public class AtomView extends AbstractAtomFeedView {
    private static final Logger LOG = LoggerFactory.getLogger(AtomView.class);
    private DataFormatter formatter;
    WebRequestContext context;
    public AtomView(WebRequestContext context){
        this.context = context;
    }



    @Override
    @SuppressWarnings("unchecked")
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        PageModel page = (PageModel)model.get("data");
        String description = page.getMeta().containsKey("description") ? page.getMeta().get("description") : null;

        feed.setTitle(page.getTitle());
        Content c = new Content();
        c.setValue(description);
        c.setType("text");
        feed.setSubtitle(c);
        feed.setId("my-id");
        StringBuffer uri = request.getRequestURL();
        String queryString = request.getQueryString();
        if(queryString!=null){
            uri.append("?").append(queryString);
        }
        List<Link> links = new ArrayList<>();
        Link l = new Link();
        l.setHref(uri.toString().replaceAll("[&?]format.*?(?=&|\\?|$)", ""));
        l.setTitle(page.getTitle());
        links.add(l);
        feed.setAlternateLinks(links);
        super.buildFeedMetadata(model, feed, request);

    }

    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        this.formatter = (DataFormatter) model.get("formatter");
        return (List<Entry>)formatter.formatData(model.get("data"));
    }
}
