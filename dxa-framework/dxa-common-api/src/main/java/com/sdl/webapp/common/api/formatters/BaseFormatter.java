package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formats.DefaultDataFormatter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseFormatter with base logic to be used by the format specific formatters
 */
public abstract class BaseFormatter implements DataFormatter {
    private final List<String> _mediaTypes = new ArrayList<>();
    WebRequestContext context;
    HttpServletRequest request;

    /**
     * <p>Constructor for BaseFormatter.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @param context a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    public BaseFormatter(HttpServletRequest request, WebRequestContext context) {
        this.context = context;
        this.request = request;

    }

    /**
     * <p>addMediaType.</p>
     *
     * @param mediaType a {@link java.lang.String} object.
     */
    public void addMediaType(String mediaType) {
        _mediaTypes.add(mediaType);
    }


    /**
     * Gets the score depending on the media type
     *
     * @return a double.
     */
    public double score() {
        double score = 0.0;
        List<String> validTypes = getValidTypes(_mediaTypes);
        if (validTypes != null && !validTypes.isEmpty()) {
            for (String type : validTypes) {
                double thisScore = DefaultDataFormatter.getScoreFromAcceptString(type);
                if (thisScore > score) {
                    score = thisScore;
                }
            }
        }
        return score;
    }

    /**
     * {@inheritDoc}
     *
     * Gets the valid mediat types depending on the allowed types by the formatter
     */
    public List<String> getValidTypes(List<String> allowedTypes) {
        List<String> res = new ArrayList<>();
        String requestHeader = request.getHeader("Accept");
        if (requestHeader != null) {
            String[] acceptTypes = requestHeader.split(",");
            for (String type : acceptTypes) {
                for (String mediaType : allowedTypes) {
                    if (type.contains(mediaType)) {
                        res.add(type);
                    }
                }
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     *
     * Whether to to add includes
     */
    @Override
    public boolean isAddIncludes() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Whether model processing is required
     */
    @Override
    public boolean isProcessModel() {
        return false;
    }


}
