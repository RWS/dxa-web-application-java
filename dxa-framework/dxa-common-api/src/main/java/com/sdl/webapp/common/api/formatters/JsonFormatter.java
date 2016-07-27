package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.WebRequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Produces the feed in json format.
 */
public class JsonFormatter extends BaseFormatter {

    public JsonFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/json");
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
     *
     * @throws Exception always throws {@link UnsupportedOperationException}
     */
    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws Exception {
        throw new UnsupportedOperationException("This method shall not be called!");
    }
}
