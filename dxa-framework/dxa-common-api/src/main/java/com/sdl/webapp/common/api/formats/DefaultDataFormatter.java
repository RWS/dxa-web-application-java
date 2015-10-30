package com.sdl.webapp.common.api.formats;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.AtomFormatter;
import com.sdl.webapp.common.api.formatters.JsonFormatter;
import com.sdl.webapp.common.api.formatters.RssFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper bean to handle the different formatters
 */

@Component
@ComponentScan(basePackages = {"com.sdl.webapp.main", "com.sdl.webapp.common.controller", "com.sdl.webapp.addon"})
public class DefaultDataFormatter implements DataFormatter {

    private HttpServletRequest request;

    private WebRequestContext context;

    private Map<String, com.sdl.webapp.common.api.formatters.DataFormatter> formatters;

    @Autowired
    public DefaultDataFormatter(HttpServletRequest request, WebRequestContext context) {
        formatters = new HashMap<String, com.sdl.webapp.common.api.formatters.DataFormatter>();
        formatters.put("json", new JsonFormatter(request, context));
        formatters.put("atom", new AtomFormatter(request, context));
        formatters.put("rss", new RssFormatter(request, context));
    }

    /**
     * Gets the score from accept string
     *
     * @param type
     * @return
     */
    public static double getScoreFromAcceptString(String type) {
        double res = 1.0;
        int pos = type.indexOf("q=");
        if (pos > 0) {
            return Double.parseDouble(type.substring(pos + 2));
        }
        return res;
    }

    /**
     * Gets the formatter based on the format
     *
     * @param format: the format (lowercase): json, atom or rss
     * @return The (@code DataFormatter) to handle the response
     */
    private com.sdl.webapp.common.api.formatters.DataFormatter getFormatter(String format) {
        if (formatters.containsKey(format) && context.getLocalization().getDataFormats().contains(format)) {
            return formatters.get(format);
        }
        return null;
    }

    /**
     * Returns the  @see ModelAndView to render the different formats
     *
     * @param model
     * @return
     */
    @Override
    public ModelAndView view(Object model) {
        String format = getFormat();
        com.sdl.webapp.common.api.formatters.DataFormatter formatter = getFormatter(format);
        ModelAndView mav = new ModelAndView();
        switch (format) {
            case "rss":
                mav.setViewName("rssFeedView");
                break;
            case "atom":
                mav.setViewName("atomFeedView");
                break;
            case "json":
            default:
                mav.setViewName("jsonFeedView");
                break;
        }
        mav.addObject("formatter", formatter);
        mav.addObject("data", model);

        return mav;
    }

    /**
     * Gets the format
     *
     * @return the format from the @see HttpServletRequest query string
     */
    private String getFormat() {
        String format = request.getParameter("format");
        if (format != null) {
            return format.toLowerCase();
        }
        format = "html";
        double topScore = getHtmlAcceptScore();
        if (topScore < 1.0) {
            for (Map.Entry<String, com.sdl.webapp.common.api.formatters.DataFormatter> entry : formatters.entrySet()) {

                double score = entry.getValue().score();
                if (score > topScore) {
                    topScore = score;
                    format = entry.getKey();
                }
                if (topScore == 1.0) {
                    break;
                }
            }
        }
        return format;
    }

    /**
     * Gets the score for html media type
     *
     * @return the score
     */
    private double getHtmlAcceptScore() {
        double score = 0.0;
        String[] acceptTypes = request.getHeader("Accept").split(",");
        if (acceptTypes != null) {
            for (String type : acceptTypes) {
                if (type.contains("html")) {
                    double thisScore = getScoreFromAcceptString(type);
                    if (thisScore > score) {
                        score = thisScore;
                    }
                    if (score == 1) {
                        break;
                    }
                }
            }
        }
        return score;
    }
}
