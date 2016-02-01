package com.sdl.webapp.common.api.mapping.semantic.annotations;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;

import java.util.Objects;

/**
 * <p>SemanticEntityInfo class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class SemanticEntityInfo {

    /**
     * Constant <code>DEFAULT_VOCABULARY="SemanticVocabulary.SDL_CORE"</code>
     */
    public static final String DEFAULT_VOCABULARY = SemanticVocabulary.SDL_CORE;

    /**
     * Constant <code>DEFAULT_PREFIX=""</code>
     */
    public static final String DEFAULT_PREFIX = "";

    private final String vocabulary;

    private final String entityName;

    private final String prefix;

    private final boolean public_;

    /**
     * <p>Constructor for SemanticEntityInfo.</p>
     *
     * @param annotation a {@link com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity} object.
     * @param entityClass a {@link java.lang.Class} object.
     */
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

    /**
     * <p>Constructor for SemanticEntityInfo.</p>
     *
     * @param entityClass a {@link java.lang.Class} object.
     */
    public SemanticEntityInfo(Class<? extends EntityModel> entityClass) {
        this.vocabulary = DEFAULT_VOCABULARY;
        this.entityName = entityClass.getSimpleName();
        this.prefix = DEFAULT_PREFIX;
        this.public_ = false;
    }

    /**
     * <p>Getter for the field <code>vocabulary</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVocabulary() {
        return vocabulary;
    }

    /**
     * <p>Getter for the field <code>entityName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>isPublic.</p>
     *
     * @return a boolean.
     */
    public boolean isPublic() {
        return public_;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName, prefix, public_);
    }

    /** {@inheritDoc} */
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
