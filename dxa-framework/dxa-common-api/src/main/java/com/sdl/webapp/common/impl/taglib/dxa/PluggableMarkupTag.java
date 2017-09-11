package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.markup.PluggableMarkupRegistry;
import com.sdl.webapp.common.markup.html.HtmlNode;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

@Setter
public class PluggableMarkupTag extends TagSupport {

    private PluggableMarkupRegistry registry;

    private String label;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {

        try {
            for (HtmlNode markup : this.getPluggableMarkupRegistry().getPluggableMarkup(this.label)) {
                pageContext.getOut().write(markup.toHtml());
            }
        } catch (IOException e) {
            throw new JspException("Error while generating pluggable markup for label=" + this.label, e);
        }

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
