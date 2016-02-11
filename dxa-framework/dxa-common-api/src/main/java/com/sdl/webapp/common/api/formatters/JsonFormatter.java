package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;

import javax.servlet.http.HttpServletRequest;

/**
 * Produces the feed in json format
 */
public class JsonFormatter extends BaseFormatter {


    /**
     * <p>Constructor for JsonFormatter.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @param context a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    public JsonFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/json");

    }

    /**
     * {@inheritDoc}
     *
     * Returns the formatted data. Additional model processing can be implemented in extending classes
     */
    @Override
    public Object formatData(Object model) {
        return model;
    }

    /**
     * {@inheritDoc}
     *
     * Not required for Json
     */
    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws Exception {
        throw new Exception("This method shall not be called!");
    }
}
