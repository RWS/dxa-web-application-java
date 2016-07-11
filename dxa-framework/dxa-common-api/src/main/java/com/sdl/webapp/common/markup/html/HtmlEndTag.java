package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlEndTag extends HtmlRenderable {

    private final String tagName;

    /**
     * <p>Constructor for HtmlEndTag.</p>
     *
     * @param tagName a {@link java.lang.String} object.
     */
    public HtmlEndTag(String tagName) {
        this.tagName = tagName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return StringUtils.isEmpty(tagName) ? "" : "</" + tagName + '>';
    }
}
