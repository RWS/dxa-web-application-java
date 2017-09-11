package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.markup.PluggableMarkupRegistry;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

@Setter
public class DefinePluggableMarkupTag extends BodyTagSupport {

    // TODO: Define a common abstract base class for all tags dealing with pluggable markup

    private PluggableMarkupRegistry registry;

    private String label;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doAfterBody() throws JspException {

        BodyContent bodyContent = this.getBodyContent();
        HtmlNode markup = new ParsableHtmlNode(bodyContent.getString());
        this.getPluggableMarkupRegistry().registerContextualPluggableMarkup(this.label, markup);
        return SKIP_BODY;
    }

    /**
     * <p>getPluggableMarkupRegistry.</p>
     *
     * @return a {@link PluggableMarkupRegistry} object.
     */
    protected PluggableMarkupRegistry getPluggableMarkupRegistry() {
        if (this.registry == null) {
            this.registry = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                    .getBean(PluggableMarkupRegistry.class);
        }
        return this.registry;
    }

}
