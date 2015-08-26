package com.sdl.webapp.main.taglib.dxa;

import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
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
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        RegionModel region = page.getRegions().get(name);
        if ( region == null && placeholder == true ) {
            // Render the region even if it is not present on the page, so XPM region markup etc can be generated
            //

            RegionModelImpl placeholderRegion = new RegionModelImpl();
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
