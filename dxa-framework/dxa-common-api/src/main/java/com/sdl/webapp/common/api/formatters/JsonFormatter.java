package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Produces the feed in json format
 */
public class JsonFormatter extends BaseFormatter {


    public JsonFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/json");

    }

    /**
     * Returns the formatted data. Additional model processing can be implemented in extending classes
     *
     * @param model
     * @return
     */
    @Override
    public Object formatData(Object model) {
        return model;
    }

    /**
     * Not required for Json
     *
     * @param item
     * @return
     * @throws URISyntaxException
     */
    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws Exception {
        throw new Exception("This method shall not be called!");
    }
}
