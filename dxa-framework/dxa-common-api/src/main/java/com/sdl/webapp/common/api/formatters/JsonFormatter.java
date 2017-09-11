package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;

import javax.servlet.http.HttpServletRequest;

/**
 * Produces the feed in json format.
 */
public class JsonFormatter extends BaseFormatter {

    public JsonFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this._addMediaType("application/json");
    }

    /**
     * JSON formatter doesn't need any processing and returns model as it is.
     */
    @Override
    public Object formatData(Object model) {
        return model;
    }

    /**
     * JSON formatter doesn't need any processing and returns model as it is.
     */
    @Override
    public Object getSyndicationItem(FeedItem item) {
        throw new UnsupportedOperationException("This method shall not be called!");
    }
}
