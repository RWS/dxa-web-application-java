package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.api.model.region.RegionImpl;
import com.sdl.webapp.common.api.model.region.SimpleRegionMvcData;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import com.sdl.webapp.common.controller.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class RegionTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;
    private boolean placeholder;

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        Region region = page.getRegions().get(name);
        if ( region == null && placeholder == true ) {
            // Render the region even if it is not present on the page, so XPM region markup etc can be generated
            //

            RegionImpl placeholderRegion = new RegionImpl();
            placeholderRegion.setName(name);
            placeholderRegion.setMvcData(new SimpleRegionMvcData(name));
            region = placeholderRegion;
            pageContext.getRequest().setAttribute("_region_" + name, placeholderRegion);
        }

        if (region != null) {
            LOG.debug("Including region: {}", name);
            try {

                //pageContext.include(ControllerUtils.getIncludePath(region));
                this.decorateInclude(ControllerUtils.getIncludePath(region), region);

            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing region tag: " + name, e);
            }
        } else {
            LOG.debug("Region not found on page: {}", name);
        }

        return SKIP_BODY;
    }

}
