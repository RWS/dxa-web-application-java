package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndFeed;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Administrator on 9/29/2015.
 */
public interface DataFormatter {
    double score(HttpServletRequest request);
    Object formatData(Object model);
    boolean isProcessModel();
    boolean isAddIncludes();
    List<String> getValidTypes(HttpServletRequest request, List<String> allowedTypes);
    Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException;
}
