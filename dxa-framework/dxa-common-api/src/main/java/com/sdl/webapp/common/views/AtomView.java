package com.sdl.webapp.common.views;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.DataFormatter;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Feed view for Atom representation of page
 */
public class AtomView extends AbstractAtomFeedView {

    @Autowired
    private WebRequestContext context;

    private DataFormatter formatter;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
        PageModel page = (PageModel) model.get("data");
        String description = page.getMeta().containsKey("description") ? page.getMeta().get("description") : null;

        Content title = new Content();
        title.setType("text");
        title.setValue(page.getTitle());
        feed.setTitleEx(title);

        Content c = new Content();
        c.setValue(description);
        c.setType("text");
        feed.setSubtitle(c);

        feed.setId("uuid:" + UUID.randomUUID().toString());
        StringBuffer uri = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            uri.append('?').append(queryString);
        }
        List<Link> links = new ArrayList<>();
        Link l = new Link();
        l.setHref(uri.toString().replaceAll("[&?]format.*?(?=&|\\?|$)", ""));
        links.add(l);

        feed.setUpdated(new Date());
        feed.setAlternateLinks(links);
        feed.setLanguage(context.getLocalization().getCulture());
        super.buildFeedMetadata(model, feed, request);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        this.formatter = (DataFormatter) model.get("formatter");
        return (List<Entry>) formatter.formatData(model.get("data"));
    }
}
