package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import lombok.Data;

/**
 * @dxa.publicApi
 */
@Data
public class RichTextFragmentImpl implements RichTextFragment {

    @JsonProperty("Html")
    private String html;

    public RichTextFragmentImpl(String html) {
        this.html = html;
    }

    @Override
    public HtmlElement toHtmlElement() {
        return HtmlBuilders.empty().withPureHtmlContent(html).build();
    }

    @Override
    public String toString() {
        return toHtmlElement().toHtml();
    }

    public String getHtml() {
        return toHtmlElement().toHtml();
    }
}
