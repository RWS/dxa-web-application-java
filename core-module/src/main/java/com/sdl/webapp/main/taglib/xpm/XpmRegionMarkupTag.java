package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.tridion.xpm.ComponentType;
import com.sdl.webapp.tridion.xpm.XpmRegion;
import com.sdl.webapp.tridion.xpm.XpmRegionConfig;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class XpmRegionMarkupTag extends XpmMarkupTag {

    private static final String REGION_PATTERN = "Start Region: {title:\"%s\",allowedComponentTypes:[%s]," +
            "minOccurs:%d%s}";

    private static final String MAX_OCCURS_PATTERN = ",maxOccurs:%d";

    private static final String COMPONENT_TYPE_PATTERN = "%s{schema:\"%s\",template:\"%s\"}";

    private Region region;

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    protected HtmlNode generateXpmMarkup() {
        // TODO determine min occurs and max occurs for the region
        final int minOccurs = 0;
        final int maxOccurs = 0;

        final XpmRegion xpmRegion = getXpmRegionConfig().getXpmRegion(region.getName(), getLocalization());
        if (xpmRegion == null) {
            return null;
        }

        String separator = "";
        boolean first = true;
        final StringBuilder sb = new StringBuilder();
        for (ComponentType componentType : xpmRegion.getComponentTypes()) {
            sb.append(String.format(COMPONENT_TYPE_PATTERN, separator, componentType.getSchemaId(),
                    componentType.getTemplateId()));
            if (first) {
                first = false;
                separator = ",";
            }
        }

        return new HtmlCommentNode(String.format(REGION_PATTERN, region.getName(), sb.toString(), minOccurs,
                maxOccurs > 0 ? String.format(MAX_OCCURS_PATTERN, maxOccurs) : ""));
    }

    private XpmRegionConfig getXpmRegionConfig() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(XpmRegionConfig.class);
    }

    private Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }

}
