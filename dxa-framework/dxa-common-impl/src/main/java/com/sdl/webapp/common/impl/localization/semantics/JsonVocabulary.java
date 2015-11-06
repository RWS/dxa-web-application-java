package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonVocabulary {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Vocab")
    private String vocab;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getVocab() {
        return vocab;
    }

    public void setVocab(String vocab) {
        this.vocab = vocab;
    }
}
