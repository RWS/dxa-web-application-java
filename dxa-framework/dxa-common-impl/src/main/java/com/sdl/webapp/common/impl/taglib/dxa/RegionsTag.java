package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Set;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;

@Slf4j
public class RegionsTag extends AbstractMarkupTag {

    private Set<String> excludes;

    @Setter
    private int containerSize;

    public void setExclude(String exclude) {
        this.excludes = commaDelimitedListToSet(exclude);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            log.error("Page not found in request attributes");
            return SKIP_BODY;
        }

        WebRequestContext webRequestContext = this.getWebRequestContext();

        RegionModel parentRegion = webRequestContext.getParentRegion();
        RegionModelSet regions = (parentRegion == null) ? page.getRegions() : parentRegion.getRegions();

        for (RegionModel region : regions) {
            String name = region.getName();
            if (this.excludes != null && this.excludes.contains(name)) {
                log.debug("Excluding region: {}", name);
                continue;
            }

            log.debug("Including region: {}", name);

            try {
                pageContext.getRequest().setAttribute("_region_", region);
                webRequestContext.pushParentRegion(region);
                webRequestContext.pushContainerSize(containerSize);
                this.decorateInclude(ControllerUtils.getIncludePath(region), region);
            } catch (ServletException | IOException e) {
                log.error("Error while processing regions tag", e);
                decorateException(region);
            } finally {
                webRequestContext.popParentRegion();
                webRequestContext.popContainerSize();
            }
        }

        return SKIP_BODY;
    }
}
