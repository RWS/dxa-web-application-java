package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public abstract class XpmMarkupTag extends XpmIfEnabledTag {

    /**
     * <p>generateXpmMarkup.</p>
     *
     * @return a {@link HtmlNode} object.
     */
    protected abstract HtmlNode generateXpmMarkup();

    /**
     * {@inheritDoc}
     */
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
