package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.UUID;

public class GoogleStaticMapTag extends AbstractGoogleMapTag {

    private static final HtmlAttribute CLASS_STATIC_MAP_ATTR = new HtmlAttribute("class", "static-map");

    @Override
    public HtmlNode generateNode() {
        final WebRequestContext webRequestContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(WebRequestContext.class);
        final Localization localization = webRequestContext.getLocalization();

        final String latString = getLatString();
        final String lonString = getLonString();

        final StringBuilder sb = new StringBuilder();
        sb.append("?center=").append(latString).append(',').append(lonString);
        sb.append("&zoom=15");
        sb.append("&size=").append(getMapWidth()).append('x').append(getMapHeight());
        sb.append("&markers=").append(latString).append(',').append(lonString);

        final String mapsApiKey = getMapsApiKey(localization);
        if (!Strings.isNullOrEmpty(mapsApiKey)) {
            sb.append("&key=").append(mapsApiKey);
        }

        return HtmlBuilders.div()
                .withId("map" + UUID.randomUUID().toString().replaceAll("-", ""))
                .withAttribute(CLASS_STATIC_MAP_ATTR)
                .withAttribute("style", "height: " + Integer.toString(getMapHeight()))
                .withContent(HtmlBuilders.img("//maps.googleapis.com/maps/api/staticmap" + sb.toString())
                        .withAlt(getMarkerName())
                        .build())
                .build();
    }
}
