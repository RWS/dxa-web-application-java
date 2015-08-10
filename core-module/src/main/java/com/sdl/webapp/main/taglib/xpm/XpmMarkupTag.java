package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public abstract class XpmMarkupTag extends XpmIfEnabledTag {

    protected abstract HtmlNode generateXpmMarkup();

    @Override
    public int doStartTag() throws JspException {
        if (isPreview()) {
            final HtmlNode xpmMarkup = generateXpmMarkup();
            if (xpmMarkup != null) {

                // TODO: Invoke Markup Decorators here...



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
}
