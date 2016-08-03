package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.api.formats.DefaultDataFormatter.getScoreFromAcceptString;

/**
 * BaseFormatter with base logic to be used by the format specific formatters.
 */
@RequiredArgsConstructor
public abstract class BaseFormatter implements DataFormatter {

    protected final HttpServletRequest request;

    protected final WebRequestContext context;

    private final List<String> _mediaTypes = new ArrayList<>();

    //todo dxa2 package-private access
    public void addMediaType(String mediaType) {
        _mediaTypes.add(mediaType);
    }

    @Override
    public double score() {
        double score = 0.0;
        List<String> validTypes = getValidTypes(_mediaTypes);
        if (validTypes == null || validTypes.isEmpty()) {
            return score;
        }

        for (String type : validTypes) {
            double newScore = getScoreFromAcceptString(type);
            if (newScore > score) {
                score = newScore;
            }
        }

        return score;
    }

    @Override
    public boolean isProcessModel() {
        return false;
    }

    @Override
    public boolean isAddIncludes() {
        return false;
    }

    @Override
    public List<String> getValidTypes(List<String> allowedTypes) {
        List<String> result = new ArrayList<>();

        String requestHeader = request.getHeader("Accept");
        if (requestHeader != null) {

            for (String type : requestHeader.split(",")) {

                for (String mediaType : allowedTypes) {

                    if (type.contains(mediaType)) {
                        result.add(type);
                    }
                }
            }
        }
        return result;
    }
}
