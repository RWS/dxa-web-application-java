package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Semantic vocabulary.
 *
 * This contains semantic mapping configuration information that comes from the content provider. This information is
 * loaded as part of the configuration of a {@code Localization} by the {@code LocalizationFactory}.
 *
 * Semantic vocabularies are normally loaded from the configuration file: {@code /system/mappings/vocabularies.json}
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticVocabulary that = (SemanticVocabulary) o;

        if (vocabulary != null ? !vocabulary.equals(that.vocabulary) : that.vocabulary != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return vocabulary != null ? vocabulary.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SemanticVocabulary{" +
                "prefix='" + prefix + '\'' +
                ", vocabulary='" + vocabulary + '\'' +
                '}';
    }
}
