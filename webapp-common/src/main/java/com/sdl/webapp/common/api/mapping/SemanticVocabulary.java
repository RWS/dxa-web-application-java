package com.sdl.webapp.common.api.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SemanticVocabulary {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Vocab")
    private String vocabulary;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }
}
