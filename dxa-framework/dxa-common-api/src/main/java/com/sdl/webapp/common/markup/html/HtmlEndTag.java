package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @dxa.publicApi
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlEndTag extends HtmlRenderable {

    private final String tagName;

    public HtmlEndTag(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String renderHtml() {
        return StringUtils.isEmpty(tagName) ? "" : "</" + tagName + '>';
    }
}
