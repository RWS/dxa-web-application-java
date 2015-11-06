package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class XpmRegionMarkupTag extends XpmMarkupTag {

    private static final String REGION_PATTERN = "Start Region: {title:\"%s\",allowedComponentTypes:[%s]," +
            "minOccurs:%d%s}";

    private static final String MAX_OCCURS_PATTERN = ",maxOccurs:%d";

    private static final String COMPONENT_TYPE_PATTERN = "%s{schema:\"%s\",template:\"%s\"}";

    private RegionModel region;

    public void setRegion(RegionModel region) {
        this.region = region;
    }

    @Override
    protected HtmlNode generateXpmMarkup() {

        return new HtmlNode() {
            @Override
            protected String renderHtml() {
                return region.getXpmMarkup(getLocalization());
            }
        };

    }


    private Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }

}
