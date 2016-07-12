package com.sdl.webapp.common.api.mapping.semantic.annotations;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * <p>SemanticEntityInfo class.</p>
 */
@Getter
@ToString
@EqualsAndHashCode
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

    public boolean isPublic() {
        return public_;
    }
}
