package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>XpmRegionMarkupTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class XpmRegionMarkupTag extends XpmMarkupTag {

    private static final String REGION_PATTERN = "Start Region: {title:\"%s\",allowedComponentTypes:[%s]," +
            "minOccurs:%d%s}";

    private static final String MAX_OCCURS_PATTERN = ",maxOccurs:%d";

    private static final String COMPONENT_TYPE_PATTERN = "%s{schema:\"%s\",template:\"%s\"}";

    private RegionModel region;

    /**
     * <p>Setter for the field <code>region</code>.</p>
     *
     * @param region a {@link com.sdl.webapp.common.api.model.RegionModel} object.
     */
    public void setRegion(RegionModel region) {
        this.region = region;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlNode generateXpmMarkup() {

        return new HtmlNode() {
            @Override
            public String renderHtml() {
                return region.getXpmMarkup(getLocalization());
            }
        };

    }


    private Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }

}
