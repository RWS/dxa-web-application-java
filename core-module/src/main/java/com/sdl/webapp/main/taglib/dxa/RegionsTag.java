package com.sdl.webapp.main.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import com.sdl.webapp.common.controller.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class RegionsTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RegionsTag.class);

    private String exclude;

    private RegionModel parentRegion;
    private int containerSize;
        
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
    public void setContainerSize(int containerSize)
    {
    	this.containerSize = containerSize;
    }

    @Override
    public int doStartTag() throws JspException {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        WebRequestContext webRequestContext = this.getWebRequestContext();
        
        parentRegion = webRequestContext.getParentRegion();

        Set<String> excludes = new HashSet<>();
        if (!Strings.isNullOrEmpty(exclude)) {
            excludes.addAll(Arrays.asList(exclude.split("\\s*,\\s*")));
        }

        RegionModelSet regions = page.getRegions();
        if (parentRegion != null) {
            regions = parentRegion.getRegions();
        }

        for (RegionModel region : regions) {
            String name = region.getName();
            if (excludes.contains(name)) {
                LOG.debug("Excluding region: {}", name);
                continue;
            }

            LOG.debug("Including region: {}", name);
            
            try {
                //pageContext.include(ControllerUtils.getIncludePath(region));
            	pageContext.getRequest().setAttribute("_region_" + name, region);
                webRequestContext.pushParentRegion(region);
                webRequestContext.pushContainerSize(containerSize);
                this.decorateInclude(ControllerUtils.getIncludePath(region), region);
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing regions tag", e);
            }
            finally {
                webRequestContext.popParentRegion();
                webRequestContext.popContainerSize();
            }
        }

        return SKIP_BODY;
    }
}
