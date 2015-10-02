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
    WebRequestContext context;
    HttpServletRequest request;

    public BaseFormatter(HttpServletRequest request, WebRequestContext context){
        this.context = context;
        this.request = request;

    }

    private final List<String> _mediaTypes = new ArrayList<String>();
    public void addMediaType(String mediaType)
    {
        _mediaTypes.add(mediaType);
    }


    /**
     * Gets the score depending on the media type
     * @return
     */
    public double score()
    {
        double score = 0.0;
        List<String> validTypes = getValidTypes(_mediaTypes);
        if (validTypes!=null && !validTypes.isEmpty())
        {
            for(String type : validTypes)
            {
                double thisScore = DefaultDataFormatter.getScoreFromAcceptString(type);
                if (thisScore>score)
                {
                    score = thisScore;
                }
            }
        }
        return score;
    }

    /**
     * Gets the valid mediat types depending on the allowed types by the formatter
     * @param allowedTypes
     * @return
     */
    public List<String> getValidTypes(List<String> allowedTypes)
    {
        List<String> res = new ArrayList<String>();
        //TODO: TW Check that acceptTypes come as comma-separated list
        String[] acceptTypes = request.getHeader("Accept").split(",");
        if (acceptTypes!=null)
        {
            for(String type : acceptTypes)
            {
                for(String mediaType : allowedTypes)
                {
                    if (type.contains(mediaType))
                    {
                        res.add(type);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Whether to to add includes
     * @return
     */
    @Override
    public boolean isAddIncludes() {
        return false;
    }

    /**
     * Whether model processing is required
     * @return
     */
    @Override
    public boolean isProcessModel() {
        return false;
    }



}
