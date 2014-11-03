package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class GoogleMapTag extends TagSupport {

    private static final String CONFIG_MAPS_API_KEY = "core.mapsApiKey";
    private static final String DUMMY_API_KEY = "xxx";

    private static final int DEFAULT_MAP_WIDTH = 311;
    private static final int DEFAULT_MAP_HEIGHT = 160;

    private double latitude;
    private double longitude;
    private String markerName;
    private int mapWidth = DEFAULT_MAP_WIDTH;
    private int mapHeight = DEFAULT_MAP_HEIGHT;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    @Override
    public int doStartTag() throws JspException {
        final WebRequestContext webRequestContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(WebRequestContext.class);
        final Localization localization = webRequestContext.getLocalization();

        final String latString = String.format(Locale.US, "%f", latitude);
        final String lonString = String.format(Locale.US, "%f", longitude);

        final StringBuilder sb = new StringBuilder();
        sb.append("?center=").append(latString).append(',').append(lonString);
        sb.append("&zoom=15");
        sb.append("&size=").append(mapWidth).append('x').append(mapHeight);
        sb.append("&markers=").append(latString).append(',').append(lonString);

        final String mapsApiKey = localization.getConfiguration(CONFIG_MAPS_API_KEY);
        if (!Strings.isNullOrEmpty(mapsApiKey) && !mapsApiKey.equals(DUMMY_API_KEY)) {
            sb.append("&key=").append(mapsApiKey);
        }

        final String queryString = sb.toString();

        final JspWriter out = pageContext.getOut();
        try {
            out.write("<div id=\"");
            out.write("map" + UUID.randomUUID().toString());
            out.write("\" class=\"static-map\" style=\"height:");
            out.write(Integer.toString(mapHeight));
            out.write("\">");

            out.write("<img src=\"//maps.googleapis.com/maps/api/staticmap");
            out.write(queryString);
            out.write("\" alt=\"");
            out.write(markerName);
            out.write("\">");

            out.write("</div>");
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }
}
