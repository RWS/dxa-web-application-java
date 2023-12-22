package com.sdl.webapp.common.views;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.DataFormatter;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Feed view for RSS representation of page
 */
public class RssView extends AbstractRssFeedView {

    @Autowired
    private WebRequestContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        DataFormatter formatter = (DataFormatter) model.get("formatter");
        return (List<Item>) formatter.formatData(model.get("data"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
        PageModel page = (PageModel) model.get("data");
        String description = page.getMeta().containsKey("description") ? page.getMeta().get("description") : null;
        feed.setTitle(page.getTitle());
        feed.setDescription(description);
        feed.setLanguage(context.getLocalization().getCulture());
        StringBuffer uri = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            uri.append('?').append(queryString);
        }
        feed.setLink(uri.toString().replaceAll("[&?]format.*?(?=&|\\?|$)", ""));
        super.buildFeedMetadata(model, feed, request);

    }
}
