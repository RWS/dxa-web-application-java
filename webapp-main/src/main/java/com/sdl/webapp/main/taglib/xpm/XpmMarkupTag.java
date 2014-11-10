package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.main.markup.html.HtmlCommentNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public abstract class XpmMarkupTag extends TagSupport {

    protected abstract HtmlCommentNode generateXpmMarkup();

    @Override
    public int doStartTag() throws JspException {
        if (isPreview()) {
            final HtmlCommentNode xpmMarkup = generateXpmMarkup();
            if (xpmMarkup != null) {
                final JspWriter out = pageContext.getOut();
                try {
                    out.write(xpmMarkup.toHtml());
                } catch (IOException e) {
                    throw new JspException(e);
                }
            }
        }

        return SKIP_BODY;
    }

    private boolean isPreview() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).isPreview();
    }
}
