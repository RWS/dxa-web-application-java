package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlEndTag;
import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public abstract class HtmlElementTag extends TagSupport {

    private HtmlElement element;

    protected abstract HtmlElement generateElement();

    @Override
    public int doStartTag() throws JspException {
        this.element = generateElement();

        if (element != null) {
            write(element.getStartTag().toHtml());
        }

        return EVAL_BODY_INCLUDE;
    }

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
