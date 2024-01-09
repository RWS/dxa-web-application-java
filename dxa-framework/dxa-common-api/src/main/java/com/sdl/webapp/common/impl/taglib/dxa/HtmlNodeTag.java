package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.markup.html.HtmlNode;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public abstract class HtmlNodeTag extends TagSupport {

    /**
     * <p>generateNode.</p>
     *
     * @return a {@link HtmlNode} object.
     */
    protected abstract HtmlNode generateNode();

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        final HtmlNode node = generateNode();
        if (node != null) {
            write(node.toHtml());
        }
        return SKIP_BODY;
    }

    private void write(String text) throws JspException {
        final JspWriter out = pageContext.getOut();
        try {
            out.write(text);
        } catch (IOException e) {
            throw new JspException(e);
        }
    }
}
