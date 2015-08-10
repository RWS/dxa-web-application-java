package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public abstract class HtmlNodeTag extends TagSupport {

    protected abstract HtmlNode generateNode();

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
