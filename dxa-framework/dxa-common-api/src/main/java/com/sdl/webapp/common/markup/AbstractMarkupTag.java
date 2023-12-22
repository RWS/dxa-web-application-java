package com.sdl.webapp.common.markup;

import com.sdl.dxa.caching.CompositeOutputCacheKeyBase;
import com.sdl.dxa.caching.LocalizationAwareCacheKey;
import com.sdl.dxa.caching.wrapper.OutputCache;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import jakarta.servlet.jsp.JspWriter;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

public class AbstractMarkupTag extends TagSupport {

    private MarkupDecoratorRegistry markupDecoratorRegistry = null;
    private OutputCache outputCache;

    protected Optional<CompositeOutputCacheKeyBase> getCacheKey(String include, ViewModel model) {
        return Optional.empty();
    }

    protected String getDecoratorId() {
        return this.getClass().getSimpleName().replace("Tag", "");
    }

    protected String processInclude(String include, ViewModel model) throws IOException, ServletException {
        return processIncludeInternal(include, model).toHtml();
    }

    protected void decorateException(ViewModel model) throws JspException {
        try {
            this.decorateInclude(ControllerUtils.getIncludeErrorPath(), model);
        } catch (IOException | ServletException e1) {
            throw new JspException("Error while processing entity tag, error view wasn't found", e1);
        }
    }

    protected void decorateInclude(String include, ViewModel model) throws IOException, ServletException {
        HtmlNode decoratedMarkup = null;
        try {
            decoratedMarkup = processIncludeInternal(include, model);
        } finally {
            if (decoratedMarkup != null) {
                pageContext.getOut().write(decoratedMarkup.toHtml());
            }
        }
    }

    protected HtmlNode decorateMarkup(HtmlNode markup, ViewModel model) {
        HtmlNode processedMarkup = markup;
        List<MarkupDecorator> markupDecorators = this.getMarkupDecoratorRegistry().getDecorators(this.getDecoratorId());
        for (MarkupDecorator markupDecorator : markupDecorators) {
            processedMarkup = markupDecorator.process(processedMarkup, model, this.getWebRequestContext());
        }
        return processedMarkup;
    }

    protected MarkupDecoratorRegistry getMarkupDecoratorRegistry() {
        if (markupDecoratorRegistry == null) {
            markupDecoratorRegistry = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                    .getBean(MarkupDecoratorRegistry.class);
        }
        return markupDecoratorRegistry;
    }

    protected WebRequestContext getWebRequestContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class);
    }

    protected OutputCache getOutputCache() {
        if (this.outputCache == null) {
            this.outputCache = ApplicationContextHolder.getContext().getBean(OutputCache.class);
        }
        return this.outputCache;
    }

    private HtmlNode processIncludeInternal(String include, ViewModel model) throws ServletException, IOException {
        pageContext.getRequest().setAttribute("ParentModel", model);

        OutputCache outputCache = getOutputCache();
        boolean cacheAccessible = false;
        LocalizationAwareCacheKey specificKey = null;

        if (outputCache.isCachingEnabled()) {
            Optional<CompositeOutputCacheKeyBase> optionalKey = getCacheKey(include, model);
            specificKey = (LocalizationAwareCacheKey) optionalKey.map(outputCache::getSpecificKey).orElse(null);

            cacheAccessible = optionalKey.isPresent() && !getWebRequestContext().isSessionPreview();

            if (cacheAccessible) {
                HtmlNode cachedNode = outputCache.get(specificKey);
                if (cachedNode != null) return cachedNode;
            }
        }

        try (StringWriter sw = new StringWriter()) {
            pageContext.pushBody(sw);
            pageContext.include(include);
            String renderedHtml = sw.toString();
            ParsableHtmlNode markup = new ParsableHtmlNode(renderedHtml);
            HtmlNode htmlNode = this.decorateMarkup(markup, model);

            if (cacheAccessible) {
                outputCache.addAndGet(specificKey, htmlNode);
            }

            return htmlNode;
        } finally {
            pageContext.popBody();
        }
    }
}
