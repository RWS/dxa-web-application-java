package com.sdl.webapp.common.api.mapping.annotations;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;

import java.util.Objects;

public final class SemanticEntityInfo {

    public static final String DEFAULT_VOCABULARY = SemanticVocabulary.SDL_CORE;

    public static final String DEFAULT_PREFIX = "";

    private final String vocabulary;

    private final String entityName;

    private final String prefix;

    private final boolean public_;

    public SemanticEntityInfo(SemanticEntity annotation, Class<? extends EntityModel> entityClass) {
        String v = annotation.vocabulary();
        if (Strings.isNullOrEmpty(v)) {
            v = DEFAULT_VOCABULARY;
        }
        this.vocabulary = v;

        String n = annotation.entityName();
        if (Strings.isNullOrEmpty(n)) {
            n = annotation.value();
            if (Strings.isNullOrEmpty(n)) {
                n = entityClass.getSimpleName();
            }
        }
        this.entityName = n;

        String p = annotation.prefix();
        if (Strings.isNullOrEmpty(p)) {
            p = DEFAULT_PREFIX;
        }
        this.prefix = p;

        this.public_ = annotation.public_();
    }

    public SemanticEntityInfo(Class<? extends EntityModel> entityClass) {
        this.vocabulary = DEFAULT_VOCABULARY;
        this.entityName = entityClass.getSimpleName();
        this.prefix = DEFAULT_PREFIX;
        this.public_ = false;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isPublic() {
        return public_;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticEntityInfo that = (SemanticEntityInfo) o;
        return Objects.equals(public_, that.public_) &&
                Objects.equals(vocabulary, that.vocabulary) &&
                Objects.equals(entityName, that.entityName) &&
                Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName, prefix, public_);
    }

    @Override
    public String toString() {
        return "SemanticEntityInfo{" +
                "vocabulary='" + vocabulary + '\'' +
                ", entityName='" + entityName + '\'' +
                ", prefix='" + prefix + '\'' +
                ", public_=" + public_ +
                '}';
    }
}
