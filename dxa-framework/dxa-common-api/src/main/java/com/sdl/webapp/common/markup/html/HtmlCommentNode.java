package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @dxa.publicApi
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlCommentNode extends HtmlNode {

    private final String text;

    public HtmlCommentNode(String text) {
        this.text = text;
    }

    @Override
    public String renderHtml() {
        return "<!-- " + text.replaceAll("<!--", "").replaceAll("-->", "") + " -->";
    }

}
