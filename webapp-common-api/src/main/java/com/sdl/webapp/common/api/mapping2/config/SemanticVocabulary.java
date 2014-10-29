package com.sdl.webapp.common.api.mapping2.config;

public final class SemanticVocabulary {

    private final String id;

    public SemanticVocabulary(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticVocabulary that = (SemanticVocabulary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
