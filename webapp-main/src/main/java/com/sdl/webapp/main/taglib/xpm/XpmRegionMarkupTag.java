package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.main.markup.html.HtmlCommentNode;

public class XpmRegionMarkupTag extends XpmMarkupTag {

    private static final String REGION_PATTERN = "<!-- Start Region: {{title: \"%s\", " +
            "allowedComponentTypes: [%s], minOccurs: 0}} -->";

    private Region region;

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    protected HtmlCommentNode generateXpmMarkup() {
        // TODO determine min occurs and max occurs for the region

        // TODO: Determine allowed component types
        final String allowedComponentTypes = "";

        return new HtmlCommentNode(String.format(REGION_PATTERN, region.getName(), allowedComponentTypes));
    }
}
