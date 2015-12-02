package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.exceptions.DxaException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RichText {

    @JsonProperty("Fragments")
    private List<RichTextFragment> fragments;

    public RichText(String html) {
        this.fragments = new LinkedList<>();
        if (html != null) {
            this.fragments.add(new RichTextFragmentImpl(html));
        }
    }

    public RichText(List<RichTextFragment> fragments) {
        this.fragments = (fragments != null) ? fragments : new LinkedList<RichTextFragment>();
    }

    @JsonIgnore
    public static Boolean isNullOrEmpty(RichText richText) throws DxaException {
        return (richText == null) || richText.isEmpty();
    }

    public List<RichTextFragment> getFragments() {
        return this.fragments;
    }

    private void setFragments(List<RichTextFragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (RichTextFragment frag : fragments) {
            result.append(frag);
        }
        return result.toString();
    }

    @JsonIgnore
    public Boolean isEmpty() throws DxaException {
        return CollectionUtils.isEmpty(fragments) ||
                fragments.get(0) == null || StringUtils.isEmpty(fragments.get(0).toHtmlElement().toHtml());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RichText richText = (RichText) o;
        return Objects.equals(fragments, richText.fragments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragments);
    }
}
