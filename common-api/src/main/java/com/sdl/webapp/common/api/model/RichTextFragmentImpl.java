package com.sdl.webapp.common.api.model;

public class RichTextFragmentImpl implements RichTextFragment {

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

        return html;
    }
}
