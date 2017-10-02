package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.dxa.caching.CompositeOutputCacheKeyBase;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;

@Setter
public class EntityTag extends AbstractMarkupTag {

    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private EntityModel entity;

    private int containerSize;

    private String viewName;

    @Override
    protected Optional<CompositeOutputCacheKeyBase> getCacheKey(String include, ViewModel model) {
        if (model.getClass().isAnnotationPresent(NeverCached.class)) {
            return Optional.empty();
        }
        return Optional.of(new CompositeOutputCacheKeyBase(entity.getId(), viewName, include, model.getMvcData(), (HttpServletRequest) pageContext.getRequest()));
    }

    /**
     * <p>Setter for the field <code>viewName</code>.</p>
     *
     * @param viewName a {@link String} object.
     */
    public void applyNewViewNameIfNeeded(String viewName) {
        this.viewName = viewName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        WebRequestContext webRequestContext = this.getWebRequestContext();

        applyNewViewNameIfNeeded();

        try {
            LOG.debug("Including entity into request: {}", entity.getId());
            pageContext.getRequest().setAttribute("_entity_", entity);

            webRequestContext.pushContainerSize(containerSize);
            this.decorateInclude(ControllerUtils.getIncludePath(entity), entity);
        } catch (ServletException | IOException e) {
            LOG.error("Error while processing entity tag", e);
            decorateException(entity);
        } finally {
            webRequestContext.popContainerSize();
        }

        return SKIP_BODY;
    }

    protected void applyNewViewNameIfNeeded() {
        if (!isNullOrEmpty(viewName)) {
            entity.setMvcData(creator(entity.getMvcData())
                    .mergeIn(creator().fromQualifiedName(viewName).create())
                    .create());
        }
    }
}
