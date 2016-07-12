package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>RichText class.</p>
 */
@Data
public class RichText {

    @JsonProperty("Fragments")
    private List<RichTextFragment> fragments;

    /**
     * <p>Constructor for RichText.</p>
     *
     * @param html a {@link java.lang.String} object.
     */
    public RichText(String html) {
        this.fragments = new LinkedList<>();
        if (html != null) {
            this.fragments.add(new RichTextFragmentImpl(html));
        }
    }

    /**
     * <p>Constructor for RichText.</p>
     *
     * @param fragments a {@link java.util.List} object.
     */
    public RichText(List<RichTextFragment> fragments) {
        this.fragments = (fragments != null) ? fragments : new LinkedList<RichTextFragment>();
    }

    /**
     * <p>isNullOrEmpty.</p>
     *
     * @param richText a {@link com.sdl.webapp.common.api.model.RichText} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    @JsonIgnore
    public static Boolean isNullOrEmpty(RichText richText) throws DxaException {
        return (richText == null) || richText.isEmpty();
    }

        /**
     * <p>isEmpty.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    @JsonIgnore
    public Boolean isEmpty() throws DxaException {
        return CollectionUtils.isEmpty(fragments) ||
                fragments.get(0) == null || StringUtils.isEmpty(fragments.get(0).toHtmlElement().toHtml());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (RichTextFragment frag : fragments) {
            result.append(frag);
        }
        return result.toString();
    }
}
