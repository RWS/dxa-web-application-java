package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;

/**
 * <p>RichTextFragmentImpl class.</p>
 */
public class RichTextFragmentImpl implements RichTextFragment {

    @JsonProperty("Html")
    private String html;

    /**
     * <p>Constructor for RichTextFragmentImpl.</p>
     *
     * @param html a {@link java.lang.String} object.
     */
    public RichTextFragmentImpl(String html) {
        this.html = html;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement() {
        return HtmlBuilders.empty().withPureHtmlContent(html).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toHtmlElement().toHtml();
    }

    /**
     * <p>Getter for the field <code>html</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHtml() {
        return toHtmlElement().toHtml();
    }
}
