package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.main.markup.html.HtmlAttribute;
import com.sdl.webapp.main.markup.html.HtmlElement;
import com.sdl.webapp.main.markup.html.builders.HtmlBuilders;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Locale;
import java.util.UUID;

public class GoogleMapTag extends HtmlElementTag {

    private static final String CONFIG_MAPS_API_KEY = "core.mapsApiKey";
    private static final String DUMMY_API_KEY = "xxx";

    private static final int DEFAULT_MAP_WIDTH = 311;
    private static final int DEFAULT_MAP_HEIGHT = 160;

    private static final HtmlAttribute CLASS_STATIC_MAP_ATTR = new HtmlAttribute("class", "static-map");

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
    public HtmlElement generateElement() {
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

        return HtmlBuilders.div()
                .withId("map" + UUID.randomUUID().toString())
                .withAttribute(CLASS_STATIC_MAP_ATTR)
                .withAttribute("style", "height: " + Integer.toString(mapHeight))
                .withContent(HtmlBuilders.img("//maps.googleapis.com/maps/api/staticmap" + sb.toString())
                        .withAlt(markerName)
                        .build())
                .build();
    }
}
