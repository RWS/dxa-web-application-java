package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>JsonVocabulary class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class JsonVocabulary {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Vocab")
    private String vocab;

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>vocab</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVocab() {
        return vocab;
    }

    /**
     * <p>Setter for the field <code>vocab</code>.</p>
     *
     * @param vocab a {@link java.lang.String} object.
     */
    public void setVocab(String vocab) {
        this.vocab = vocab;
    }
}
