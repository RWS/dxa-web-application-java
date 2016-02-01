package com.sdl.webapp.common.markup.html;

import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

public final class HtmlAttribute extends HtmlRenderable {

    private final String name;
    private final String value;

    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String renderHtml() {
        return String.format("%s=\"%s\"", name, HtmlUtils.htmlEscape(value == null ? "" : value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlAttribute that = (HtmlAttribute) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
