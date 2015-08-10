package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.UUID;

public class GoogleMapTag extends AbstractGoogleMapTag {

    private static final HtmlAttribute CLASS_MAP_CANVAS_ATTR = new HtmlAttribute("class", "map-canvas");

    @Override
    protected HtmlNode generateNode() {
        final WebRequestContext webRequestContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(WebRequestContext.class);
        final Localization localization = webRequestContext.getLocalization();

        final String divId = "map" + UUID.randomUUID().toString().replaceAll("-", "");

        final String mapsApiKey = getMapsApiKey(localization);
        final String queryString = !Strings.isNullOrEmpty(mapsApiKey) ? "?key=" + mapsApiKey : "";

        final HtmlElement div = HtmlBuilders.div()
                .withId(divId)
                .withAttribute(CLASS_MAP_CANVAS_ATTR)
                .withAttribute("style", "height:" + Integer.toString(getMapHeight()) + "px")
                .build();

        final HtmlElement script1 = HtmlBuilders.element("script")
                .withAttribute("src", "//maps.googleapis.com/maps/api/js" + queryString)
                .build();

        final HtmlElement script2 = HtmlBuilders.element("script")
                .withLiteralContent("function initialize() {\n" +
                        "    var myLatlng = new google.maps.LatLng(" + getLatString() + ", " + getLonString() + ");\n" +
                        "    var mapOptions = { center: myLatlng, zoom: 15 };\n" +
                        "    var map = new google.maps.Map(document.getElementById(\"" + divId + "\"), mapOptions);\n" +
                        "    var marker = new google.maps.Marker({ position: myLatlng, map: map, title: \"" + getMarkerName() + "\" });\n" +
                        "}\n" +
                        "google.maps.event.addDomListener(window, 'load', initialize);\n")
                .build();

        return new HtmlMultiNode(div, script1, script2);
    }
}
