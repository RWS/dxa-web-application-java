package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class RichText {

    @JsonProperty("Fragments")
    private List<RichTextFragment> fragments;

    public List<RichTextFragment> getFragments() {
        return this.fragments;
    }

    private void setFragments(List<RichTextFragment> fragments) {
        this.fragments = fragments;
    }

    public RichText(String html) {
        this.fragments = new LinkedList<RichTextFragment>();
        this.fragments.add(new RichTextFragmentImpl(html));
    }

    public RichText(List<RichTextFragment> fragments) {
        this.fragments = (fragments != null) ? fragments : new LinkedList<RichTextFragment>();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (RichTextFragment frag : fragments) {
            result.append(frag.toHtml());
        }
        return result.toString();
    }

    public Boolean isEmpty() {
        if (!fragments.isEmpty() && fragments.size() > 0) {
            return fragments.get(0) == null || fragments.get(0).toHtml().isEmpty();
        }
        return true;
    }

    public static Boolean isNullOrEmpty(RichText richText) {
        return (richText == null) || richText.isEmpty();
    }

}
