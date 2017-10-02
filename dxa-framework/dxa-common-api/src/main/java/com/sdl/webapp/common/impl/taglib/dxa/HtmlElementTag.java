package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlEndTag;
import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public abstract class HtmlElementTag extends TagSupport {

    private HtmlElement element;

    /**
     * <p>generateElement.</p>
     *
     * @return a {@link HtmlElement} object.
     * @throws DxaException if any.
     */
    protected abstract HtmlElement generateElement() throws DxaException;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        try {
            this.element = generateElement();
        } catch (DxaException e) {
            throw new JspException(e);
        }

        if (element != null) {
            write(element.getStartTag().toHtml());
        }

        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        if (element != null) {
            for (HtmlNode node : element.getContent()) {
                write(node.toHtml());
            }

            final HtmlEndTag endTag = element.getEndTag();
            if (endTag != null) {
                write(endTag.toHtml());
            }
        }

        return EVAL_PAGE;
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
