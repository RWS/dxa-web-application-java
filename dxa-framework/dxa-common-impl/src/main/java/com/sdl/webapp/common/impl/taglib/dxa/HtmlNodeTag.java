package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.markup.html.HtmlNode;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * <p>Abstract HtmlNodeTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class HtmlNodeTag extends TagSupport {

    /**
     * <p>generateNode.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
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
