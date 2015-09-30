package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formats.DefaultDataFormatter;
import com.sdl.webapp.common.api.model.PageModel;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/29/2015.
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




    public double score(HttpServletRequest request)
    {
        double score = 0.0;
        List<String> validTypes = getValidTypes(request, _mediaTypes);
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

    public List<String> getValidTypes(HttpServletRequest request, List<String> allowedTypes)
    {
        List<String> res = new ArrayList<String>();
        //TODO: Jaime Santos get acceptTypes
        String[] acceptTypes = null;//controllerContext.HttpContext.Request.AcceptTypes;
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

    @Override
    public boolean isAddIncludes() {
        return false;
    }

    @Override
    public boolean isProcessModel() {
        return false;
    }



}
