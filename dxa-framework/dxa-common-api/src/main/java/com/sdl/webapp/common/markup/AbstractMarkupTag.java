package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * AbstractMarkupTag
 *
 * @author nic
 */
public class AbstractMarkupTag extends TagSupport {

    private MarkupDecoratorRegistry markupDecoratorRegistry = null;

    protected String getDecoratorId() {
        return this.getClass().getSimpleName().replace("Tag", "");
    }

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
}
