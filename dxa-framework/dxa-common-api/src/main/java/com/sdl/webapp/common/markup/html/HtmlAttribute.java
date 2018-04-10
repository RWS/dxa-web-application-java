package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.web.util.HtmlUtils;

/**
 * @dxa.publicApi
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlAttribute extends HtmlRenderable {

    private final String name;

    private final String value;

    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String renderHtml() {
        return String.format("%s=\"%s\"", name, HtmlUtils.htmlEscape(value == null ? "" : value));
    }
}
