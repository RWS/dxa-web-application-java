package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.dxa.caching.CompositeOutputCacheKeyBase;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Optional;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.REGION;
import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Setter
public class RegionTag extends AbstractMarkupTag {

    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;

    private boolean placeholder;

    private String emptyViewName;

    private int containerSize;

    @Override
    protected Optional<CompositeOutputCacheKeyBase> getCacheKey(String include, ViewModel model) {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null || !page.canBeCached() || !getOutputCache().isCachingEnabled() || model.getClass().isAnnotationPresent(NeverCached.class)) {
            return Optional.empty();
        }
        return Optional.of(new CompositeOutputCacheKeyBase(page.getId(), name, include, model.getMvcData(), (HttpServletRequest) pageContext.getRequest()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        WebRequestContext webRequestContext = this.getWebRequestContext();

        RegionModel parentRegion = webRequestContext.getParentRegion();

        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        RegionModel region;
        if (isEmpty(name)) {
            //special case where we wish to render an include page as region
            this.pageContext.setAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE, "1");
            // Create a new Region Model which reflects the Page Model
            String regionName = page.getName().replace(" ", "-");
            MvcData mvcData = MvcDataCreator.creator()
                    .defaults(REGION)
                    .builder()
                    .regionName(regionName)
                    .viewName(regionName).build();

            RegionModelImpl includeRegion = null;
            try {
                includeRegion = new RegionModelImpl(regionName);
                includeRegion.setMvcData(mvcData);
                includeRegion.setRegions(page.getRegions());
            } catch (DxaException e) {
                LOG.error("Exception when creating new regionModel {}", name, e);
            }

            region = includeRegion;
        } else {
            region = page.getRegions().get(name);
        }

        if (parentRegion != null) {
            region = parentRegion.getRegions().get(name);
        }

        if (region == null && placeholder) {
            // Render the region even if it is not present on the page, so XPM region markup etc can be generated
            RegionModelImpl placeholderRegion = null;
            try {
                placeholderRegion = new RegionModelImpl(name);
                MvcData mvcData = MvcDataCreator.creator()
                        .defaults(REGION)
                        .builder()
                        .regionName(name)
                        .viewName(isEmpty(emptyViewName) ? name : emptyViewName)
                        .build();

                placeholderRegion.setMvcData(mvcData);
            } catch (DxaException e) {
                LOG.error("Exception when creating new placeholderRegion {}", name, e);
            }

            region = placeholderRegion;
        }

        if (region != null) {
            String regionName = region.getName();
            LOG.debug("Including region: {}", regionName);

            try {
                pageContext.getRequest().setAttribute("_region_", region);
                webRequestContext.pushParentRegion(region);
                webRequestContext.pushContainerSize(containerSize);
                this.decorateInclude(ControllerUtils.getIncludePath(region), region);
            } catch (ServletException | IOException e) {
                LOG.error("Error while processing region tag", e);
                decorateException(region);
            } finally {
                webRequestContext.popParentRegion();
                webRequestContext.popContainerSize();
            }
        } else {
            LOG.debug("Region not found on page: {}", name);
        }

        return SKIP_BODY;
    }
}
