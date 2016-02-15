package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>SemanticSchema class.</p>
 */
public final class SemanticSchema {

    private final long id;

    private final String rootElement;

    private final Set<EntitySemantics> entitySemantics;

    private final Map<FieldSemantics, SemanticField> semanticFields;
    private Localization localization;

    /**
     * <p>Constructor for SemanticSchema.</p>
     *
     * @param id              a long.
     * @param rootElement     a {@link java.lang.String} object.
     * @param entitySemantics a {@link java.util.Set} object.
     * @param semanticFields  a {@link java.util.Map} object.
     */
    public SemanticSchema(long id, String rootElement, Set<EntitySemantics> entitySemantics,
                          Map<FieldSemantics, SemanticField> semanticFields) {
        this.id = id;
        this.rootElement = rootElement;
        this.entitySemantics = ImmutableSet.copyOf(entitySemantics);
        this.semanticFields = ImmutableMap.copyOf(semanticFields);
    }

    /**
     * <p>Getter for the field <code>localization</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.localization.Localization} object.
     */
    public Localization getLocalization() {
        return localization;
    }

    /**
     * <p>Setter for the field <code>localization</code>.</p>
     *
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     */
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a long.
     */
    public long getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>rootElement</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRootElement() {
        return rootElement;
    }

    /**
     * <p>Getter for the field <code>entitySemantics</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<EntitySemantics> getEntitySemantics() {
        return entitySemantics;
    }

    /**
     * <p>Getter for the field <code>semanticFields</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<FieldSemantics, SemanticField> getSemanticFields() {
        return semanticFields;
    }

    /**
     * <p>getFullyQualifiedNames.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getFullyQualifiedNames() {
        final HashSet<String> result = new HashSet<>();
        for (EntitySemantics semantics : this.entitySemantics) {
            result.add(String.format("%s:%s", semantics.getVocabulary().getId(), semantics.getEntityName()));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticSchema that = (SemanticSchema) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rootElement, that.rootElement) &&
                Objects.equals(entitySemantics, that.entitySemantics) &&
                Objects.equals(semanticFields, that.semanticFields) &&
                Objects.equals(localization, that.localization);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id, rootElement, entitySemantics, semanticFields, localization);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SemanticSchema{" +
                "id=" + id +
                ", rootElement='" + rootElement + '\'' +
                ", entitySemantics=" + entitySemantics +
                ", semanticFields=" + semanticFields +
                '}';
    }
}
