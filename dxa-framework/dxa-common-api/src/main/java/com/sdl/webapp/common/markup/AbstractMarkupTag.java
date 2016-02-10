package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * <p>AbstractMarkupTag class.</p>
 */
public class AbstractMarkupTag extends TagSupport {

    private MarkupDecoratorRegistry markupDecoratorRegistry = null;

    /**
     * <p>getDecoratorId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getDecoratorId() {
        return this.getClass().getSimpleName().replace("Tag", "");
    }

    /**
     * <p>processInclude.</p>
     *
     * @param include a {@link java.lang.String} object.
     * @param model   a {@link com.sdl.webapp.common.api.model.ViewModel} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException            if any.
     * @throws javax.servlet.ServletException if any.
     */
    protected String processInclude(String include, ViewModel model) throws IOException, ServletException {

        // TODO: Consider to replace with an annotation instead

        try {
            StringWriter sw = new StringWriter();
            pageContext.pushBody(sw);
            pageContext.getRequest().setAttribute("ParentModel", model);
            pageContext.include(include);
            String renderedHtml = sw.toString();
            ParsableHtmlNode markup = new ParsableHtmlNode(renderedHtml);
            HtmlNode decoratedMarkup = this.decorateMarkup(markup, model);
            return decoratedMarkup.toHtml();
        } finally {
            pageContext.popBody();
        }
    }

    /**
     * <p>decorateException.</p>
     *
     * @param model a {@link com.sdl.webapp.common.api.model.ViewModel} object.
     * @throws javax.servlet.jsp.JspException if any.
     */
    protected void decorateException(ViewModel model) throws JspException {
        try {
            this.decorateInclude(ControllerUtils.getIncludeErrorPath(), model);
        } catch (IOException | ServletException e1) {
            throw new JspException("Error while processing entity tag, error view wasn't found", e1);
        }
    }

    /**
     * <p>decorateInclude.</p>
     *
     * @param include a {@link java.lang.String} object.
     * @param model   a {@link com.sdl.webapp.common.api.model.ViewModel} object.
     * @throws java.io.IOException            if any.
     * @throws javax.servlet.ServletException if any.
     */
    protected void decorateInclude(String include, ViewModel model) throws IOException, ServletException {

        // TODO: Consider to replace with an annotation instead

        HtmlNode decoratedMarkup = null;
        try {
            StringWriter sw = new StringWriter();
            pageContext.pushBody(sw);
            pageContext.getRequest().setAttribute("ParentModel", model);
            pageContext.include(include);
            String renderedHtml = sw.toString();
            ParsableHtmlNode markup = new ParsableHtmlNode(renderedHtml);
            decoratedMarkup = this.decorateMarkup(markup, model);
        } finally {
            pageContext.popBody();
            if (decoratedMarkup != null) {
                pageContext.getOut().write(decoratedMarkup.toHtml());
            }
        }
    }

    /**
     * <p>decorateMarkup.</p>
     *
     * @param markup a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     * @param model a {@link com.sdl.webapp.common.api.model.ViewModel} object.
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    protected HtmlNode decorateMarkup(HtmlNode markup, ViewModel model) {
        HtmlNode processedMarkup = markup;
        List<MarkupDecorator> markupDecorators = this.getMarkupDecoratorRegistry().getDecorators(this.getDecoratorId());
        for (MarkupDecorator markupDecorator : markupDecorators) {
            processedMarkup = markupDecorator.process(processedMarkup, model, this.getWebRequestContext());
        }
        return processedMarkup;
    }

    /**
     * <p>Getter for the field <code>markupDecoratorRegistry</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.MarkupDecoratorRegistry} object.
     */
    protected MarkupDecoratorRegistry getMarkupDecoratorRegistry() {
        if (markupDecoratorRegistry == null) {
            markupDecoratorRegistry = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                    .getBean(MarkupDecoratorRegistry.class);
        }
        return markupDecoratorRegistry;
    }

    /**
     * <p>getWebRequestContext.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    protected WebRequestContext getWebRequestContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class);
    }
}
