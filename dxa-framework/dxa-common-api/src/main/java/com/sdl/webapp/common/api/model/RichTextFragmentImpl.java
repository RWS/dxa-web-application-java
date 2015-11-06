package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RichTextFragmentImpl implements RichTextFragment {

    @JsonProperty("Html")
    private String html;


    public RichTextFragmentImpl(String html) {
        this.html = html;
    }

    @Override
    public String toHtml() {
        return html;
    }

    @Override
    public String toString() {
        return toHtml();
    }

    public String getHtml() {
        return toHtml();
    }
}
