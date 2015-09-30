package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Teaser;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 9/29/2015.
 */
public class JsonFormatter extends BaseFormatter {


    public JsonFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
        this.addMediaType("application/json");
    }

    @Override
    public Object formatData(Object model) {
        return model;
    }

    @Override
    public Object getSyndicationItemFromTeaser(Teaser item) throws URISyntaxException {
        return null;
    }
}
