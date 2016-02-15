package com.sdl.webapp.common.markup.html;

import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

/**
 * <p>HtmlAttribute class.</p>
 */
public final class HtmlAttribute extends HtmlRenderable {

    private final String name;
    private final String value;

    /**
     * <p>Constructor for HtmlAttribute.</p>
     *
     * @param name  a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return String.format("%s=\"%s\"", name, HtmlUtils.htmlEscape(value == null ? "" : value));
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlAttribute that = (HtmlAttribute) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
