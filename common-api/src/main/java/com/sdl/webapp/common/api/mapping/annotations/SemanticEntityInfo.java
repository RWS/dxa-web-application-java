package com.sdl.webapp.common.api.mapping.annotations;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.Entity;

public final class SemanticEntityInfo {

    public static final String DEFAULT_VOCABULARY = SemanticVocabulary.SDL_CORE;

    public static final String DEFAULT_PREFIX = "";

    private final String vocabulary;

    private final String entityName;

    private final String prefix;

    private final boolean public_;

    public SemanticEntityInfo(SemanticEntity annotation, Class<? extends Entity> entityClass) {
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

    public SemanticEntityInfo(Class<? extends Entity> entityClass) {
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
    public String toString() {
        return "SemanticEntityInfo{" +
                "vocabulary='" + vocabulary + '\'' +
                ", entityName='" + entityName + '\'' +
                ", prefix='" + prefix + '\'' +
                ", public_=" + public_ +
                '}';
    }
}
