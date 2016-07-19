package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlCommentNode extends HtmlNode {

    private final String text;

    /**
     * <p>Constructor for HtmlCommentNode.</p>
     *
     * @param text a {@link java.lang.String} object.
     */
    public HtmlCommentNode(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return "<!-- " + text.replaceAll("<!--", "").replaceAll("-->", "") + " -->";
    }

}
