package com.sdl.webapp.common.api.formats;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Administrator on 9/29/2015.
 */

@Component
@ComponentScan(basePackages = {"com.sdl.webapp.main", "com.sdl.webapp.common.controller", "com.sdl.webapp.addon"})
public class DefaultDataFormatter implements DataFormatter{

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private WebRequestContext context;

    private static Map<String, com.sdl.webapp.common.api.formatters.DataFormatter> formatters;

    @Autowired
    public DefaultDataFormatter(HttpServletRequest request, WebRequestContext context)
    {
        formatters = new HashMap<String, com.sdl.webapp.common.api.formatters.DataFormatter>();
        formatters.put("json", new JsonFormatter(request, context));
        formatters.put("atom", new AtomFormatter(request, context));
        formatters.put("rss", new RssFormatter(request, context));
    }



    public com.sdl.webapp.common.api.formatters.DataFormatter getFormatter(String format)
    {
        if (formatters.containsKey(format) && context.getLocalization().getDataFormats().contains(format))
        {
            return (com.sdl.webapp.common.api.formatters.DataFormatter)formatters.get(format);
        }
        return null;
    }

    private String getFormat(HttpServletRequest request)
    {
        //TODO: Jaime Santos get the format query string
        String format = "";//controllerContext.RequestContext.HttpContext.Request.QueryString["format"];
        if (format != null)
        {
            return format.toLowerCase();
        }
        format = "html";
        double topScore = getHtmlAcceptScore(request);
        if (topScore<1.0)
        {
            for (String key : formatters.keySet())
            {
                double score = formatters.get(key).score(request);
                if (score > topScore)
                {
                    topScore = score;
                    format = key;
                }
                if (topScore == 1.0)
                {
                    break;
                }
            }
        }
        return format;
    }

    private double getHtmlAcceptScore(HttpServletRequest controllerContext)
    {
        double score = 0.0;
        //TODO: Jaime Santos Get accept types
        String[] acceptTypes = null;//controllerContext.HttpContext.Request.AcceptTypes;
        if (acceptTypes!=null)
        {
            for (String type : acceptTypes)
            {
                if (type.contains("html"))
                {
                    double thisScore = getScoreFromAcceptString(type);
                    if (thisScore > score)
                    {
                        score = thisScore;
                    }
                    if (score == 1)
                    {
                        break;
                    }
                }
            }
        }
        return score;
    }

    public Map<String, com.sdl.webapp.common.api.formatters.DataFormatter> getFormatters() {
        return formatters;
    }

    public void setFormatters(Map<String, com.sdl.webapp.common.api.formatters.DataFormatter> formatters) {
        formatters = formatters;
    }
    public static double getScoreFromAcceptString(String type)
    {
        double res = 1.0;
        int pos = type.indexOf("q=");
        if (pos > 0)
        {
            return Double.parseDouble(type.substring(pos + 2));
        }
        return res;
    }
}
